# Capítulo 29: Material 3: componentes listos para usar

## Introducción

En los capítulos anteriores escribiste tu primer `@Composable`, conociste `Modifier` y aprendiste a organizar los recursos de tu app (imágenes, ícono, textos). Las apps reales se construyen con **componentes de interfaz** ya conocidos: textos, imágenes, botones, tarjetas… En este capítulo conocerás **Material 3**, el sistema de diseño que trae Compose, y los **componentes** más habituales que usarás para construir tus pantallas, junto con sus parámetros más comunes.

Dejaremos para más adelante cómo **personalizar** el aspecto de estos componentes (colores, tipografía); por ahora, usarás el estilo que Android Studio ya dejó preparado en tu proyecto.

## ¿Qué es Material Design?

**Material Design** es el sistema de diseño creado por Google: una serie de guías y componentes para construir interfaces atractivas y coherentes en Android (y otras plataformas). Su versión más reciente es **Material 3** (también llamado *Material You*), que introduce, entre otras cosas, colores capaces de adaptarse al fondo de pantalla del usuario.

Compose viene con una biblioteca que implementa Material 3, así que obtienes sus componentes y su sistema de temas **gratis**, sin tener que diseñarlos desde cero. Cuando creaste el proyecto, Android Studio generó además un **tema** (un composable, normalmente llamado `NombreDeTuAppTheme`) que envuelve toda tu interfaz y le da, por detrás, un estilo consistente a todos estos componentes. Volveremos sobre el tema con detalle más adelante; por ahora, basta con saber que ya está ahí, trabajando por ti.

## Componentes básicos

Material 3 trae muchos componentes ya construidos, todos con un estilo coherente entre sí. Estos son los cuatro que más usarás para armar una pantalla: `Text`, `Image`, `Button` y `Card`.

> [!NOTE]Nota
> Ya conoces `Modifier` para ajustar tamaño, espaciado, fondo o forma (`padding`, `size`, `background`…). Sigue usándolo para eso. Algunos componentes, como `Text`, también aceptan parámetros propios de apariencia (`color`, `fontSize`…), porque `Modifier` no llega al contenido interno del componente. Puedes usarlos si lo necesitas, pero acostúmbrate a resolver tamaño, espaciado y fondo con `modifier`, tal como aprendiste, y deja esos parámetros propios solo para lo que `modifier` no puede hacer.

### `Text`

Ya usaste `Text` para mostrar texto en pantalla. Su único parámetro obligatorio es `text`; el resto de su apariencia (tamaño, espaciado, fondo) sigue resolviéndose con `modifier`:

| Parámetro | Qué hace |
| :--- | :--- |
| `text` | El texto a mostrar. Es el único parámetro obligatorio. |
| `textAlign` | La alineación del texto dentro de su espacio (`TextAlign.Center`, `TextAlign.End`…). |
| `maxLines` | El número máximo de líneas antes de recortar el texto. |

```kotlin
Text(
    text = "¡Bienvenido!",
    modifier = Modifier
        .padding(16.dp)
        .fillMaxWidth(),
    textAlign = TextAlign.Center
)
```

> [!NOTE]Nota
> `Text` también admite `color`, `fontSize` o `fontWeight` para fijar su apariencia directamente, y es válido usarlos. Pero cuando lleguemos al capítulo de Material 3 sobre el tema, verás la forma recomendada de aplicar estilos de texto coherentes en toda la app con el parámetro `style`, en lugar de fijarlos uno por uno en cada `Text`.

### `Image`

`Image` muestra una imagen: un recurso de tu proyecto (guardado en `res/drawable`) o un ícono vectorial.

| Parámetro | Qué hace |
| :--- | :--- |
| `painter` | La fuente de la imagen; lo habitual es `painterResource(id = R.drawable.mi_imagen)`. |
| `contentDescription` | Un texto que describe la imagen para lectores de pantalla; es obligatorio por accesibilidad (usa `null` solo si la imagen es puramente decorativa). |
| `contentScale` | Cómo se ajusta la imagen a su tamaño (`ContentScale.Crop`, `ContentScale.Fit`…). |

