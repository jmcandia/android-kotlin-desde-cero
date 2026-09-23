# Capítulo 30: Estructura de pantalla: `Scaffold`, layouts y `LazyColumn`

## Introducción

En el capítulo anterior conociste los componentes de Material 3, pero solo los has mostrado de a uno. Una pantalla real combina **muchos** elementos: una barra superior, textos, imágenes, botones, unos debajo de otros o en fila. En este capítulo aprenderás a montar el **esqueleto** de una pantalla con `Scaffold`, a **organizar** su contenido con los layouts de Compose —`Column`, `Row` y `Box`—, a controlar cómo se distribuyen y alinean, y a mostrar **listas** de forma eficiente con `LazyColumn`, imprescindible para presentar listas largas de datos.

## `Scaffold`: el esqueleto de una pantalla

La mayoría de las pantallas comparten una estructura: una barra arriba, el contenido en el medio, quizás una barra abajo o un botón flotante. En lugar de armar eso a mano, Material ofrece el **`Scaffold`** ("andamio"), un composable que provee **espacios** (*slots*) para cada una de esas partes:

![Scaffold](../../assets/images/chapter30/scaffold.svg)

Un uso típico, con una barra superior y el contenido:

```kotlin
Scaffold(
    topBar = {
        TopAppBar(title = { Text("Mi aplicación") })
    }
) { innerPadding ->
    Column(modifier = Modifier.padding(innerPadding)) {
        // el contenido de la pantalla
    }
}
```

Fíjate en el `innerPadding`: el `Scaffold` te entrega el espacio que ocupan las barras para que **apartes** el contenido y no quede tapado por ellas. Por eso se lo pasas como `padding` al composable de contenido. (Ya habías visto este patrón en el `MainActivity` que generó Android Studio.) Ese contenido normalmente es un layout —como los que verás a continuación— o una lista con `LazyColumn`, que cerrará el capítulo.

> [!NOTE]Nota
> Algunos componentes de Material 3, como `TopAppBar`, están marcados todavía como *experimentales*, lo que obliga a añadir la anotación `@OptIn(ExperimentalMaterial3Api::class)` sobre la función que los usa. Android Studio te avisa y la agrega por ti.

## Diseñar por jerarquías

Una pantalla mantenible no se construye como una única función enorme. Conviene organizarla en una jerarquía: una raíz que decide la estructura general, secciones que agrupan contenido relacionado y componentes pequeños que muestran un dato o emiten un evento.

```kotlin
@Composable
fun ContactosScreen() {
    Scaffold(
        topBar = { ContactosTopBar() },
        bottomBar = { ContactosBottomBar() }
    ) { innerPadding ->
        ContactosContent(modifier = Modifier.padding(innerPadding))
    }
}

@Composable
private fun ContactosContent(modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier) {
        item { ContactosHeader() }
        items(contactos, key = { it.id }) { contacto ->
            ContactoItem(contacto = contacto)
        }
    }
}
```

Esta separación ayuda a que cada pieza tenga una responsabilidad clara y permite previsualizar una sección con datos de ejemplo. La raíz conoce la estructura de la pantalla; los hijos reciben datos y callbacks. El estado compartido no debe esconderse en cada fila, sino vivir en el nivel más bajo que necesite coordinarlo, o en el `ViewModel` cuando la pantalla tenga lógica de negocio.

## Adaptar el diseño al espacio disponible

Una interfaz móvil no debe limitarse a escalar. En una pantalla estrecha puede mostrar una sola columna y navegación inferior; en una pantalla ancha puede mostrar una lista junto a un detalle y una navegación lateral. El contenido es el mismo, pero la **estructura** cambia.

Las **Window Size Classes** clasifican el espacio disponible de la ventana en categorías estables, en lugar de tomar decisiones con muchos valores concretos:

- **Compact**: normalmente una sola columna y acciones esenciales.
- **Medium**: más espacio para separar secciones o mostrar dos columnas sencillas.
- **Expanded**: una navegación lateral, paneles simultáneos o una lista y su detalle.

Estas clases describen el espacio disponible en **dp**, no el tamaño físico del dispositivo. Para el ancho de la ventana, la guía de diseño de Android usa habitualmente estos puntos de corte:

