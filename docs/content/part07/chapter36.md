# Capítulo 36: Navegación con Navigation Compose (fundamentos)

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

## Resumen

En este capítulo aprendiste los fundamentos de Navigation Compose:

- Una **ruta** identifica un destino; no es la pantalla en sí.
- El **`NavController`** controla la navegación: mantiene el *back stack*, permite navegar y volver atrás, y permite consultar el destino actual.
- El **`NavHost`** es donde se muestran los destinos: declara qué composable se dibuja para cada ruta.
- `composable("ruta")` **declara** un destino; `navigate("ruta")` **navega** hacia él.
- El ***back stack*** registra las pantallas visitadas y permite que el botón de retroceso funcione automáticamente.

En el **próximo capítulo** verás cómo modelar rutas y destinos de forma más robusta, cómo pasar datos entre pantallas con argumentos en las rutas, y buenas prácticas para mantener tus pantallas desacopladas de la navegación.