```kotlin
Image(
    painter = painterResource(id = R.drawable.foto_perfil),
    contentDescription = "Foto de perfil",
    contentScale = ContentScale.Crop,
    modifier = Modifier.size(80.dp)
)
```

Fíjate en que, para el tamaño, seguimos usando `modifier.size(...)`, tal como ya sabes; `contentScale` solo indica **cómo encajar** la imagen dentro de ese tamaño.

### `Button`: botones y el evento de clic

Un **`Button`** muestra un botón con el estilo de Material. Su único parámetro obligatorio es `onClick`:

| Parámetro | Qué hace |
| :--- | :--- |
| `onClick` | La acción que se ejecuta al tocar el botón. Es el único parámetro obligatorio. |
| `enabled` | Si es `false`, el botón se muestra atenuado y no responde a los toques. |

```kotlin
Button(onClick = { println("Botón presionado") }) {
    Text("Enviar")
}
```

Cada vez que el usuario toca el botón, Compose ejecuta la lambda de `onClick` **una vez** —en este ejemplo, imprime un mensaje en la consola—. Pero fíjate en algo importante: la pantalla **no cambia sola** por tocar el botón. Si dentro de `onClick` quisieras, por ejemplo, aumentar un contador en pantalla, necesitarías guardar ese número en algo que Compose pueda observar; de lo contrario, aunque el valor cambie por dentro, la interfaz nunca se entera y sigue mostrando lo mismo. A esa pieza que falta se le llama **estado**, y es el tema del capítulo de "Estado en Compose": por ahora, quédate con la idea clave de que `onClick` es el lugar donde tu app **reacciona** a un toque.

> [!NOTE]Nota
> `Button` también admite `colors`, `shape` o `contentPadding` para personalizar su apariencia. Por ahora, apóyate en el estilo que trae Material 3 por defecto; en el capítulo de tema aprenderás a ajustar estos aspectos de forma consistente en toda la app, en lugar de repetirlos botón por botón.

### `Card`: agrupar información

Una **`Card`** es una superficie con elevación y esquinas redondeadas, ideal para agrupar información relacionada; es perfecta, por ejemplo, para cada elemento de una lista. No tiene parámetros obligatorios: su contenido se arma como el de cualquier otro composable contenedor, ayudándote de `modifier` para el espaciado interno.

```kotlin
Card {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ana López")
        Text("Diseñadora gráfica")
    }
}
```

> [!NOTE]Nota
> Igual que `Button`, `Card` admite `elevation`, `shape` y `colors` para ajustar su apariencia, y existe en una variante *clickable* que recibe `onClick`. Volveremos a estos parámetros de apariencia en el capítulo de tema.

## Otros componentes habituales

Material 3 trae muchos otros componentes con los que te irás encontrando. No hace falta memorizarlos todos ahora, pero conviene que conozcas los más comunes y cómo se usan.

> [!NOTE]Nota
> Los ejemplos de esta sección guardan lo que el usuario escribe o marca con `remember { mutableStateOf(...) } `. Todavía no explicamos en detalle cómo funciona: lo harás en el próximo capítulo, "Estado en Compose". Por ahora, quédate con la idea de que cada uno de estos componentes necesita un lugar donde **guardar** su valor actual, y una función que se ejecute cuando el usuario lo cambia.

### `Icon`

Un **`Icon`** dibuja un ícono vectorial, muy usado dentro de botones, barras o junto a un texto:

| Parámetro | Qué hace |
| :--- | :--- |
| `imageVector` | El ícono a mostrar, por ejemplo `Icons.Default.Favorite`. Es el único parámetro obligatorio. |
| `contentDescription` | Un texto que lo describe para lectores de pantalla, igual que en `Image`. |
| `tint` | El color con el que se dibuja el ícono. |

