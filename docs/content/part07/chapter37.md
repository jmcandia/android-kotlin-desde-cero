# Capítulo 37: Diseño adaptable con *Window Size Classes*

> [!NOTE]Capítulo opcional
> Este capítulo es de ampliación. No es necesario para continuar con la Parte VIII ni con el proyecto final; vuelve a él cuando quieras que tu app aproveche tabletas, pantallas plegables o ventanas redimensionables.

## Introducción

Hasta ahora has diseñado pantallas pensando en un teléfono en vertical. En este capítulo verás cómo una misma app puede cambiar su **estructura** según el espacio disponible, apoyándose en lo que ya conoces: `Scaffold`, `Row`, `weight` y las barras de navegación del capítulo 35.

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

## Resumen

- Las **Window Size Classes** permiten elegir entre una composición compacta, intermedia o expandida según el espacio real de la ventana, haciendo posible un diseño adaptable para móviles, tabletas y ventanas redimensionadas.
- `Compact`, `Medium` y `Expanded` son clases de ancho y de alto basadas en `dp`; para el ancho, los puntos de referencia habituales son `600.dp` y `840.dp`. No representan modelos de dispositivo, sino el espacio que la app tiene disponible.
- La pantalla decide su composición; los composables internos, el estado y la navegación se comparten entre las variantes.
