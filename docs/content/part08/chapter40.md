# Capítulo 40: Efectos y eventos puntuales: `LaunchedEffect`, Snackbar y navegación

## Introducción

Con el `ViewModel` del capítulo anterior, la interfaz muestra un estado y avisa de los eventos del usuario. Pero hay cosas que no encajan del todo en «mostrar un estado»:

- Después de eliminar una tarea, aparece un mensaje abajo durante unos segundos: «Tarea eliminada · Deshacer».
- Después de guardar un formulario, la app vuelve sola a la pantalla anterior.

Ninguna de las dos es algo que se **dibuja** mientras dure un estado: son cosas que **ocurren una vez**, como consecuencia de algo. En este capítulo aprenderás a ejecutarlas de forma segura desde Compose con **`LaunchedEffect`**, a mostrar mensajes con un **Snackbar** y a resolver los eventos que nacen en el `ViewModel`, incluida la navegación después de una acción.

## Por qué no directamente en el composable

En el capítulo de estado viste una regla: un composable puede ejecutarse muchas veces (una por cada recomposición), así que no debe tener **efectos secundarios**. Mira qué pasaría si la ignoraras:

```kotlin
@Composable
fun PantallaTareas(viewModel: TareasViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    registrarVisita()   // ¡Mal! Se ejecuta en cada recomposición

    ListaTareas(uiState.tareas)
}
```

Cada vez que cambia el estado, Compose vuelve a ejecutar `PantallaTareas`, y `registrarVisita()` se llamaría una y otra vez. Y si la acción fuera una función `suspend`, como mostrar un Snackbar, ni siquiera compilaría: un composable no es una coroutine.

Compose ofrece **APIs de efectos** para esto. Son la forma controlada de decir «ejecuta este código cuando pase X, y no en cada recomposición».

## `LaunchedEffect`: una coroutine atada a la pantalla

`LaunchedEffect` lanza una **coroutine** cuando el composable aparece en pantalla:

```kotlin
@Composable
fun PantallaTareas(viewModel: TareasViewModel) {
    LaunchedEffect(Unit) {
        registrarVisita()   // una sola vez, al entrar
    }
    // ...
}
```

Su comportamiento se resume en tres reglas:

1. **Se lanza** cuando el composable entra en la composición.
2. **Se cancela** cuando el composable sale de ella (por ejemplo, al navegar a otra pantalla). Es la misma cancelación que viste en el capítulo de coroutines.
3. **Se reinicia** cuando cambia su **clave**, el argumento entre paréntesis: cancela la coroutine anterior y lanza una nueva.

La clave es lo que decide **cuándo** se repite el efecto. Con `Unit`, que nunca cambia, se ejecuta una sola vez. Con un valor que sí cambia, se ejecuta cada vez que cambia:

```kotlin
@Composable
fun DetalleTarea(tareaId: String, onCargar: (String) -> Unit) {
    LaunchedEffect(tareaId) {
        onCargar(tareaId)   // se repite solo si cambia la tarea mostrada
    }
    // ...
}
```

> [!NOTE]Nota
> La carga inicial de los datos de una pantalla normalmente **no** va en un `LaunchedEffect`, sino en el bloque `init` del `ViewModel`: así se carga una sola vez, aunque la pantalla se recree al girar el dispositivo. Reserva `LaunchedEffect` para lo que depende de la interfaz, como mostrar un mensaje o navegar.

## Mostrar un Snackbar

Un **Snackbar** es el mensaje breve que aparece en la parte inferior de la pantalla, opcionalmente con una acción («Deshacer»). En Material 3 se necesitan tres piezas:

- Un **`SnackbarHostState`**, que controla qué mensaje se muestra. Se crea con `remember` para que sobreviva a las recomposiciones.
- Un **`SnackbarHost`**, el lugar donde se dibuja, que se entrega al `Scaffold` en su parámetro `snackbarHost`.
- Una llamada a **`showSnackbar(...)`**, que muestra el mensaje.

