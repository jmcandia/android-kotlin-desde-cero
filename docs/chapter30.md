# Capítulo 30: Material 3: tema, color, tipografía y componentes

## Introducción

Tu interfaz ya funciona, pero se ve bastante sosa: texto negro sobre fondo blanco, sin estilo. Las apps reales lucen pulidas porque siguen un **sistema de diseño**: un conjunto coherente de colores, tipografías y componentes.

Compose incluye **Material 3**, el sistema de diseño de Google, con **componentes listos para usar** (botones, tarjetas, barras) y un sistema de **temas** que le da a toda tu app una apariencia consistente. En este capítulo verás el tema, los colores, la tipografía y los componentes clave. Esto es lo que hace que una app se vea pulida y profesional.

## ¿Qué es Material Design?

**Material Design** es el sistema de diseño creado por Google: una serie de guías y componentes para construir interfaces atractivas y coherentes en Android (y otras plataformas). Su versión más reciente es **Material 3** (también llamado *Material You*), que introduce, entre otras cosas, colores capaces de adaptarse al fondo de pantalla del usuario.

Compose viene con una biblioteca que implementa Material 3, así que obtienes sus componentes y su sistema de temas **gratis**, sin tener que diseñarlos desde cero.

## El tema: `MaterialTheme`

Cuando creaste el proyecto, Android Studio generó un **tema** para tu app: un composable, normalmente llamado `NombreDeTuAppTheme`, que envuelve toda la interfaz. Lo viste en `MainActivity`:

```kotlin
setContent {
    MiAppTheme {
        // toda tu interfaz va aquí
    }
}
```

Ese tema (que por dentro usa `MaterialTheme`) les proporciona a todos los composables de su interior tres cosas: un **esquema de colores**, una **tipografía** y unas **formas**. Gracias a él, los componentes de Material saben qué colores y estilos usar sin que tengas que indicárselos uno por uno. El tema se define en los archivos de la carpeta `ui/theme/` de tu proyecto, que puedes personalizar.

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

Usar estos estilos, en lugar de fijar tamaños de letra a mano, mantiene la jerarquía visual consistente en toda la app.

## Componentes listos para usar

Material 3 trae muchos componentes ya construidos. Estos son algunos de los que más usarás.

Un **`Button`** (que ya usaste) muestra un botón con el estilo de Material:

```kotlin
Button(onClick = { /* acción */ }) {
    Text("Aceptar")
}
```

Una **`Card`** es una superficie con elevación y esquinas redondeadas, ideal para agrupar información relacionada; es perfecta, por ejemplo, para cada elemento de una lista:

```kotlin
Card {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ana López", style = MaterialTheme.typography.titleLarge)
        Text("Diseñadora gráfica")
    }
}
```

Hay muchos más (`Icon`, `IconButton`, `TextField`, `Checkbox`, `Switch`…), y todos comparten el estilo del tema, así que combinan entre sí de forma coherente.

## `Scaffold`: el esqueleto de una pantalla

La mayoría de las pantallas comparten una estructura: una barra arriba, el contenido en el medio, quizás una barra abajo o un botón flotante. En lugar de armar eso a mano, Material ofrece el **`Scaffold`** ("andamio"), un composable que provee **espacios** (*slots*) para cada una de esas partes:

![Scaffold](assets/images/chapter30/scaffold.svg)

Un uso típico, con una barra superior y el contenido:

```kotlin
Scaffold(
    topBar = {
        TopAppBar(title = { Text("Mi aplicación") })
    }
) { innerPadding ->
    LazyColumn(modifier = Modifier.padding(innerPadding)) {
        // una lista de elementos
    }
}
```

Fíjate en el `innerPadding`: el `Scaffold` te entrega el espacio que ocupan las barras para que **apartes** el contenido y no quede tapado por ellas. Por eso se lo pasas como `padding` al contenido. (Ya habías visto este patrón en el `MainActivity` que generó Android Studio.)

> [!NOTE]
> Algunos componentes de Material 3, como `TopAppBar`, están marcados todavía como *experimentales*, lo que obliga a añadir la anotación `@OptIn(ExperimentalMaterial3Api::class)` sobre la función que los usa. Android Studio te avisa y la agrega por ti.

## Resumen

En este capítulo le diste estilo a la interfaz con Material 3:

- **Material Design** es el sistema de diseño de Google; **Material 3** es su versión actual, y Compose lo incluye con componentes y temas listos para usar.
- El **tema** (`MaterialTheme`, envuelto en el `NombreAppTheme` de tu proyecto) le da a toda la app un **esquema de colores**, una **tipografía** y unas **formas** coherentes.
- Accedes a los colores por su **rol** (`MaterialTheme.colorScheme.primary`) y a los estilos de texto por su nombre (`MaterialTheme.typography.titleLarge`), lo que hace que la app se adapte sola a modo claro u oscuro y mantenga la jerarquía visual.
- Material trae **componentes listos**: `Button`, `Card`, `Icon`, `TextField` y muchos más.
- El **`Scaffold`** ofrece la estructura básica de una pantalla, con espacios para la barra superior (`TopAppBar`), el contenido, una barra inferior y un botón flotante.

En el próximo capítulo cerrarás la parte de Compose con la **navegación**: cómo moverte entre distintas pantallas de la app, por ejemplo, de una lista a la pantalla de detalle.
