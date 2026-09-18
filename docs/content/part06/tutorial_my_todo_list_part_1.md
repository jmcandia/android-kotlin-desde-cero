# Tutorial: Mi lista de tareas (parte 1)

## Qué vamos a construir

Ya tienes todo lo necesario para armar tu primera interfaz de verdad: sabes anatomía de un proyecto Android, escribiste tu primer `@Composable`, conoces `Modifier`, sabes cómo Android organiza sus **recursos** (imágenes, ícono de la app, textos), los componentes básicos de Material 3 (`Text`, `Image`, `Button`, `Card`, `TextField`, `Checkbox`…), cómo estructurar una pantalla con `Scaffold` y layouts, y cómo manejar el **estado** con `remember` y *state hoisting*.

En este tutorial vas a **aplicar todo eso junto**, paso a paso, construyendo una aplicación pequeña pero completa: **una lista de tareas** (*to-do list*). Al terminar, tu app podrá:

- Mostrar un ícono propio y una barra superior con el título de la app.
- Escribir el texto de una tarea nueva en un campo de texto y agregarla con un botón.
- Ver la lista de tareas en pantalla, cada una en su propia tarjeta.
- Marcar una tarea como completada con un `Checkbox`.
- Eliminar una tarea.

> [!NOTE]Nota
> En esta primera parte usaremos el **tema por defecto** que Android Studio genera junto con el proyecto, sin personalizarlo, y la app tendrá una única pantalla (todavía no vimos navegación). En la segunda parte del tutorial, después del capítulo de Material 3 (tema, color y tipografía) y del de navegación, retomaremos este mismo proyecto para darle estilo propio y separar la lista en más de una pantalla.

## Paso 1: Crear el proyecto

Sigue los mismos pasos que en el capítulo 25:

1. Abre Android Studio y elige **New Project**.
2. Selecciona la plantilla **Empty Activity**.
3. Configúralo así:
    - **Name**: `MiListaDeTareas`
    - **Package name**: `com.ejemplo.milistadetareas`
    - **Minimum SDK**: API 24 o superior
    - **Build configuration language**: Kotlin DSL (`build.gradle.kts`)
4. Pulsa **Finish** y espera a que Gradle sincronice el proyecto.

Android Studio habrá generado un `MainActivity.kt` con un `setContent { }` que envuelve todo en un tema, algo como `MiListaDeTareasTheme { ... }`. **No lo toques todavía**: ese es justamente el tema por defecto que vamos a aprovechar.

## Paso 2: Recursos de la app: ícono y textos

Antes de escribir composables, dejemos preparados los **recursos** que la app va a necesitar, tal como viste en el capítulo de gestión de recursos.

### Un ícono propio

El proyecto ya trae un ícono por defecto en `res/mipmap/`, pero démosle uno propio a nuestra lista de tareas:

1. Haz clic derecho sobre `res` en el panel de proyecto y elige **New > Image Asset**.
2. En **Icon Type**, deja **Launcher Icons (Adaptive and Legacy)**.
3. En la capa **Foreground Layer**, cambia la fuente de **Image** a **Clip Art** y haz clic en el ícono para elegir uno del catálogo que trae Android Studio (por ejemplo, uno de una lista de tareas o un ✓). No necesitas ningún archivo propio: el asistente genera todas las densidades y ambas variantes (adaptativa y clásica) por ti.
4. Pulsa **Next** y luego **Finish**.

Ejecuta la app una vez y verifica, en el launcher del emulador o dispositivo, que el ícono cambió.

### Los textos, en `strings.xml`

En lugar de escribir los textos de la interfaz directamente en el código, decláralos en `res/values/strings.xml`. Abre ese archivo y agrega estas entradas, junto a la que ya generó Android Studio (`app_name`):

