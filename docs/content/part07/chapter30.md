# Capítulo 30: Gestión de recursos: imágenes, íconos y cadenas de texto

## Introducción

En el capítulo 25 viste que un proyecto Android separa el **código** (`kotlin+java/`) de los **recursos** (`res/`): imágenes, el ícono de la app, textos, entre otros. Hasta ahora no necesitabas mirar esa carpeta de cerca, pero para seguir avanzando —y, en particular, para usar `Image` en el próximo capítulo— conviene entender cómo Android organiza esos recursos y cómo se referencian desde tu código.

En este capítulo verás cómo agregar **imágenes** al proyecto, cómo se genera y personaliza el **ícono de la app**, y por qué conviene guardar los **textos** en un archivo aparte en lugar de escribirlos directamente en el código.

## La carpeta `res` y la clase `R`

Dentro de `res/` (de *resources*), Android organiza cada tipo de recurso en su propia carpeta. Las que más vas a usar por ahora son:

```text
res/
├── drawable/    ← imágenes e íconos vectoriales
├── mipmap/      ← el ícono de la app, en sus distintas variantes
└── values/      ← strings.xml, y otros valores como colores o dimensiones
```

Cuando agregas un archivo a cualquiera de estas carpetas, Android Studio **regenera automáticamente** una clase llamada `R` (de *resources*), con una referencia a cada recurso. Por ejemplo, una imagen `res/drawable/foto_perfil.png` queda disponible en tu código Kotlin como `R.drawable.foto_perfil`; un texto declarado en `res/values/strings.xml` con el nombre `titulo_pantalla`, como `R.string.titulo_pantalla`.

Esto tiene dos ventajas grandes frente a, por ejemplo, escribir la ruta de un archivo a mano: el compilador **verifica** que el recurso exista (si lo borras o le cambias el nombre, tu código no compila hasta que lo corrijas) y Android Studio te ofrece **autocompletado** para encontrarlos.

> [!NOTE]Nota
> Los nombres de los recursos siguen una convención estricta: solo minúsculas, números y guion bajo (`snake_case`), sin espacios ni mayúsculas. Android Studio te avisa si intentas nombrar un archivo de otra forma.

## Imágenes: `res/drawable`

Para agregar una imagen a tu proyecto, la forma más simple es arrastrarla (o copiarla) dentro de `res/drawable/` desde el explorador de archivos, o bien hacer clic derecho sobre `res` y elegir **New > Vector Asset** si quieres usar uno de los íconos que trae Android Studio.

Aquí aparece una decisión importante: **imagen de mapa de bits** (PNG, JPG) o **vector** (`.xml`, un *Vector Drawable*).

- Una imagen de mapa de bits es una **cuadrícula fija de píxeles**: se ve bien a su tamaño original, pero se pixela si la agrandas demasiado. Por eso, tradicionalmente, Android pedía una copia distinta por cada densidad de pantalla (`mdpi`, `hdpi`, `xhdpi`, `xxhdpi`…), y el sistema elegía automáticamente la que correspondía al dispositivo.
- Un **Vector Drawable** describe la imagen con **formas matemáticas** (líneas, curvas), no con píxeles. Esto significa que **escala sin perder calidad** a cualquier tamaño y, además, ocupa mucho menos espacio, porque no necesitas una copia por densidad.

Por eso, para íconos y logos simples, **preferirás casi siempre un Vector Drawable**; para fotografías reales (donde no aplican las formas vectoriales), seguirás usando PNG o JPG.

Una vez que la imagen está en `res/drawable/`, ya sabes cómo mostrarla: con `Image` y `painterResource`, como viste en el capítulo de componentes.

## El ícono de la app: `res/mipmap`

El **ícono de tu app** (el que ve el usuario en la pantalla de inicio del teléfono) vive en una carpeta aparte, `res/mipmap/`, y no en `drawable/`. La razón es técnica: a diferencia de una imagen dentro de tu app —que Android puede optimizar y descartar en las densidades que no necesita—, el ícono del launcher debe estar **siempre disponible en todas las densidades**, sin importar la del dispositivo, porque el sistema operativo lo usa fuera de tu app (en el launcher, en la lista de apps recientes, etc.).