| Clase de ancho | Ancho disponible | Decisiones habituales |
| :--- | :--- | :--- |
| `Compact` | Menos de `600.dp` | Una columna, `NavigationBar` y pantallas completas. |
| `Medium` | Desde `600.dp` hasta menos de `840.dp` | Dos columnas moderadas, `NavigationRail` o una lista con un panel secundario. |
| `Expanded` | `840.dp` o más | Navegación lateral, lista y detalle simultáneos o varias regiones persistentes. |

Los límites no son una clasificación de teléfonos concretos. Un móvil puede pasar de `Compact` a `Medium` al girarse, una tableta puede estar en `Expanded` y una ventana de escritorio puede cambiar de clase al redimensionarse. Por eso se decide a partir del espacio real que recibe la app.

También existe una **clase de altura** con las mismas categorías. Es útil cuando una pantalla tiene poco espacio vertical: en altura `Compact` puedes reducir espacios, agrupar acciones o permitir desplazamiento; en altura `Medium` puedes mostrar más contenido; en altura `Expanded` puedes distribuir las secciones con mayor comodidad. No conviene usar la altura para decidir si una navegación debe ser inferior o lateral: esa decisión suele depender principalmente del ancho.

La clase tampoco indica por sí sola qué diseño debes construir. Es un punto de partida para una decisión de producto:

| Necesidad | `Compact` | `Medium` | `Expanded` |
| :--- | :--- | :--- | :--- |
| Lista y detalle | Navegar a otra pantalla | Mostrar una lista amplia y un detalle opcional | Mantener lista y detalle visibles a la vez |
| Navegación | `NavigationBar` | `NavigationRail` o barra inferior si sigue siendo cómoda | `NavigationDrawer` permanente o rail con etiquetas |
| Formulario | Una columna y scroll | Dos grupos de campos si mejora la lectura | Separar datos principales, ayuda y resumen |
| Acciones | Mostrar las esenciales | Agrupar acciones relacionadas | Mantener acciones frecuentes visibles y el resto en menú |

La clasificación debe basarse en el ancho de la ventana, no en el modelo del teléfono. Así la misma decisión funciona en un móvil girado, una tableta, una ventana redimensionada o un escritorio.

### Obtener la clase desde un composable

También puedes encapsular el acceso a la actividad actual en una función composable. Este patrón permite que una pantalla obtenga su `WindowSizeClass` sin recibir la actividad como parámetro:

```kotlin
import androidx.activity.compose.LocalActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun getWindowSizeClass(): WindowSizeClass {
    return calculateWindowSizeClass(LocalActivity.current as android.app.Activity)
}
```

La pantalla puede consultar la clase y delegar la composición a una implementación específica para cada ancho. El `when` es exhaustivo: cada valor de `WindowWidthSizeClass` tiene una estructura explícita.

```kotlin
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass

@Composable
fun HomeScreen() {
    val windowSizeClass = getWindowSizeClass()
    when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> HomeScreenCompact()
        WindowWidthSizeClass.Medium -> HomeScreenMedium()
        WindowWidthSizeClass.Expanded -> HomeScreenExpanded()
    }
}

@Composable
fun HomeScreenCompact() {
    // Implement the compact screen layout
}

@Composable
fun HomeScreenMedium() {
    // Implement the medium screen layout
}

@Composable
fun HomeScreenExpanded() {
    // Implement the expanded screen layout
}
```

En este caso, `HomeScreen` decide **qué estructura** mostrar, mientras que `HomeScreenCompact`, `HomeScreenMedium` y `HomeScreenExpanded` implementan cada composición. Las tres variantes pueden compartir modelos, estado y callbacks; lo que cambia es la disposición de los elementos. `LocalActivity.current` y `calculateWindowSizeClass` dependen de las versiones de `activity-compose` y Material 3 del proyecto, por lo que Android Studio puede solicitar la actualización de la dependencia o del import.

