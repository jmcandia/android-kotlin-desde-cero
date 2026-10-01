# Capítulo 38: Navegación avanzada: rutas modeladas y paso de datos

## Introducción

En el capítulo anterior aprendiste los fundamentos de Navigation Compose: el `NavController`, el `NavHost` y el *back stack*. Hasta ahora usaste rutas como cadenas de texto simples (`"inicio"`, `"favoritos"`). En este capítulo verás cómo modelar rutas y destinos de forma más robusta, cómo **pasar datos** entre pantallas con argumentos, y buenas prácticas para mantener tus pantallas desacopladas de la navegación.

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

## Resumen

En este capítulo profundizaste en la navegación con Navigation Compose:

- Centraliza las rutas en un **modelo** (`sealed class AppDestination`) en lugar de repartir cadenas por todo el código.
- Una ruta con `{id}` es una **declaración** de parámetro; al navegar, incluyes el valor concreto (`"detalle/42"`).
- Los **argumentos** de la ruta llegan como texto; conviértelos con `toInt()` si necesitas un número.
- No pases **objetos grandes** dentro de una ruta; pasa un identificador y recupera el objeto desde el estado o el repositorio.
- Como buena práctica, pasa **funciones** de navegación a las pantallas en vez del `NavController`, para mantenerlas desacopladas (misma idea del *state hoisting*).

En el **próximo capítulo** verás las barras de navegación de Material 3 —los controles visibles con los que el usuario se mueve por la app— y cómo integrarlas con todo lo que aprendiste aquí.
