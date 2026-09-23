# Tutorial: Mi lista de tareas (parte 3)

## Qué vamos a construir

Al terminar la segunda parte, **Mi lista de tareas** tenía dos pantallas, navegación y un tema propio. Pero su estado seguía viviendo en un composable: la lista de tareas estaba guardada con `remember` dentro de `App`. En el capítulo 27 adelantamos qué problema trae eso, y en los capítulos 38 a 40 viste cómo resolverlo.

En esta tercera parte vas a aplicar MVVM a la app sin cambiar lo que el usuario ve. Al terminar, tu app tendrá:

- un `TareasViewModel` que guarda las tareas y toda la lógica para modificarlas;
- un estado de pantalla, `TareasUiState`, expuesto como `StateFlow`;
- tareas que **sobreviven al giro de pantalla**;
- un resumen con la cantidad de tareas pendientes;
- un Snackbar con la acción **Deshacer** al eliminar una tarea.

> [!NOTE]Nota
> Todavía no habrá repositorio ni Hilt: el `ViewModel` guardará las tareas él mismo. Separar los datos en su propia capa es el tema de la cuarta parte, después de los capítulos 41 y 42.

## Paso 1: Comprobar el problema

Abre el proyecto `MiListaDeTareas` de la parte 2 y ejecútalo. Haz esta prueba:

1. Agrega dos o tres tareas.
2. Escribe un texto en el campo **Nueva tarea**, sin agregarlo.
3. Gira el emulador o el dispositivo.

El texto del campo se conserva, gracias a `rememberSaveable`. Pero **la lista de tareas desaparece**. Al girar, Android destruye la `Activity` y la vuelve a crear (capítulo 26); todo lo que estaba guardado con `remember` se pierde con ella.

Podrías cambiar el `remember` de la lista por `rememberSaveable`, pero sería un parche: el estado seguiría viviendo en la interfaz, junto con la lógica para modificarlo. La solución de fondo es moverlo a un `ViewModel`, que **vive más que la `Activity`**.

## Paso 2: Agregar las dependencias de `lifecycle`

Necesitas dos bibliotecas nuevas:

- `lifecycle-viewmodel-compose`, que trae la función `viewModel()` para obtener un `ViewModel` desde un composable;
- `lifecycle-runtime-compose`, que trae `collectAsStateWithLifecycle()`.

Abre el catálogo de versiones `gradle/libs.versions.toml`. La plantilla de Android Studio ya incluye otra biblioteca de la misma familia, `lifecycle-runtime-ktx`, con su versión en la sección `[versions]` (normalmente con el nombre `lifecycleRuntimeKtx`). Las bibliotecas de `lifecycle` se publican juntas y deben usar **la misma versión**, así que reutilízala. Agrega en `[libraries]`:

```toml
androidx-lifecycle-viewmodel-compose = { group = "androidx.lifecycle", name = "lifecycle-viewmodel-compose", version.ref = "lifecycleRuntimeKtx" }
androidx-lifecycle-runtime-compose = { group = "androidx.lifecycle", name = "lifecycle-runtime-compose", version.ref = "lifecycleRuntimeKtx" }
```

Y en `build.gradle.kts (Module :app)`, dentro de `dependencies`:

```kotlin
implementation(libs.androidx.lifecycle.viewmodel.compose)
implementation(libs.androidx.lifecycle.runtime.compose)
```

Pulsa **Sync Now**.

> [!TIP]Sugerencia
> En el código del curso (`code/todo-list-app/`), esa versión se llama simplemente `lifecycle`, porque la usan tres bibliotecas. Puedes renombrarla igual en tu proyecto; si lo haces, cambia también el `version.ref` de `lifecycle-runtime-ktx`.

## Paso 3: Los textos nuevos

Agrega en `strings.xml` los textos del resumen y del Snackbar:

```xml
<string name="resumen_pendientes">Pendientes: %1$d de %2$d</string>
<string name="mensaje_tarea_eliminada">Tarea eliminada</string>
<string name="accion_deshacer">Deshacer</string>
```

`%1$d` y `%2$d` son marcadores para dos números enteros: el primer y el segundo argumento que le pasarás a `stringResource`. Es el mismo formato del anexo D.

## Paso 4: El estado de la pantalla: `TareasUiState`