Con la biblioteca `androidx.compose.material3:material3-window-size-class`, una actividad puede obtener la clase y pasar solo una decisión de diseño al composable:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            MiAppTheme {
                ContactosApp(windowWidth = windowSizeClass.widthSizeClass)
            }
        }
    }
}
```

La pantalla decide su composición, no cada componente individual:

```kotlin
@Composable
fun ContactosApp(windowWidth: WindowWidthSizeClass) {
    when (windowWidth) {
        WindowWidthSizeClass.Expanded -> ContactosTwoPane()
        WindowWidthSizeClass.Medium -> ContactosWithRail()
        WindowWidthSizeClass.Compact -> ContactosSinglePane()
    }
}
```

El diseño puede compartir los mismos composables internos y cambiar solo el contenedor:

```kotlin
@Composable
fun ContactosLayout(windowWidth: WindowWidthSizeClass) {
    when (windowWidth) {
        WindowWidthSizeClass.Compact -> {
            Scaffold(
                bottomBar = { ContactosBottomBar() }
            ) { padding ->
                ContactosList(modifier = Modifier.padding(padding))
            }
        }

        WindowWidthSizeClass.Medium -> {
            Row {
                ContactosRail()
                ContactosList(modifier = Modifier.weight(1f))
            }
        }

        WindowWidthSizeClass.Expanded -> {
            Row {
                ContactosNavigationDrawer()
                ContactosList(modifier = Modifier.weight(0.4f))
                ContactoDetail(modifier = Modifier.weight(0.6f))
            }
        }
    }
}
```

En este ejemplo, la lista y el detalle siguen siendo las mismas funciones y el estado de selección sigue teniendo una sola fuente de verdad. Solo cambia el contenedor que las presenta. Si el ancho cambia mientras la app está abierta, Compose recompone esta decisión y la navegación debe conservar el destino y el elemento seleccionado siempre que la nueva estructura pueda mostrarlos.

El nombre de algunas APIs puede variar según la versión de Compose. El principio no cambia: medir el espacio disponible, elegir una estructura y dejar que los componentes internos ocupen el espacio asignado con `fillMaxWidth`, `weight`, `LazyColumn` y restricciones razonables. No conviene mantener dos copias independientes de la navegación o del estado solo porque cambia la disposición visual.

> [!IMPORTANT]
> Una adaptación correcta conserva la jerarquía y la tarea principal. No ocultes información esencial en una pantalla ancha ni fuerces una fila de controles que desborde en una pantalla estrecha; cambia la composición, no el significado.

## El problema: los elementos se superponen

Si colocas dos composables juntos sin más, Compose los dibuja en el **mismo lugar**, uno encima del otro:

```kotlin
@Composable
fun Pantalla() {
    Text("Primero")
    Text("Segundo") // ¡se dibuja encima del anterior!
}
```

Para arreglarlo, necesitas un **layout**: un composable cuyo trabajo es **organizar** a sus hijos. Compose ofrece tres básicos, que resuelven las tres formas fundamentales de disponer elementos:

![Layout](../../assets/images/chapter30/layout-column-row-box.svg)

## `Column`: en vertical

Un `Column` organiza a sus hijos **en vertical**, uno debajo del otro:

```kotlin
Column {
    Text("Primero")
    Text("Segundo")
    Text("Tercero")
}
```

Ahora los tres textos aparecen apilados de arriba abajo, en el orden en que los escribiste.

## `Row`: en horizontal

Un `Row` organiza a sus hijos **en horizontal**, uno al lado del otro:

```kotlin
Row {
    Text("Izquierda")
    Text("Centro")
    Text("Derecha")
}
```

Es idéntico a `Column`, pero en el eje horizontal.

## `Box`: superponer elementos

Un `Box` **apila** a sus hijos, uno **encima** de otro. Es útil para superponer cosas: un texto sobre una imagen, una insignia sobre un ícono, etcétera.

```kotlin
Box {
    Text("Fondo")
    Text("Encima") // se dibuja sobre el anterior
}
```

Combinando estos tres layouts (y anidándolos unos dentro de otros) puedes construir prácticamente cualquier pantalla.

## Distribución y alineación

Dentro de un `Column` o un `Row`, a menudo querrás controlar **cómo se reparten** los hijos y **cómo se alinean**. Para eso, estos layouts reciben dos parámetros. La clave es distinguir sus dos ejes:

- En un `Column`, el eje principal es **vertical**. Controlas la distribución vertical con `verticalArrangement` y la alineación horizontal con `horizontalAlignment`.
- En un `Row`, el eje principal es **horizontal**. Controlas la distribución horizontal con `horizontalArrangement` y la alineación vertical con `verticalAlignment`.

Por ejemplo, un `Column` que separa sus hijos con espacio y los centra horizontalmente:

```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Primero")
    Text("Segundo")
}
```

Algunos valores útiles de `Arrangement` son `spacedBy(...)` (un espacio fijo entre elementos), `SpaceBetween` (reparte el espacio sobrante entre ellos) y `Center` (los agrupa al centro). Y de `Alignment`, `Start`, `CenterHorizontally` y `End` (o `Top`, `CenterVertically` y `Bottom` en un `Row`).

## El modificador `weight`

En el capítulo de fundamentos mencionamos que hay modificadores que solo funcionan dentro de ciertos layouts. `weight` es el más importante: dentro de un `Row` o un `Column`, reparte el **espacio disponible** entre los hijos de forma proporcional.

```kotlin
Row {
    Text("Izquierda", modifier = Modifier.weight(1f))
    Text("Derecha", modifier = Modifier.weight(1f))
}
```

Aquí ambos textos reciben el mismo peso (`1f`), así que se reparten el ancho **a la mitad**. Si a uno le dieras `weight(2f)` y al otro `weight(1f)`, el primero ocuparía el doble de espacio que el segundo.

## `LazyColumn`: listas eficientes

Un `Column` dibuja **todos** sus hijos de una vez. Eso está bien para unos pocos elementos, pero ¿y si tienes una lista de cientos o miles de elementos? Dibujarlos todos a la vez sería lento y desperdiciaría memoria, sobre todo porque la mayoría ni siquiera caben en la pantalla.

Para eso está el **`LazyColumn`**: una columna con desplazamiento (*scroll*) que solo compone los elementos **visibles** en cada momento, y los va reutilizando a medida que te desplazas. Así puede mostrar listas enormes sin problemas.

En vez de escribir cada hijo a mano, le pasas la lista con la función `items`:

```kotlin
val nombres = listOf("Ana", "Diego", "Elena")

