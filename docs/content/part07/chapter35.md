# Capítulo 35: Navegación con Navigation Compose

## Introducción

Hasta ahora, tu app ha vivido en una sola pantalla. Pero las aplicaciones reales tienen **varias**: una lista y su detalle, una pantalla de ajustes, un formulario… y el usuario se mueve entre ellas. En este capítulo aprenderás a hacer esa **navegación** entre pantallas con **Navigation Compose**, la biblioteca oficial para ello.

> [!NOTE]Nota
> Navigation Compose no viene incluido en la plantilla del proyecto; es una dependencia que se agrega en el `build.gradle.kts`, a través del catálogo de versiones que viste al crear el proyecto.

## Modelo mental de Navigation Compose

Antes de ver APIs específicas, es importante entender cómo funciona conceptualmente la navegación en Compose.

Una aplicación puede tener varias pantallas:

```
Inicio
Favoritos
Perfil
Detalle
```

Cada pantalla representa un **destino de navegación**. Para que el sistema de navegación pueda identificar cada destino, necesita una forma de nombrarlo. Para eso se usa una **ruta**.

Por ejemplo:

```
"inicio"
"favoritos"
"perfil"
"detalle"
```

> [!NOTE]Nota importante
> **La ruta identifica un destino; no es la pantalla en sí misma.** La ruta es simplemente un identificador (un texto), mientras que la pantalla es el composable que se muestra cuando se navega a ese destino.

Por ejemplo, `"inicio"` es un identificador, mientras que:

```kotlin
@Composable
fun PantallaInicio() {
    // ... contenido de la pantalla
}
```

es la interfaz de usuario que se muestra cuando el usuario está en ese destino.

### Relación entre ruta y destino

Visualmente, la relación entre ruta y destino se ve así:

```
Ruta                     Destino
────────────────────────────────────────
"inicio"          →      PantallaInicio
"favoritos"       →      PantallaFavoritos
"perfil"          →      PantallaPerfil
```

En Navigation Compose, esta relación se expresa así:

```kotlin
NavHost(
    navController = navController,
    startDestination = "inicio"
) {
    composable("inicio") {
        PantallaInicio()
    }

    composable("favoritos") {
        PantallaFavoritos()
    }

    composable("perfil") {
        PantallaPerfil()
    }
}
```

Cada llamada a `composable("inicio")` declara:

> "Cuando el destino actual sea la ruta `inicio`, muestra este contenido composable."

## ¿Qué es el `NavController`?

El **`NavController`** es el objeto que controla la navegación. Piénsalo como el "director" que coordina todo el sistema de navegación.

Para crear un `NavController`, usas:

```kotlin
val navController = rememberNavController()
```

El `NavController` tiene varias responsabilidades:

- **Conoce el estado actual**: sabe en qué destino está el usuario en este momento.
- **Mantiene el *back stack***: lleva registro de las pantallas visitadas, para que el botón de retroceso funcione correctamente.
- **Permite navegar**: ofrece funciones para ir a otro destino.
- **Permite regresar**: ofrece funciones para volver al destino anterior.
- **Permite consultar el destino actual**: puedes preguntarle en qué destino estás para actualizar la interfaz (por ejemplo, resaltar el ítem seleccionado en una barra de navegación).

### Navegar a un destino

La forma más simple de navegar es:

```kotlin
navController.navigate("favoritos")
```

Esto le dice al `NavController`: "cambia el destino actual a `favoritos`".

El flujo completo se ve así:

```
Usuario pulsa "Favoritos"
        ↓
navController.navigate("favoritos")
        ↓
NavController cambia el destino actual
        ↓
NavHost detecta el nuevo destino
        ↓
NavHost muestra PantallaFavoritos
```

Esta secuencia es fundamental: el usuario dispara una acción, el `NavController` actualiza el estado de navegación, y el `NavHost` reacciona mostrando el contenido correspondiente.

## ¿Qué es el `NavHost`?

Si el `NavController` es el "director" que controla la navegación, el **`NavHost`** es el "escenario" donde se muestran los destinos.

> **`NavController` controla la navegación; `NavHost` es el lugar donde se muestran los destinos definidos para esa navegación.**

Un `NavHost` se ve así:

```kotlin
NavHost(
    navController = navController,
    startDestination = "inicio"
) {
    composable("inicio") {
        PantallaInicio()
    }

    composable("favoritos") {
        PantallaFavoritos()
    }
}
```