```xml
<resources>
    <string name="app_name">MiListaDeTareas</string>
    <string name="titulo_pantalla">Mi lista de tareas</string>
    <string name="etiqueta_nueva_tarea">Nueva tarea</string>
    <string name="boton_agregar">Agregar</string>
    <string name="descripcion_eliminar">Eliminar tarea</string>
    <string name="mensaje_lista_vacia">Todavía no agregaste ninguna tarea.</string>
</resources>
```

Vamos a ir usando cada uno de estos textos, con `stringResource`, a medida que construyamos la pantalla en los pasos siguientes.

## Paso 3: Modelar una tarea

Antes de dibujar nada, pensemos en los **datos**. Una tarea tiene un identificador único, un texto y un estado (si está completada o no). Como ya sabes de la Parte IV, esto es un caso perfecto para una `data class`:

```kotlin
import java.util.UUID

data class Tarea(
    val id: String = UUID.randomUUID().toString(),
    val texto: String,
    val completada: Boolean = false
)
```

Crea un archivo `Tarea.kt` en el mismo paquete que `MainActivity.kt` con este contenido.

> [!NOTE]Nota
> `UUID.randomUUID()` (de `java.util.UUID`, parte del propio JDK) genera un identificador prácticamente único cada vez que se llama. Como `id` tiene un valor por defecto, no necesitas indicarlo al crear una tarea (`Tarea(texto = "Comprar pan")` ya le asigna uno distinto); así, aunque dos tareas tengan el mismo texto, vas a poder distinguirlas por su `id`.

## Paso 4: El esqueleto de la pantalla

Empecemos por la estructura general. Abre `MainActivity.kt` y reemplaza el composable de ejemplo por uno propio, `ListaTareasScreen`, montado sobre un `Scaffold` con una barra superior:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen() {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.titulo_pantalla)) })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Aquí irán el formulario y la lista
        }
    }
}
```

Y actualiza `MainActivity` para mostrarlo dentro del tema por defecto del proyecto:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MiListaDeTareasTheme {
                ListaTareasScreen()
            }
        }
    }
}
```

Ejecuta la app: deberías ver una barra superior con el título "Mi lista de tareas" y una pantalla vacía debajo, ya con los colores y la tipografía que trae el tema por defecto.

## Paso 5: El estado, en el composable contenedor

La pantalla necesita recordar dos cosas:

- La **lista de tareas** actual: `List<Tarea>`.
- El **texto** que el usuario está escribiendo en el campo para una tarea nueva.

Ambos son datos que cambian con el tiempo y que, al cambiar, deben **recomponer** la pantalla: van declarados con `remember { mutableStateOf(...) }`, **juntos, al principio del cuerpo** de la función `ListaTareasScreen` —el composable que vas a crear en el paso siguiente—, antes de cualquier otra línea (antes del `Scaffold`):

```kotlin
var tareas by remember { mutableStateOf(listOf<Tarea>()) }
var textoNuevaTarea by remember { mutableStateOf("") }
```

Siguiendo el patrón de *state hoisting* que aprendiste, ese estado vive en `ListaTareasScreen`, el composable "contenedor" (con estado); más adelante se lo pasará como parámetros a composables "hijos" (sin estado) encargados solo de mostrar cosas y avisar de eventos. En el paso 6 vas a ver este mismo par de líneas ya integradas ahí, como las dos primeras instrucciones de la función.