LazyColumn {
    items(nombres) { nombre ->
        Text(nombre)
    }
}
```

`items(nombres)` recorre la lista y, por cada elemento, ejecuta la lambda que describe cómo mostrarlo (aquí, un `Text` con su nombre). El desplazamiento funciona automáticamente. También existe `LazyRow`, su equivalente horizontal.

> [!NOTE]Nota
> Si vienes del desarrollo Android tradicional, `LazyColumn` cumple el papel del antiguo `RecyclerView`, pero con muchísimo menos código: no necesitas adaptadores ni *view holders*.

Este es, precisamente, el componente que sueles poner dentro del contenido de un `Scaffold` para mostrar listas largas de datos: un `LazyColumn` con un `items` que recorre los elementos, recibiendo el `innerPadding` que viste al principio del capítulo.

## Resumen

En este capítulo aprendiste a construir y organizar una pantalla completa:

- El **`Scaffold`** ofrece la estructura básica de una pantalla, con espacios para la barra superior (`TopAppBar`), el contenido, una barra inferior y un botón flotante.
- Una pantalla se organiza mejor por **jerarquías**: la raíz coordina la estructura y los componentes hijos reciben datos y eventos.
- Las **Window Size Classes** permiten elegir entre una composición compacta, intermedia o expandida según el espacio real de la ventana, haciendo posible un diseño adaptable para móviles, tabletas y ventanas redimensionadas.
- `Compact`, `Medium` y `Expanded` son clases de ancho y de alto basadas en `dp`; para el ancho, los puntos de referencia habituales son `600.dp` y `840.dp`. No representan modelos de dispositivo, sino el espacio que la app tiene disponible.
- Sin un layout, los composables se **superponen**. Los tres layouts básicos son `Column` (vertical), `Row` (horizontal) y `Box` (apilados).
- `Column` y `Row` controlan la **distribución** (`Arrangement`) en su eje principal y la **alineación** (`Alignment`) en el eje cruzado.
- El modificador **`weight`**, dentro de un `Row` o `Column`, reparte el espacio disponible de forma proporcional.
- **`LazyColumn`** muestra listas con desplazamiento de forma eficiente, componiendo solo los elementos visibles; se llena con la función `items`. Su versión horizontal es `LazyRow`.

Ya sabes construir la estructura de una pantalla. En el próximo capítulo verás el **estado en Compose**: cómo declarar los datos que, al cambiar, disparan la recomposición, con `remember` y `mutableStateOf`.