Ya viste, en el `AndroidManifest.xml` del capítulo 25, cómo se referencia:

```xml
<application
    android:icon="@mipmap/ic_launcher">
```

Desde Android 8.0 (API 26), los íconos son **adaptativos** (*adaptive icons*): en lugar de una sola imagen, se arman con dos capas, un `ic_launcher_foreground` (el dibujo) y un `ic_launcher_background` (el fondo), para que el propio sistema pueda recortarlas con distintas formas (círculo, cuadrado con esquinas redondeadas, "squircle"…) según el fabricante del dispositivo, manteniendo una apariencia consistente en todo el sistema.

Para reemplazar el ícono por defecto con tu propio logo, no edites los archivos a mano: usa el asistente de Android Studio. Haz clic derecho sobre `res/` y elige **New > Image Asset**. Ahí eliges tu imagen (idealmente un logo simple, en alta resolución o en formato vectorial), Android Studio te deja previsualizar cómo se ve recortado con las distintas formas, y genera automáticamente todos los archivos y densidades necesarias por ti.

## Texto: `res/values/strings.xml`

Hasta ahora, en los ejemplos del curso, escribiste el texto directamente en el código: `Text("¡Bienvenido!")`. Funciona, pero en una app real es preferible declarar los textos en un archivo aparte, `res/values/strings.xml`:

```xml
<resources>
    <string name="app_name">Mi Lista de Tareas</string>
    <string name="titulo_pantalla">Mis tareas</string>
</resources>
```

Y leerlos desde un composable con `stringResource`:

```kotlin
Text(text = stringResource(id = R.string.titulo_pantalla))
```

¿Por qué conviene hacerlo así, en lugar de escribir el texto directamente?

- **Reutilización**: si el mismo texto aparece en varios lugares, lo defines una sola vez (el mismo principio **DRY** del anexo de principios de diseño).
- **Traducción**: si más adelante quieres ofrecer tu app en otro idioma, creas una carpeta como `values-en/` con un `strings.xml` equivalente, y Android elige automáticamente el que corresponde al idioma del dispositivo, sin tocar una sola línea de tu código Kotlin.
- Algunos textos, como el **nombre de la app** (`app_name`, el que ya usa tu `AndroidManifest.xml`), **deben** vivir en `strings.xml`; no es opcional.

Los textos con partes variables también se pueden definir como recursos, usando un marcador de posición:

```xml
<string name="saludo">¡Hola, %1$s!</string>
```

```kotlin
Text(text = stringResource(id = R.string.saludo, nombre))
```

> [!NOTE]Nota
> Para mantener los ejemplos del curso simples y fáciles de leer, seguiremos escribiendo la mayoría de los textos directamente en el código, como hasta ahora. Pero en un proyecto real —y, en especial, en cualquier app que vayas a publicar— es una buena práctica declarar los textos visibles para el usuario en `strings.xml`.

## Resumen

En este capítulo aprendiste a organizar los recursos de tu app:

- La carpeta **`res/`** separa los recursos por tipo: `drawable/` (imágenes e íconos), `mipmap/` (el ícono de la app) y `values/` (textos y otros valores).
- La clase **`R`**, generada automáticamente, te da acceso *type-safe* a cada recurso (`R.drawable.foto`, `R.string.titulo`…); el compilador te avisa si un recurso no existe.
- Para íconos y logos simples, preferirás un **Vector Drawable**: escala sin perder calidad y no necesita una copia por densidad de pantalla.
- El **ícono de la app** vive en `mipmap/`, no en `drawable/`, porque debe estar disponible en todas las densidades; se genera y reemplaza con el asistente **Image Asset** de Android Studio, y hoy en día suele ser un **ícono adaptativo** (una capa de fondo y una de primer plano).
- Los **textos** deberían declararse en `res/values/strings.xml` y leerse con `stringResource`, para reutilizarlos y facilitar la traducción a otros idiomas.

En el próximo capítulo retomarás los componentes de Material 3 —`Text`, `Image`, `Button`, `Card`— ahora que ya sabes de dónde salen las imágenes que les vas a pasar.