Ahora sí, agreguemos el estado dentro de `ListaTareasScreen`:

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen() {
    var tareas by remember { mutableStateOf(listOf<Tarea>()) }
    var textoNuevaTarea by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.titulo_pantalla)) })
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            // Aquí irán el formulario y la lista
        }
    }
}
```

`tareas` guarda la lista completa; `textoNuevaTarea` guarda lo que el usuario va escribiendo antes de agregar una tarea. Ambos usan `remember` y la sintaxis `by`, tal como viste en el capítulo de estado.

> [!NOTE]Nota
> Este código necesita varias importaciones nuevas. Android Studio suele agregarlas solo (con **Alt+Enter** sobre cada símbolo subrayado, o el atajo **Optimize Imports**), pero si prefieres escribirlas a mano, estas son:
>
> ```kotlin
> import androidx.compose.foundation.layout.Column
> import androidx.compose.foundation.layout.padding
> import androidx.compose.material3.ExperimentalMaterial3Api
> import androidx.compose.material3.Scaffold
> import androidx.compose.material3.Text
> import androidx.compose.material3.TopAppBar
> import androidx.compose.runtime.Composable
> import androidx.compose.runtime.getValue
> import androidx.compose.runtime.mutableStateOf
> import androidx.compose.runtime.remember
> import androidx.compose.runtime.setValue
> import androidx.compose.ui.Modifier
> import androidx.compose.ui.res.stringResource
> ```
>
> Nota los dos casos que suelen sorprender: `getValue` y `setValue` son los que hacen posible la sintaxis `by` con `remember { mutableStateOf(...) }`, aunque no los uses directamente en tu código; y `stringResource` viene de `androidx.compose.ui.res`, no de `androidx.compose.material3`.

## Paso 6: El formulario para agregar tareas

Dentro del `Column`, agrega un `Row` con un `TextField` (que ocupe todo el espacio disponible) y un `Button` para confirmar:

```kotlin
Column(modifier = Modifier.padding(innerPadding)) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = textoNuevaTarea,
            onValueChange = { textoNuevaTarea = it },
            label = { Text(stringResource(R.string.etiqueta_nueva_tarea)) },
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = {
                if (textoNuevaTarea.isNotBlank()) {
                    tareas = tareas + Tarea(texto = textoNuevaTarea)
                    textoNuevaTarea = ""
                }
            },
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(stringResource(R.string.boton_agregar))
        }
    }

    // Aquí irá la lista
}
```

Repasemos lo que ya conoces en este bloque:

- `Modifier.weight(1f)` en el `TextField` hace que ocupe **todo el espacio sobrante** de la fila, empujando al botón hacia el borde (lo viste en el capítulo de layouts).
- El `onClick` del botón valida que el texto no esté en blanco (`isNotBlank()`, de la Parte III) y, si corresponde, **reemplaza** `tareas` por una lista nueva con la tarea agregada al final (`tareas + Tarea(...)`), aprovechando que los operadores sobre listas ya son viejos conocidos tuyos.
- Como `tareas` es un estado (`remember { mutableStateOf(...) }` con `by`), reasignarlo dispara la **recomposición**: la pantalla se entera del cambio sola.
- Al final, `textoNuevaTarea = ""` limpia el campo para la próxima tarea.

## Paso 7: Mostrar la lista de tareas

Ahora, justo después del `Row` del formulario, agrega un `LazyColumn` que recorra `tareas` y muestre cada una dentro de una `Card`:

```kotlin
LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
    items(tareas) { tarea ->
        Card(modifier = Modifier.padding(vertical = 4.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = { marcada ->
                        tareas = tareas.map {
                            if (it.id == tarea.id) it.copy(completada = marcada) else it
                        }
                    }
                )

                Text(
                    text = tarea.texto,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.descripcion_eliminar),
                    modifier = Modifier.clickable {
                        tareas = tareas.filter { it.id != tarea.id }
                    }
                )
            }
        }
    }
}
```

Aquí se combinan varias piezas que ya conoces:

- `LazyColumn` con `items(tareas)` recorre la lista y compone una `Card` por cada tarea, sin importar cuántas haya (capítulo de layouts y listas).
- Dentro de cada `Card`, un `Row` acomoda el `Checkbox`, el texto y el ícono uno al lado del otro; el `Text` usa `weight(1f)` para ocupar el espacio central.
- Al tocar el `Checkbox`, actualizamos `tareas` con `map` (Parte III): recorremos todas las tareas y **reemplazamos** solo la que tiene el mismo `id`, usando `copy()` para crear una nueva `Tarea` con `completada` invertido, en lugar de modificar la original.
- Al tocar el ícono de eliminar, `filter` (Parte III también) construye una nueva lista **sin** la tarea de ese `id`. El propio `Icon` reacciona al toque gracias al modificador `clickable`, que ya conoces del capítulo de fundamentos de Compose.

> [!WARNING]Advertencia
> Debes asegurarte de tener la importación clave: `import androidx.compose.foundation.lazy.items`. Sin ella, Kotlin no encuentra la función `items` que recibe una lista y, en su lugar, resuelve `items(count: Int, ...)`.

>[!NOTE]Nota
> Para las versiones más recientes de Android Studio, Google eliminó la biblioteca predeterminada de Material Icons para reducir el tamaño de las aplicaciones y acelerar los tiempos de compilación. Para incorporarla, edita el archivo `build.gradle.kts (Module :app)` con el siguiente código:
>
> ```kotlin
> implementation("androidx.compose.material:material-icons-core:1.7.8")
> ```
>
> También puedes agregar la dependencia desde **Project Structure**.

En ambos casos, fíjate en que **nunca modificamos una lista existente**: siempre creamos una lista nueva (con `+`, `map` o `filter`) y se la asignamos a `tareas`. Es el mismo hábito de trabajar con colecciones de forma inmutable que aprendiste en la Parte III, y es também lo que le permite a Compose darse cuenta de que el estado cambió y recomponer.

## Paso 8: Un mensaje cuando no hay tareas

Un último detalle: si `tareas` está vacía, tiene mejor aspecto mostrar un mensaje en lugar de una `LazyColumn` en blanco. Como los composables son solo funciones de Kotlin, puedes usar un `if` normal:

```kotlin
if (tareas.isEmpty()) {
    Text(
        text = stringResource(R.string.mensaje_lista_vacia),
        modifier = Modifier.padding(16.dp)
    )
} else {
    LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
        items(tareas) { tarea ->
            // el mismo contenido del paso anterior
        }
    }
}
```

## Paso 9: Ejecutar y probar

Ejecuta la app y prueba el flujo completo:

1. Escribe un texto en el campo y toca **Agregar**: la tarea debe aparecer en la lista y el campo debe quedar vacío.
2. Marca el `Checkbox` de una tarea: no cambia visualmente todavía (eso lo resolveremos con estilos en la segunda parte), pero si agregas un `println` o revisas con el depurador, verás que su `completada` cambia a `true`.
3. Toca el ícono de eliminar: la tarea debe desaparecer de la lista.
4. Deja el campo vacío y toca **Agregar**: no debería pasar nada, gracias a la validación con `isNotBlank()`.

## Resumen

En este tutorial construiste tu primera interfaz completa combinando lo aprendido en los capítulos 25 a 31:

- Creaste el proyecto y reutilizaste el **tema por defecto** que genera Android Studio, sin personalizarlo todavía.
- Le diste a la app un **ícono propio** con el asistente de Image Asset, y declaraste sus textos en `strings.xml`, leyéndolos con `stringResource` en lugar de escribirlos directamente en el código.
- Modelaste los datos de la app con una `data class` (`Tarea`), como en la Parte IV.
- Elevaste el estado (`tareas`, `textoNuevaTarea`) a un composable contenedor, siguiendo el patrón de *state hoisting*.
- Armaste el esqueleto de la pantalla con `Scaffold` y `TopAppBar`.
- Construiste un formulario con `TextField` y `Button`, y una lista con `LazyColumn`, `Card`, `Checkbox` e `Icon`.
- Actualizaste listas de forma **inmutable** con `+`, `map` y `filter`, reforzando lo que ya sabías de la Parte III.

Todavía quedan pendientes dos cosas: la app se ve con el estilo genérico del tema por defecto, y vive en una sola pantalla. En la **parte 2** de este tutorial, después de ver Material 3 (tema, color y tipografía) y la navegación con Navigation Compose, volveremos sobre este mismo proyecto para darle un estilo propio y separar la lista de tareas en más de una pantalla.