```kotlin
Icon(
    imageVector = Icons.Default.Favorite,
    contentDescription = "Marcar como favorito",
    tint = Color.Red
)
```

### `TextField`: la base de los formularios

Un **`TextField`** es un campo de texto editable: el componente con el que construirás prácticamente **todos los formularios** del curso (inicio de sesión, búsqueda, alta de un contacto…).

| Parámetro | Qué hace |
| :--- | :--- |
| `value` | El texto que se muestra **ahora mismo** en el campo. Es obligatorio. |
| `onValueChange` | La función que se ejecuta cada vez que el usuario escribe algo. Es obligatorio. |
| `label` | Una etiqueta que identifica el campo (por ejemplo, "Correo electrónico"). |
| `placeholder` | Un texto de ejemplo que se ve cuando el campo está vacío. |

```kotlin
var nombre by remember { mutableStateOf("") }

TextField(
    value = nombre,
    onValueChange = { nombre = it },
    label = { Text("Nombre") },
    placeholder = { Text("Ingresa tu nombre") }
)
```

Fíjate en el patrón: `value` le dice a `TextField` **qué mostrar**, y `onValueChange` recibe el texto nuevo cada vez que el usuario teclea, para que tú lo guardes (aquí, en `nombre`). Si solo pasaras `value` sin actualizarlo en `onValueChange`, el campo se vería "congelado" y no dejaría escribir, por la misma razón que viste con `Button`: la interfaz no cambia si nadie actualiza el estado que lee. Un formulario real simplemente combina **varios** `TextField` como este, uno por cada dato que pidas.

## Formularios: componentes, modelo y validación

Un formulario no es solo una columna de campos. Es un pequeño flujo de datos con tres responsabilidades que conviene distinguir:

1. **Componentes**: `TextField`, `Checkbox`, `Switch`, `Button` y, cuando corresponda, `DropdownMenu` o `RadioButton`.
2. **Modelo**: una clase que representa los datos que el formulario recopila, sin depender de Compose.
3. **Validación**: reglas que determinan si esos datos se pueden enviar y mensajes que explican cómo corregirlos.

Por ejemplo, el modelo de un contacto puede vivir en un archivo normal de Kotlin:

```kotlin
data class ContactoForm(
    val nombre: String = "",
    val correo: String = "",
    val aceptaTerminos: Boolean = false
)
```

El composable puede mantener el estado editable y elevar el resultado al contenedor, siguiendo el *state hoisting* del capítulo 31:

```kotlin
@Composable
fun ContactoForm(
    formulario: ContactoForm,
    errores: Map<String, String>,
    onFormularioChange: (ContactoForm) -> Unit,
    onEnviar: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = formulario.nombre,
            onValueChange = { onFormularioChange(formulario.copy(nombre = it)) },
            label = { Text("Nombre") },
            isError = errores.containsKey("nombre"),
            supportingText = { errores["nombre"]?.let { Text(it) } }
        )
        OutlinedTextField(
            value = formulario.correo,
            onValueChange = { onFormularioChange(formulario.copy(correo = it)) },
            label = { Text("Correo electrónico") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = errores.containsKey("correo"),
            supportingText = { errores["correo"]?.let { Text(it) } }
        )
        Button(
            onClick = onEnviar,
            enabled = errores.isEmpty() && formulario.aceptaTerminos
        ) {
            Text("Guardar")
        }
    }
}
```

La validación no debería depender de que el usuario pulse el botón. Puedes validar al salir de un campo para ofrecer una corrección temprana, y volver a validar al enviar para no confiar únicamente en el estado visual. Una función pura resulta fácil de probar:

```kotlin
fun validarContacto(formulario: ContactoForm): Map<String, String> = buildMap {
    if (formulario.nombre.isBlank()) put("nombre", "Escribe un nombre")
    if (!formulario.correo.contains("@")) put("correo", "Escribe un correo válido")
    if (!formulario.aceptaTerminos) put("terminos", "Debes aceptar los términos")
}
```

En una pantalla real, el contenedor conserva `ContactoForm` y los errores, y decide cuándo llamar a `validarContacto`. El composable del formulario solo muestra valores, errores y eventos. Para formularios largos, usa `rememberSaveable` para conservar lo escrito durante una recreación de la `Activity`; la validación definitiva y el envío deberán pasar después a un `ViewModel`, como se verá en la Parte VII.

> [!WARNING]Advertencia
> `isError` cambia el aspecto del campo, pero no sustituye al texto del error ni a una validación real. Tampoco valides solo en la interfaz: la capa que guarda o envía los datos debe volver a comprobar sus reglas.

### `Checkbox` y `Switch`

Un **`Checkbox`** es una casilla de verificación; un **`Switch`** es un interruptor de encendido/apagado. Ambos comparten los mismos parámetros:

| Parámetro | Qué hace |
| :--- | :--- |
| `checked` | Si está marcado o activado. Es obligatorio. |
| `onCheckedChange` | La función que se ejecuta cuando el usuario lo toca, con el nuevo valor. Es obligatorio. |

```kotlin
var aceptaTerminos by remember { mutableStateOf(false) }

Row(verticalAlignment = Alignment.CenterVertically) {
    Checkbox(
        checked = aceptaTerminos,
        onCheckedChange = { aceptaTerminos = it }
    )
    Text("Acepto los términos y condiciones")
}
```

Para un `Switch`, el uso es idéntico: solo cambia el componente.

```kotlin
var notificacionesActivas by remember { mutableStateOf(true) }

Switch(
    checked = notificacionesActivas,
    onCheckedChange = { notificacionesActivas = it }
)
```

Fíjate en el patrón que se repite en los tres: cada componente interactivo recibe los **datos a mostrar** (`value`, `checked`) y una **función de devolución de llamada** (`onValueChange`, `onCheckedChange`) para reaccionar a la interacción del usuario, igual que viste con `Button` y su `onClick`. Volverás a encontrarte con varios de estos componentes en capítulos posteriores, sobre todo al construir formularios.

## Resumen

En este capítulo conociste los componentes de Material 3:

- **Material Design** es el sistema de diseño de Google; **Material 3** es su versión actual, y Compose lo incluye con componentes y un tema listos para usar.
- Los **componentes básicos** son `Text`, `Image`, `Button` y `Card`. Para su tamaño, espaciado y fondo sigues usando `modifier`, tal como ya sabías; sus parámetros propios (`text`, `painter`, `onClick`…) cubren lo que `modifier` no puede resolver.
- **`Button`** ejecuta una acción en `onClick` al tocarlo; por sí solo no actualiza la pantalla, eso requiere **estado** (lo verás más adelante).
- Componentes como `Text`, `Button` o `Card` también admiten parámetros propios de apariencia (`color`, `fontSize`, `colors`, `shape`…); son válidos, pero el capítulo de tema te mostrará la forma recomendada de aplicarlos de forma coherente en toda la app.
- Hay muchos más componentes: `Icon` (íconos vectoriales), `TextField` (la base de los **formularios**), `Checkbox` y `Switch` (selección de opciones), todos con un estilo coherente entre sí y el mismo patrón de datos + función de devolución de llamada.
- Un formulario combina **componentes**, un **modelo independiente de Compose** y una validación que produce errores comprensibles. El estado se eleva al contenedor y el formulario recibe valores y eventos.

Con estas piezas ya puedes construir pantallas completas. En el próximo capítulo aprenderás a montar el esqueleto de una pantalla con `Scaffold` y a **organizar** su contenido con `Column`, `Row` y `LazyColumn`.