Cada elemento tiene un propósito específico:

### `navController`

Indica qué `NavController` administra esta navegación. El `NavHost` observa el estado de este controlador y actualiza su contenido cuando el destino actual cambia.

### `startDestination`

Indica cuál es el destino inicial que se muestra al abrir la app. En este ejemplo, `"inicio"` será la primera pantalla que vea el usuario.

### `composable("inicio")`

Registra un destino identificado por la ruta `"inicio"`. Dentro del bloque, defines qué composable se debe mostrar cuando el usuario navega a ese destino.

> [!NOTE]Nota
> El `NavHost` no crea las pantallas automáticamente; simplemente define qué contenido debe mostrarse para cada destino. Tú decides qué composables mostrar.

## Todos los elementos juntos

Veamos un ejemplo completo que integra `NavController` y `NavHost`:

```kotlin
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "inicio"
    ) {
        composable("inicio") {
            PantallaInicio(
                onNavigateToFavoritos = {
                    navController.navigate("favoritos")
                }
            )
        }

        composable("favoritos") {
            PantallaFavoritos()
        }
    }
}
```

Visualmente, la arquitectura se ve así:

```
                  ┌─────────────────────┐
                  │    NavController    │
                  │                     │
                  │  destino actual     │
                  │  back stack         │
                  │  navegación         │
                  └──────────┬──────────┘
                             │
                             ▼
                  ┌─────────────────────┐
                  │       NavHost       │
                  │                     │
                  │  "inicio"           │
                  │  "favoritos"        │
                  │  "perfil"           │
                  └──────────┬──────────┘
                             │
                             ▼
                    Pantalla correspondiente
```

El punto clave es que **`NavController` y `NavHost` trabajan juntos, pero tienen responsabilidades diferentes**: uno controla el estado y las transiciones, el otro muestra el contenido.

## Diferencia entre `composable(...)` y `navigate(...)`

Es importante distinguir entre declarar un destino y navegar hacia él:

```kotlin
composable("perfil") { ... }
```

**declara/registra** el destino. Le dice al `NavHost`: "cuando alguien navegue a `perfil`, muestra este contenido".

Mientras que:

```kotlin
navController.navigate("perfil")
```

**navega hacia** ese destino. Le dice al `NavController`: "cambia el destino actual a `perfil`".

Visualmente:

```
composable("perfil")
        ↓
DECLARA / REGISTRA
el destino

navigate("perfil")
        ↓
NAVEGA HACIA
ese destino
```

Esta diferencia es fundamental: **primero declaras los destinos disponibles** (con `composable`), y **después navegas entre ellos** (con `navigate`).

## El *back stack*

Una vez que entiendes cómo funcionan `NavController` y `NavHost`, es importante conocer el concepto de ***back stack*** (pila de retroceso).

Cuando navegas entre pantallas, el `NavController` va apilando los destinos visitados. Por ejemplo:

**Estado inicial:**
```
Inicio
```

**Usuario navega a Favoritos:**
```
Inicio → Favoritos
```

**Usuario navega a Perfil:**
```
Inicio → Favoritos → Perfil
```

Esta pila es lo que permite que el **botón de retroceso** funcione: al presionarlo, el sistema quita el destino actual de la pila y muestra el anterior.

Si ejecutas:

```kotlin
navController.popBackStack()
```

la pila vuelve a:

```
Inicio → Favoritos
```

Y si el usuario presiona el botón de retroceso del sistema, el efecto es el mismo: Navigation quita `Favoritos` de la pila y muestra `Inicio`.

> [!TIP]Sugerencia
> El botón de retroceso del sistema funciona automáticamente con Navigation Compose. No necesitas escribir código extra para que funcione; Navigation lo gestiona por ti.

## Modelar rutas y destinos

Las cadenas de texto repartidas por todo el código son fáciles de escribir mal y difíciles de mantener. Una práctica común es centralizar las rutas en un modelo:

```kotlin
sealed class AppDestination(val route: String) {
    data object Inicio : AppDestination("inicio")
    data object Favoritos : AppDestination("favoritos")
    data object Perfil : AppDestination("perfil")
    data object Detalle : AppDestination("detalle/{id}")
}
```

Ahora, en lugar de usar cadenas directamente:

```kotlin
navController.navigate("inicio") // ❌ fácil equivocarse
```

usas el modelo:

```kotlin
navController.navigate(AppDestination.Inicio.route) // ✅ seguro
```

Si necesitas una ruta que incluya datos (como un ID), puedes construirla así:

```kotlin
navController.navigate("detalle/42")
```

La ruta `"detalle/{id}"` es una **declaración** que indica que el destino acepta un parámetro; la ruta que usas al navegar contiene el valor concreto.

## Pasar datos entre pantallas

Muchas veces necesitas pasar información de una pantalla a otra. Por ejemplo, al navegar a un detalle, necesitas decirle **qué** elemento mostrar.

Para eso, las rutas pueden incluir **argumentos**, indicados entre llaves:

```kotlin
composable("detalle/{id}") { backStackEntry ->
    val id = backStackEntry.arguments?.getString("id")
    PantallaDetalle(id = id)
}
```

Y, al navegar, incluyes el valor en la ruta:

```kotlin
navController.navigate("detalle/42")
```

Así, la pantalla de detalle recibe el `id` (`"42"`) y puede mostrar el elemento correspondiente. 

> [!NOTE]Nota
> El argumento llega como texto (`String`). Si necesitas un número, conviértelo con `toInt()`, como viste en el capítulo 4.

Para valores que puedan incluir espacios o caracteres especiales, codifica el argumento o, cuando el flujo lo requiera, pasa solo un identificador estable y recupera el objeto desde el estado de la app o su repositorio. No pases objetos grandes dentro de una ruta.

## Buenas prácticas de navegación

> [!TIP]Sugerencia
> En lugar de pasar el `NavController` a cada pantalla, es preferible que las pantallas reciban **funciones** de navegación (por ejemplo, `onVerDetalle: (String) -> Unit`). Así quedan desacopladas de la navegación y son más fáciles de reutilizar y previsualizar, siguiendo la misma idea del *state hoisting* que viste con el estado.

Por ejemplo, en lugar de:

```kotlin
@Composable
fun PantallaInicio(navController: NavController) {
    Button(onClick = { navController.navigate("favoritos") }) {
        Text("Ver favoritos")
    }
}
```

Prefiere:

```kotlin
@Composable
fun PantallaInicio(onNavigateToFavoritos: () -> Unit) {
    Button(onClick = onNavigateToFavoritos) {
        Text("Ver favoritos")
    }
}
```

Así, `PantallaInicio` no necesita conocer el `NavController` ni las rutas; simplemente invoca la función que recibe.

## Barras de navegación y destinos principales

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

El capítulo 37 (opcional) profundiza en cómo detectar el tamaño de ventana con *Window Size Classes* y elegir automáticamente entre `NavigationBar`, `NavigationRail` o un diseño de dos paneles.

## Resumen

En este capítulo aprendiste a moverte entre pantallas:

- Una **ruta** identifica un destino; no es la pantalla en sí.
- El **`NavController`** controla la navegación: mantiene el *back stack*, permite navegar y volver atrás, y permite consultar el destino actual.
- El **`NavHost`** es donde se muestran los destinos: declara qué composable se dibuja para cada ruta.
- `composable("ruta")` **declara** un destino; `navigate("ruta")` **navega** hacia él.
- El ***back stack*** registra las pantallas visitadas y permite que el botón de retroceso funcione automáticamente.
- Puedes **pasar datos** incluyendo argumentos en la ruta (`"detalle/{id}"`).
- `NavigationBar`, `NavigationRail` y `ModalNavigationDrawer` son controles que disparan navegación, pero no reemplazan al `NavHost`.
- Los **destinos principales** aparecen en la barra de navegación; los **destinos secundarios** se alcanzan desde ellos.
- Usa `currentBackStackEntryAsState()` para sincronizar el elemento seleccionado de la barra con el destino actual.
- Usa `popUpTo`, `launchSingleTop` y `restoreState` para evitar acumular destinos duplicados en la pila.
- Como buena práctica, pasa **funciones** de navegación a las pantallas en vez del `NavController`, para mantenerlas desacopladas.

Ya sabes construir y conectar pantallas completas. En el próximo capítulo cerrarás la parte de Compose dándole a la interfaz su **estilo final** con Material 3: el tema, los colores y la tipografía.
