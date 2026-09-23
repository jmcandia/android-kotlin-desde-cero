# Capítulo 28: Tu primer composable: `@Composable`, `@Preview` y parámetros

## Introducción

Ya sabes que `MainActivity` llama a `setContent { }` en `onCreate`, y que la `Activity` es solo el contenedor de la pantalla. En el mapa del capítulo anterior, esta parte del curso se ocupa de la primera zona: la **interfaz**. En este capítulo escribirás lo que va **dentro** de `setContent { }`: tu primer componente de interfaz con Jetpack Compose —un `@Composable`—. Aprenderás a verlo al instante con `@Preview`, sin siquiera ejecutar la app, y a hacerlo reutilizable con **parámetros**.

## Tu primer `@Composable`

Pasemos ahora a lo que va dentro de `setContent { }`: la interfaz, construida con **Jetpack Compose**.

Compose es un kit de herramientas **declarativo**: en lugar de crear y modificar elementos de pantalla paso a paso, tú **describes** cómo debe verse la interfaz, y Compose se encarga de dibujarla. Esa descripción se hace con **funciones componibles** (*composables*): funciones normales de Kotlin marcadas con la anotación **`@Composable`**.

Aquí tienes tu primer composable, que muestra un texto en pantalla:

```kotlin
@Composable
fun Saludo() {
    Text("¡Hola, Android!")
}
```

Analicémoslo:

- La anotación `@Composable` le indica a Compose que esta función **describe una parte de la interfaz**.
- `Text(...)` es, a su vez, otro composable: uno que ya viene con Compose y que muestra texto en pantalla.

Así se construye una interfaz en Compose: **componiendo** unas funciones dentro de otras. Un composable puede llamar a otros composables, y así se van armando pantallas complejas a partir de piezas simples.

> [!NOTE]Nota
> Por convención, los nombres de las funciones componibles se escriben con **mayúscula inicial** (`Saludo`, `Text`), a diferencia de las funciones normales de Kotlin, que usan minúscula inicial.

## Vistas previas con `@Preview`

Una de las mejores cosas de Compose es que puedes **ver** un composable directamente en Android Studio, sin tener que ejecutar la app en un emulador o dispositivo. Para eso está la anotación **`@Preview`**.

Creas una función componible aparte, la marcas con `@Preview` (además de `@Composable`) y, dentro, llamas al composable que quieres previsualizar:

```kotlin
@Preview
@Composable
fun SaludoPreview() {
    Saludo()
}
```

Android Studio mostrará, en un panel junto al editor, cómo se ve `Saludo`. Cada vez que cambies el código, la vista previa se actualiza. Esto hace que construir interfaces sea mucho más rápido: ves el resultado al instante.

## Juntando todo

Volvamos a `MainActivity` del capítulo anterior. Ahora puedes entender cómo encaja todo: en `onCreate`, `setContent { }` recibe el composable que será la interfaz de la pantalla:

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Saludo() // nuestra interfaz
        }
    }
}

@Composable
fun Saludo() {
    Text("¡Hola, Android!")
}

@Preview
@Composable
fun SaludoPreview() {
    Saludo()
}
```

Cuando el usuario abre la app, Android crea `MainActivity`, llama a `onCreate` y `setContent` dibuja el composable `Saludo`. Ese texto es tu primera interfaz hecha con Compose.

## Composables con parámetros

Un composable es una función, así que —como cualquier función— puede recibir **parámetros**. Esto es lo que los hace reutilizables. En vez de un saludo fijo, podemos parametrizar el nombre:

```kotlin
@Composable
fun Saludo(nombre: String) {
    Text("¡Hola, $nombre!")
}
```

Ahora el mismo composable sirve para saludar a cualquiera:

```kotlin
Saludo("Ana")   // muestra: ¡Hola, Ana!
Saludo("Diego") // muestra: ¡Hola, Diego!
```

Y, tal como una función puede llamar a otras, un composable puede llamar a **otros composables**. Así se construye una interfaz en Compose: componiendo piezas pequeñas para formar pantallas complejas.

> [!NOTE]Nota
> Por ahora combinaremos composables de a uno. Para **organizar varios elementos** en pantalla (uno debajo de otro, en fila, etc.) necesitarás los *layouts*, que veremos en un capítulo próximo.

## Resumen

En este capítulo diste tus primeros pasos con Jetpack Compose:

- **Jetpack Compose** es declarativo: describes la interfaz con funciones **`@Composable`**. `Text` es un composable básico, y los composables se **componen** unos dentro de otros.
- La anotación **`@Preview`** te permite ver un composable en Android Studio sin ejecutar la app.
- En `MainActivity`, `setContent { }` recibe el composable que será la interfaz de la pantalla.
- Los composables pueden recibir **parámetros**, lo que los hace reutilizables.

En el próximo capítulo aprenderás a ajustar la apariencia de cada composable con **`Modifier`** y a organizar varios en pantalla con los layouts `Column`, `Row` y `Box`.
