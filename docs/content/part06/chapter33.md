# Capítulo 33: Material 3: tema, color y tipografía

## Introducción

Ya sabes construir pantallas completas con componentes de Material 3, organizarlos con layouts, darles estado y navegar entre ellas. Pero tu interfaz todavía usa el estilo por defecto que trae el proyecto: es hora de hacerla **tuya**. En este capítulo verás cómo funciona el **tema** de Material 3 por dentro, cómo definir un esquema de **colores** y cómo aplicar una **tipografía** consistente. Con esto cierras la parte de interfaz con Jetpack Compose.

## El tema: `MaterialTheme`

Cuando creaste el proyecto, Android Studio generó un **tema** para tu app: un composable, normalmente llamado `NombreDeTuAppTheme`, que envuelve toda la interfaz. Lo viste en `MainActivity`:

```kotlin
setContent {
    MiAppTheme {
        // toda tu interfaz va aquí
    }
}
```

Ese tema (que por dentro usa `MaterialTheme`) les proporciona a todos los composables de su interior tres cosas: un **esquema de colores**, una **tipografía** y unas **formas**. Gracias a él, los componentes de Material que usaste en capítulos anteriores (`Button`, `Card`, `TopAppBar`…) saben qué colores y estilos usar sin que tengas que indicárselos uno por uno. El tema se define en los archivos de la carpeta `ui/theme/` de tu proyecto, que puedes personalizar.

## Colores

El tema define un **esquema de colores** (*color scheme*) con roles con nombre, no colores sueltos. Los principales son `primary` (el color de marca de tu app), `secondary`, `background` (el fondo), `surface` (superficies como las tarjetas) y sus variantes "on" (`onPrimary`, `onBackground`…), que indican el color del contenido que va **encima** de cada uno.

Los componentes de Material usan estos colores automáticamente, pero tú también puedes acceder a ellos a través de `MaterialTheme.colorScheme`:

```kotlin
Text(
    text = "Hola",
    color = MaterialTheme.colorScheme.primary
)
```

La gran ventaja de usar roles (en vez de colores fijos) es que tu app se adapta sola: si defines un tema **oscuro**, todos esos roles cambian de valor y la interfaz entera se ve bien en modo oscuro, sin que toques cada componente.

## Tipografía

Igual que con los colores, el tema define una **tipografía**: un conjunto de estilos de texto predefinidos y coherentes, como `displayLarge` (títulos grandes), `titleLarge`, `bodyLarge` (texto normal) o `labelSmall` (etiquetas pequeñas).

Aplicas un estilo con el parámetro `style` de `Text`:

```kotlin
Text(
    text = "Mi aplicación",
    style = MaterialTheme.typography.headlineMedium
)
```

Usar estos estilos, en lugar de fijar tamaños de letra a mano, mantiene la jerarquía visual consistente en toda la app. Por ejemplo, en la `Card` de contacto que viste antes, podrías darle al nombre un estilo destacado:

```kotlin
Card {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ana López", style = MaterialTheme.typography.titleLarge)
        Text("Diseñadora gráfica")
    }
}
```

## Resumen

En este capítulo le diste estilo final a la interfaz con Material 3:

- El **tema** (`MaterialTheme`, envuelto en el `NombreAppTheme` de tu proyecto) le da a toda la app un **esquema de colores**, una **tipografía** y unas **formas** coherentes.
- Accedes a los colores por su **rol** (`MaterialTheme.colorScheme.primary`) y a los estilos de texto por su nombre (`MaterialTheme.typography.titleLarge`), lo que hace que la app se adapte sola a modo claro u oscuro y mantenga la jerarquía visual.

Con esto cierras la parte de **interfaz con Jetpack Compose**. En la próxima parte del curso darás un paso clave hacia las apps profesionales: la **arquitectura MVVM**, que organiza tu código separando la interfaz, la lógica y los datos.