En el capítulo 39 viste un `UiState` como `sealed class`, con un estado distinto para *cargando*, *éxito* y *error*. Esta pantalla no carga nada: siempre muestra una lista (vacía o no) y, a veces, un mensaje. Cuando la pantalla muestra **varios datos a la vez**, el estado se modela mejor con una **`data class`**, con una propiedad por cada dato.

Crea el archivo `TareasUiState.kt` en el mismo paquete que `MainActivity.kt`:

```kotlin
data class TareasUiState(
    val tareas: List<Tarea> = emptyList(),
    val mensaje: Mensaje? = null
) {
    val pendientes: Int
        get() = tareas.count { !it.completada }
}

sealed interface Mensaje {
    data object TareaEliminada : Mensaje
}
```

- `tareas` es la lista que muestra la pantalla.
- `mensaje` es el evento pendiente del capítulo 40: `null` cuando no hay nada que avisar.
- `pendientes` es una propiedad **calculada**: no se guarda, se deriva de `tareas` cada vez que se consulta. Así nunca puede quedar desincronizada con la lista.

Los valores por defecto describen el estado inicial: una lista vacía y ningún mensaje.

## Paso 5: El `TareasViewModel`

Crea el archivo `TareasViewModel.kt`, también en el paquete principal. Empieza por el estado, con el patrón del capítulo 39: un `MutableStateFlow` privado y un `StateFlow` público.

```kotlin
class TareasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    private var ultimaEliminada: Tarea? = null

    // aquí van las acciones
}
```

`ultimaEliminada` guarda la tarea que se acaba de eliminar, para poder deshacerlo. No forma parte del `UiState` porque la pantalla nunca la muestra: es un detalle interno del `ViewModel`, y por eso es `private`.

Ahora agrega las acciones dentro de la clase. Las tres primeras son las que antes vivían en las lambdas de `App`:

```kotlin
fun agregarTarea(texto: String) {
    if (texto.isBlank()) return
    _uiState.update { estado ->
        estado.copy(tareas = estado.tareas + Tarea(texto = texto.trim()))
    }
}

fun cambiarCompletada(id: String, completada: Boolean) {
    _uiState.update { estado ->
        estado.copy(
            tareas = estado.tareas.map { tarea ->
                if (tarea.id == id) tarea.copy(completada = completada) else tarea
            }
        )
    }
}

fun eliminarTarea(id: String) {
    ultimaEliminada = _uiState.value.tareas.firstOrNull { it.id == id }
    _uiState.update { estado ->
        estado.copy(
            tareas = estado.tareas.filter { it.id != id },
            mensaje = Mensaje.TareaEliminada
        )
    }
}
```

La lógica es la misma que ya tenías (`+`, `map`, `filter` sobre listas inmutables), pero ahora cada cambio pasa por `update { }`, que recibe el estado actual y devuelve el nuevo con `copy`. Fíjate en dos detalles:

- La validación del texto en blanco se mueve al `ViewModel`. La regla «no se agregan tareas vacías» es lógica de la app, no de la interfaz.
- `eliminarTarea` quita la tarea **y** deja un mensaje pendiente en el estado, en un solo `update`.

Por último, las dos acciones que necesita el Snackbar:

```kotlin
fun deshacerEliminacion() {
    val tarea = ultimaEliminada ?: return
    ultimaEliminada = null
    _uiState.update { estado -> estado.copy(tareas = estado.tareas + tarea) }
}

fun mensajeMostrado() {
    _uiState.update { estado -> estado.copy(mensaje = null) }
}
```

`deshacerEliminacion` vuelve a agregar la tarea guardada (al final de la lista) y `mensajeMostrado` borra el mensaje cuando la interfaz avisa que ya lo mostró.

Los imports que necesita el archivo son:

```kotlin
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
```

> [!NOTE]Nota
> Ninguna de estas funciones usa `viewModelScope.launch`: todas cambian el estado al instante, sin esperar nada. Las coroutines aparecerán en la cuarta parte, cuando el `ViewModel` le pida los datos a un repositorio con funciones `suspend`.

## Paso 6: Separar el formulario

`ListaTareasScreen` va a recibir más cosas, así que conviene aligerarla antes. Saca el formulario a su propio composable, al final de `ListaTareasScreen.kt`:

```kotlin
@Composable
fun FormularioNuevaTarea(
    onAgregar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var texto by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text(stringResource(R.string.etiqueta_nueva_tarea)) },
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = {
                onAgregar(texto)
                texto = ""
            },
            enabled = texto.isNotBlank(),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(stringResource(R.string.boton_agregar))
        }
    }
}
```

El texto que se está escribiendo sigue siendo estado **local** de la interfaz, con `rememberSaveable`: nadie más lo necesita y no tiene sentido llevarlo al `ViewModel`. El botón ahora se deshabilita con `enabled` mientras el campo está vacío, como viste en el capítulo 34, en lugar de ignorar el toque en silencio.

## Paso 7: La pantalla recibe el `UiState`

Cambia la firma de `ListaTareasScreen`. En lugar de la lista suelta, recibe el `TareasUiState` completo, y suma dos acciones nuevas para el Snackbar:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    uiState: TareasUiState,
    onAgregar: (String) -> Unit,
    onCambiarCompletada: (String, Boolean) -> Unit,
    onEliminar: (String) -> Unit,
    onDeshacer: () -> Unit,
    onMensajeMostrado: () -> Unit,
    onVerDetalle: (String) -> Unit
) {
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

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.titulo_pantalla)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            FormularioNuevaTarea(onAgregar = onAgregar)

            Text(
                text = stringResource(
                    R.string.resumen_pendientes,
                    uiState.pendientes,
                    uiState.tareas.size
                ),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (uiState.tareas.isEmpty()) {
                Text(
                    text = stringResource(R.string.mensaje_lista_vacia),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(uiState.tareas, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            onCambiarCompletada = onCambiarCompletada,
                            onEliminar = onEliminar,
                            onVerDetalle = onVerDetalle
                        )
                    }
                }
            }
        }
    }
}
```

Es el código del capítulo 40, ahora en su lugar real:

- El `SnackbarHostState` se crea con `remember` y se entrega al `Scaffold` en `snackbarHost`.
- El `LaunchedEffect` tiene como clave `uiState.mensaje`. Cuando el `ViewModel` pone `TareaEliminada`, se lanza, muestra el Snackbar y espera. Si el usuario toca **Deshacer**, llama a `onDeshacer()`; en cualquier caso, termina con `onMensajeMostrado()`.
- Los textos se leen con `stringResource` **fuera** del `LaunchedEffect`, porque dentro de la coroutine no se pueden llamar funciones composables.
- El resumen usa `stringResource` con argumentos: `uiState.pendientes` reemplaza a `%1$d` y `uiState.tareas.size` a `%2$d`.

`TareaItem` no cambia.

La pantalla sigue siendo un composable **sin estado propio** (salvo el del Snackbar y el del formulario, que son de la interfaz): todo lo que muestra llega en `uiState`, y todo lo que hace el usuario sale por una función. Eso tiene una ventaja inmediata: puedes previsualizarla con datos inventados, sin `ViewModel`. Reemplaza la vista previa del archivo por esta:

```kotlin
@Preview(showBackground = true)
@Composable
fun ListaTareasScreenPreview() {
    MiListaDeTareasTheme {
        ListaTareasScreen(
            uiState = TareasUiState(
                tareas = listOf(
                    Tarea(texto = "Comprar pan"),
                    Tarea(texto = "Estudiar Compose", completada = true)
                )
            ),
            onAgregar = {},
            onCambiarCompletada = { _, _ -> },
            onEliminar = {},
            onDeshacer = {},
            onMensajeMostrado = {},
            onVerDetalle = {}
        )
    }
}
```

Estos son los imports nuevos de `ListaTareasScreen.kt`:

```kotlin
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.LaunchedEffect
```

## Paso 8: Conectar el `ViewModel` en `App`

Solo queda que `App` deje de guardar las tareas y se las pida al `ViewModel`. Reemplaza el contenido de `App.kt` por este:

```kotlin
@Composable
fun App(viewModel: TareasViewModel = viewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            ListaTareasScreen(
                uiState = uiState,
                onAgregar = viewModel::agregarTarea,
                onCambiarCompletada = viewModel::cambiarCompletada,
                onEliminar = viewModel::eliminarTarea,
                onDeshacer = viewModel::deshacerEliminacion,
                onMensajeMostrado = viewModel::mensajeMostrado,
                onVerDetalle = { id -> navController.navigate("detalle/$id") }
            )
        }
        composable(
            route = "detalle/{tareaId}",
            arguments = listOf(navArgument("tareaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getString("tareaId")
            val tarea = uiState.tareas.firstOrNull { it.id == tareaId }

            if (tarea != null) {
                DetalleTareaScreen(
                    tarea = tarea,
                    onVolver = { navController.popBackStack() }
                )
            } else {
                Text(stringResource(R.string.tarea_no_encontrada))
            }
        }
    }
}
```

Compara con la versión de la parte 2:

- `var tareas by remember { ... }` desapareció. En su lugar, `viewModel()` entrega el `TareasViewModel` (el mismo, aunque la `Activity` se recree) y `collectAsStateWithLifecycle()` convierte su `StateFlow` en estado de Compose.
- Las lambdas con la lógica de agregar, completar y eliminar se reemplazan por **referencias a funciones**, que viste en el capítulo 21. Allí usaste `Tipo::función`; aquí, a la izquierda de `::` va un **objeto concreto**: `viewModel::agregarTarea` apunta a la función `agregarTarea` de *ese* `ViewModel`, y equivale a escribir `{ texto -> viewModel.agregarTarea(texto) }`. Sus tipos coinciden con los parámetros de la pantalla: `agregarTarea` recibe un `String` y devuelve `Unit`, igual que `onAgregar`.
- La navegación sigue en el `NavHost`: el `ViewModel` no conoce el `NavController`.

Los imports nuevos son:

```kotlin
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
```

Ya puedes borrar los imports de `remember`, `mutableStateOf` y `setValue` que `App.kt` dejó de usar. `MainActivity` no cambia.

## Paso 9: Ejecutar y probar

Ejecuta la app y repite la prueba del paso 1:

1. Agrega tres tareas y marca una como completada. El resumen debe decir «Pendientes: 2 de 3».
2. Gira el dispositivo. **Las tareas siguen ahí**, igual que el texto del campo.
3. Abre el detalle de una tarea, gira el dispositivo y vuelve: la lista sigue completa.
4. Elimina una tarea. Aparece el Snackbar «Tarea eliminada»; toca **Deshacer** y la tarea vuelve a la lista.
5. Elimina otra tarea y deja que el Snackbar desaparezca solo: la tarea no vuelve.
6. Elimina una tarea y gira el dispositivo mientras el Snackbar está visible. Como el mensaje es parte del estado, la pantalla recreada lo encuentra pendiente y lo vuelve a mostrar.
7. Con el campo vacío, el botón **Agregar** aparece deshabilitado.

> [!WARNING]Advertencia
> El `ViewModel` sobrevive al giro de pantalla, pero no al cierre de la app. Si la cierras desde la lista de apps recientes, las tareas se pierden. Lo resolverás en la quinta parte, guardándolas en una base de datos con Room.

## Resumen

En esta tercera parte aplicaste MVVM a **Mi lista de tareas**:

- Comprobaste que el estado guardado con `remember` se pierde al girar la pantalla, porque la `Activity` se recrea.
- Modelaste el estado de la pantalla como una **`data class`** (`TareasUiState`), con una propiedad calculada (`pendientes`) y un mensaje pendiente (`mensaje: Mensaje?`).
- Creaste `TareasViewModel`, que expone el estado como `StateFlow` y lo modifica con **`update { }`** y `copy`. La lógica que antes vivía en las lambdas de `App` ahora está en el `ViewModel`.
- Mostraste un **Snackbar** con **Deshacer** a partir de un evento modelado como estado, con `LaunchedEffect`, tal como en el capítulo 40.
- Conectaste la interfaz con `viewModel()`, `collectAsStateWithLifecycle()` y **referencias a funciones**.
- Dejaste en la interfaz solo el estado que es de la interfaz: el texto del formulario y el `SnackbarHostState`.

El `ViewModel` todavía hace dos trabajos: decide qué mostrar **y** guarda los datos. En la cuarta parte, después de los capítulos 41 y 42, moverás los datos a un **repositorio** y dejarás que **Hilt** lo entregue al `ViewModel`.

El código completo de esta etapa está en `code/todo-list-app/etapas/etapa3/`.
