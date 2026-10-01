# Capítulo 38: Barras de navegación y destinos principales

## Introducción

Hasta ahora has visto cómo declarar destinos con `NavHost`, controlar la navegación con `NavController`, y moverte entre pantallas con `navigate()`. Pero en una aplicación real, el usuario no escribe rutas a mano: toca botones, íconos o elementos de una lista.

Las **barras de navegación** son los controles visibles que el usuario emplea para moverse por la app. Material 3 ofrece componentes especializados para diferentes contextos.

### ¿Qué problema resuelven las barras de navegación?

Cuando tu app tiene varios destinos principales —Inicio, Favoritos, Perfil—, el usuario necesita una forma rápida y visible de saltar entre ellos sin tener que retroceder hasta el principio cada vez. Las barras de navegación ofrecen esa orientación constante: muestran dónde está el usuario ahora y hacia dónde puede ir.

> [!NOTE]Nota importante
> Las barras de navegación **no reemplazan** al `NavHost` ni al `NavController`. Son simplemente controles de interfaz que disparan navegación. El flujo sigue siendo el mismo:

```
Usuario toca un ítem
        ↓
NavigationBarItem
        ↓
navController.navigate("favoritos")
        ↓
NavController
        ↓
NavHost
        ↓
composable("favoritos")
        ↓
PantallaFavoritos
```

### `NavigationBar`

`NavigationBar` es el componente de navegación inferior de Material 3. Es ideal para pantallas compactas (teléfonos en vertical) con 3 a 5 destinos principales.

```kotlin
NavigationBar {
    // ... ítems de navegación
}
```

La barra en sí es solo el contenedor; los ítems individuales se crean con `NavigationBarItem`.

### `NavigationBarItem`

Cada ítem de la barra tiene:

- **`icon`**: el ícono que se muestra.
- **`label`**: el texto que acompaña al ícono.
- **`selected`**: si este ítem corresponde al destino actual.
- **`onClick`**: qué hacer cuando el usuario lo toca (normalmente, navegar).

Ejemplo:

```kotlin
NavigationBarItem(
    selected = false,
    onClick = { navController.navigate("inicio") },
    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
    label = { Text("Inicio") }
)
```

### Determinar el ítem seleccionado

La barra de navegación debe reflejar en qué pantalla está el usuario **en este momento**. Para eso, necesitas obtener el destino actual desde el `NavController`.

Navigation Compose ofrece `currentBackStackEntryAsState()`, que devuelve un `State` con la entrada actual de la pila de navegación:

```kotlin
val backStackEntry by navController.currentBackStackEntryAsState()
val currentRoute = backStackEntry?.destination?.route
```

Ahora puedes comparar `currentRoute` con la ruta de cada ítem:

```kotlin
NavigationBarItem(
    selected = currentRoute == "inicio",
    onClick = { navController.navigate("inicio") },
    icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
    label = { Text("Inicio") }
)
```

> [!NOTE]Nota importante
> El elemento seleccionado de la barra **no debería ser un estado independiente** que pueda quedar desincronizado de la navegación. Siempre obtén el destino actual desde el *back stack* del `NavController`.

### Evitar destinos duplicados en la pila

Imagina que el usuario está en **Inicio** y toca **Favoritos** en la barra de navegación; luego toca **Perfil**; luego vuelve a tocar **Inicio**. Si cada toque simplemente hace `navigate("inicio")`, la pila crece así:

```
Inicio → Favoritos → Perfil → Inicio
```

Al presionar el botón de retroceso, el usuario vuelve a Perfil, luego a Favoritos, luego al Inicio anterior… ¡tiene que retroceder **cuatro veces** para salir de la app!

Eso no es lo que el usuario espera. Entre destinos principales, el usuario espera **saltar**, no **apilar**.

Para evitarlo, Navigation Compose ofrece opciones al navegar:

```kotlin
navController.navigate("favoritos") {
    popUpTo(navController.graph.startDestinationId) {
        saveState = true
    }
    launchSingleTop = true
    restoreState = true
}
```