```kotlin
val snackbarHostState = remember { SnackbarHostState() }

Scaffold(
    snackbarHost = { SnackbarHost(snackbarHostState) }
) { innerPadding ->
    // contenido de la pantalla
}
```

`showSnackbar` es una función **`suspend`**: se queda esperando hasta que el mensaje desaparece o el usuario toca la acción, y devuelve qué pasó:

```kotlin
val resultado = snackbarHostState.showSnackbar(
    message = "Tarea eliminada",
    actionLabel = "Deshacer"
)
if (resultado == SnackbarResult.ActionPerformed) {
    // el usuario tocó «Deshacer»
}
```

Por ser `suspend`, necesita una coroutine. Hay dos formas de conseguirla, según **qué** dispara el mensaje.

### Cuando lo dispara un toque: `rememberCoroutineScope`

Si el mensaje responde directamente a un toque en la interfaz, y no interviene el `ViewModel`, lanzas la coroutine desde el `onClick` con un *scope* ligado a la pantalla:

```kotlin
val scope = rememberCoroutineScope()

Button(onClick = {
    scope.launch { snackbarHostState.showSnackbar("Copiado al portapapeles") }
}) {
    Text("Copiar")
}
```

`rememberCoroutineScope()` devuelve un *scope* que se cancela solo cuando el composable sale de la pantalla. Úsalo dentro de **callbacks** (`onClick`, `onValueChange`); nunca llames a `scope.launch` directamente en el cuerpo del composable, por la misma razón que viste al principio del capítulo.

### Cuando lo decide el `ViewModel`: `LaunchedEffect`

En el caso de la tarea eliminada, quien sabe que hay que mostrar el mensaje es el `ViewModel`: él eliminó la tarea y él sabe deshacerlo. Ese es un **evento que nace en el `ViewModel`**, y merece una sección propia.

## Eventos del `ViewModel`: modelarlos como estado

¿Cómo le dice el `ViewModel` a la interfaz «muestra un mensaje»? La forma recomendada por la guía de arquitectura de Android es tratarlo como **parte del estado**: el mensaje pendiente es un dato más del `UiState`.

```kotlin
data class TareasUiState(
    val tareas: List<Tarea> = emptyList(),
    val mensaje: Mensaje? = null   // null: no hay nada que avisar
)

sealed interface Mensaje {
    data object TareaEliminada : Mensaje
}
```

El `ViewModel` **pone** el mensaje cuando ocurre algo y lo **quita** cuando la interfaz avisa que ya lo mostró:

```kotlin
class TareasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    private var ultimaEliminada: Tarea? = null

    fun eliminarTarea(id: String) {
        ultimaEliminada = _uiState.value.tareas.firstOrNull { it.id == id }
        _uiState.update { estado ->
            estado.copy(
                tareas = estado.tareas.filter { it.id != id },
                mensaje = Mensaje.TareaEliminada
            )
        }
    }

    fun deshacerEliminacion() {
        val tarea = ultimaEliminada ?: return
        ultimaEliminada = null
        _uiState.update { estado -> estado.copy(tareas = estado.tareas + tarea) }
    }

    fun mensajeMostrado() {
        _uiState.update { estado -> estado.copy(mensaje = null) }
    }
}
```

`update { }` es la forma segura de modificar un `MutableStateFlow` a partir de su valor actual: recibe el estado actual y devuelve el nuevo, normalmente con `copy`.

La interfaz reacciona con un `LaunchedEffect` cuya **clave es el mensaje**:

```kotlin
val snackbarHostState = remember { SnackbarHostState() }
val textoTareaEliminada = stringResource(R.string.mensaje_tarea_eliminada)
val textoDeshacer = stringResource(R.string.accion_deshacer)

LaunchedEffect(uiState.mensaje) {
    if (uiState.mensaje == Mensaje.TareaEliminada) {
        val resultado = snackbarHostState.showSnackbar(
            message = textoTareaEliminada,
            actionLabel = textoDeshacer
        )
        if (resultado == SnackbarResult.ActionPerformed) {
            onDeshacer()
        }
        onMensajeMostrado()
    }
}
```

Paso a paso:

1. El usuario elimina una tarea. El `ViewModel` quita la tarea y pone `mensaje = TareaEliminada`.
2. La clave del `LaunchedEffect` cambió, así que se lanza. Muestra el Snackbar y espera.
3. Si el usuario toca «Deshacer», se llama a `onDeshacer()`.
4. En cualquier caso, `onMensajeMostrado()` avisa al `ViewModel`, que vuelve a poner `mensaje = null`.

Fíjate en que los textos se obtienen con `stringResource` **fuera** del `LaunchedEffect`: `stringResource` es una función composable y no se puede llamar dentro de la coroutine.

¿Por qué tanto trabajo, en lugar de que el `ViewModel` «dispare» el mensaje? Porque, al ser estado, **no se pierde**. Si el usuario gira el teléfono justo cuando se elimina la tarea, la pantalla se recrea, vuelve a leer el estado, encuentra el mensaje pendiente y lo muestra. Y como `onMensajeMostrado()` lo borra, tampoco se muestra dos veces.

> [!NOTE]¿Y el `SharedFlow`?
> En el capítulo 24 viste que un `SharedFlow` sirve para eventos de una sola vez, y es una alternativa que encontrarás en muchos proyectos: el `ViewModel` emite el evento y la interfaz lo recolecta. Su problema es que, si nadie está recolectando en ese instante (por ejemplo, durante la rotación), el evento **se pierde**. Por eso, en este curso los eventos que nacen en el `ViewModel` se modelan como estado. `SharedFlow` sigue siendo útil para eventos entre capas que no dependen de una pantalla visible.

## Navegar después de una acción

La navegación después de guardar sigue la misma idea. El `ViewModel` no navega (no conoce el `NavController`): solo registra en el estado que la acción terminó bien.

```kotlin
data class FormularioUiState(
    val texto: String = "",
    val guardando: Boolean = false,
    val guardado: Boolean = false
)
```

Cuando el guardado termina, el `ViewModel` pone `guardado = true`. La pantalla, que recibe la navegación como una función (la buena práctica del capítulo de navegación), reacciona:

```kotlin
@Composable
fun FormularioScreen(
    uiState: FormularioUiState,
    onGuardar: () -> Unit,
    onGuardado: () -> Unit      // por ejemplo, { navController.popBackStack() }
) {
    LaunchedEffect(uiState.guardado) {
        if (uiState.guardado) onGuardado()
    }
    // campos y botón «Guardar», que llama a onGuardar
}
```

Así, el `ViewModel` sigue sin saber nada de pantallas, la pantalla sigue sin saber nada del `NavController`, y quien conecta las dos es el `NavHost`, que es el único que conoce la navegación.

## Resumen

- Un composable no debe tener efectos secundarios, porque se ejecuta en cada recomposición. Para ejecutar código **una vez**, como consecuencia de algo, se usan las **APIs de efectos**.
- **`LaunchedEffect(clave) { }`** lanza una coroutine cuando el composable entra en pantalla, la cancela cuando sale y la reinicia cuando cambia la clave.
- Un **Snackbar** necesita un `SnackbarHostState` (con `remember`), un `SnackbarHost` en el `Scaffold` y una llamada a `showSnackbar`, que es `suspend` y devuelve si el usuario tocó la acción.
- Si el mensaje lo dispara un toque, usa **`rememberCoroutineScope()`** dentro del callback. Si lo decide el `ViewModel`, usa un `LaunchedEffect`.
- Los eventos que nacen en el `ViewModel` se **modelan como estado** (`mensaje: Mensaje?`, `guardado: Boolean`): la interfaz los atiende con un `LaunchedEffect` y avisa cuando terminó, para que el `ViewModel` los borre. Así no se pierden al girar la pantalla.
- El `ViewModel` no navega: marca que la acción terminó, y la pantalla llama a la función de navegación que recibió.

En el tutorial que sigue aplicarás todo esto a **Mi lista de tareas**: moverás su estado a un `TareasViewModel`, las tareas sobrevivirán al giro de pantalla y eliminar una tarea mostrará un Snackbar con «Deshacer».