¿Qué hace cada opción?

- **`popUpTo(startDestinationId) { saveState = true }`**: elimina todas las pantallas de la pila hasta el destino de inicio (incluido), pero guarda su estado. Así, al volver a Inicio, recuperas el scroll y los datos que tenía.
- **`launchSingleTop = true`**: si el destino al que navegas ya está en la cima de la pila, no se apila de nuevo; se reutiliza la instancia existente.
- **`restoreState = true`**: si el destino al que navegas ya existía antes (y su estado fue guardado con `saveState`), lo restaura en lugar de crear una instancia nueva.

Con estas opciones, cada toque en la barra "aplana" la pila: solo quedan el destino de inicio y, encima, el destino recién seleccionado. Al presionar retroceso, el usuario vuelve al inicio, y al presionar de nuevo, sale de la app. Eso es lo que espera.

### Otros componentes de navegación de Material 3

Material 3 ofrece tres componentes de navegación, cada uno optimizado para un contexto distinto:

| Componente | Uso típico | Ventaja |
|---|---|---|
| **`NavigationBar`** + `NavigationBarItem` | Barra inferior en pantallas compactas (teléfonos en vertical), con 3 a 5 destinos principales. | Acceso rápido con el pulgar; siempre visible. |
| **`NavigationRail`** + `NavigationRailItem` | Barra lateral vertical en pantallas medianas o anchas (tabletas, teléfonos en horizontal). | Libera espacio vertical para el contenido; se adapta a ventanas expandidas. |
| **`ModalNavigationDrawer`** + `NavigationDrawerItem` | Cajón lateral que se abre desde un botón de menú, para destinos secundarios o cuando hay muchos (más de 5). | No ocupa espacio permanente; puede contener muchas opciones agrupadas. |

La **`TopAppBar`** también forma parte del sistema de navegación, pero su propósito es distinto: identifica la pantalla actual y ofrece acciones contextuales (buscar, guardar, abrir el menú). En una pantalla de detalle suele mostrar una flecha de retroceso para volver a la lista.

#### `NavigationRail`

Para ventanas anchas (tabletas o teléfonos en horizontal), `NavigationRail` ofrece mejor aprovechamiento del espacio:

```kotlin
NavigationRail {
    NavigationRailItem(
        selected = currentRoute == "inicio",
        onClick = { /* navegar */ },
        icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
        label = { Text("Inicio") }
    )
}
```

La lógica de selección y navegación es idéntica a `NavigationBar`; solo cambia el componente visual.

#### `ModalNavigationDrawer`

Para destinos secundarios o muy numerosos, `ModalNavigationDrawer` envuelve el contenido y se abre desde un botón de menú en la `TopAppBar`:

```kotlin
ModalNavigationDrawer(
    drawerContent = {
        ModalDrawerSheet {
            NavigationDrawerItem(
                label = { Text("Ajustes") },
                selected = false,
                onClick = { /* navegar */ }
            )
        }
    }
) {
    // ... contenido principal
}
```

### Destinos principales vs. secundarios

No todos los destinos son iguales. Es importante distinguir entre:

#### Destinos principales

Los **destinos principales** (*top-level destinations*) son las secciones principales de la app que el usuario visita con frecuencia y entre las que salta sin orden fijo: Inicio, Favoritos, Perfil. Cada uno es un punto de entrada independiente, y ninguno es "hijo" de otro.

Estos son los destinos que aparecen en la barra de navegación.

#### Destinos secundarios

Los **destinos secundarios** son pantallas que se alcanzan desde un destino principal y a las que se llega en un flujo lineal: el detalle de un contacto, la pantalla de edición, el resultado de una búsqueda. No aparecen en la barra de navegación; se accede a ellos tocando un elemento de una lista o un botón, y se sale de ellos con el botón de retroceso.

Ejemplo de jerarquía:

- **Inicio** (principal) → Detalle de una tarea (secundario) → Editar tarea (secundario)
- **Favoritos** (principal) → Detalle de un favorito (secundario)
- **Perfil** (principal)

La barra de navegación solo muestra los tres destinos principales; los secundarios no aparecen ahí.

### Ejemplo completo: navegación con `NavigationBar`

Aquí tienes un ejemplo completo que integra todos los conceptos:

#### Modelo de destinos

```kotlin
sealed class AppDestination(val route: String, val label: String, val icon: ImageVector) {
    data object Inicio : AppDestination("inicio", "Inicio", Icons.Default.Home)
    data object Favoritos : AppDestination("favoritos", "Favoritos", Icons.Default.Star)
    data object Perfil : AppDestination("perfil", "Perfil", Icons.Default.Person)
    
    // Destino secundario (no aparece en la barra):
    data object Detalle : AppDestination("detalle/{id}", "Detalle", Icons.Default.Info)
}

val destinosPrincipales = listOf(
    AppDestination.Inicio,
    AppDestination.Favoritos,
    AppDestination.Perfil
)
```

#### Barra de navegación

```kotlin
@Composable
fun AppNavigationBar(
    navController: NavHostController,
    destinations: List<AppDestination>
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    NavigationBar {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = currentRoute == destination.route,
                onClick = {
                    navController.navigate(destination.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(destination.icon, contentDescription = destination.label) },
                label = { Text(destination.label) }
            )
        }
    }
}
```

#### Estructura completa de la app

```kotlin
@Composable
fun App() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            AppNavigationBar(
                navController = navController,
                destinations = destinosPrincipales
            )
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Inicio.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(AppDestination.Inicio.route) {
                PantallaInicio()
            }
            composable(AppDestination.Favoritos.route) {
                PantallaFavoritos()
            }
            composable(AppDestination.Perfil.route) {
                PantallaPerfil()
            }
            composable(AppDestination.Detalle.route) { backStackEntry ->
                val id = backStackEntry.arguments?.getString("id")
                PantallaDetalle(id = id)
            }
        }
    }
}
```

La arquitectura conceptual completa es:

```
App
│
├── NavController
│
├── NavHost
│   ├── "inicio" → PantallaInicio
│   ├── "favoritos" → PantallaFavoritos
│   ├── "perfil" → PantallaPerfil
│   └── "detalle/{id}" → PantallaDetalle
│
└── NavigationBar
    ├── Inicio → navigate("inicio")
    ├── Favoritos → navigate("favoritos")
    └── Perfil → navigate("perfil")
```

### Diseño adaptable

La barra y el contenido también deben adaptarse juntos. En una ventana compacta, `NavigationBar` puede llevar a una pantalla completa; en una ventana expandida, la misma acción puede seleccionar el panel de la izquierda mientras el detalle permanece visible. En ambos casos, la fuente de verdad sigue siendo el estado del `NavController`, y `NavHost` continúa declarando los destinos.

El capítulo 40 (opcional) profundiza en cómo detectar el tamaño de ventana con *Window Size Classes* y elegir automáticamente entre `NavigationBar`, `NavigationRail` o un diseño de dos paneles.

## Resumen

En este capítulo aprendiste a construir los controles de navegación de la app:

- `NavigationBar`, `NavigationRail` y `ModalNavigationDrawer` son controles que disparan navegación, pero **no reemplazan** al `NavHost`.
- Los **destinos principales** aparecen en la barra de navegación; los **destinos secundarios** se alcanzan desde ellos.
- Usa `currentBackStackEntryAsState()` para sincronizar el elemento seleccionado de la barra con el destino actual.
- Usa `popUpTo`, `launchSingleTop` y `restoreState` para evitar acumular destinos duplicados en la pila: entre destinos principales el usuario espera saltar, no apilar.

Ya sabes construir y conectar pantallas completas. En el próximo capítulo cerrarás la parte de Compose dándole a la interfaz su **estilo final** con Material 3: el tema, los colores y la tipografía.
