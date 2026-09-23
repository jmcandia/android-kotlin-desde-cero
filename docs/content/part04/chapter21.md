# Capítulo 21: Lambdas a fondo: receptores, funciones de alcance y delegación

## Introducción

Ya conoces las lambdas. En el capítulo 10 viste que una lambda es una función sin nombre que se puede guardar en una variable, que tiene un **tipo de función** como `(Int) -> Int` y que una **función de orden superior** recibe otras funciones como parámetro. En el capítulo 12 las usaste a diario con `map`, `filter` y `forEach`.

En este capítulo das el paso siguiente. Verás cuatro ideas que se apoyan en las lambdas y que aparecen por todas partes en Android:

- Las **referencias a funciones** y las lambdas que **capturan** variables.
- Las **lambdas con receptor**, la pieza con la que se construye Jetpack Compose.
- Las **funciones de alcance** (`let`, `apply`, `also`, `run` y `with`).
- La **delegación con `by`**, que usarás en cuanto guardes estado en una pantalla.

Si alguna vez miraste código de Compose y te preguntaste por qué `Column { ... }` o `var contador by remember { ... }` se escriben así, este capítulo te da la respuesta.

## Referencias a funciones

A veces la lambda que quieres pasar solo llama a una función que ya existe:

```kotlin
fun esPar(numero: Int): Boolean = numero % 2 == 0

val pares = listOf(1, 2, 3, 4).filter { numero -> esPar(numero) }
```

En ese caso puedes pasar **la función misma**, sin envolverla en una lambda, con el operador `::`:

```kotlin
val pares = listOf(1, 2, 3, 4).filter(::esPar) // [2, 4]
```

`::esPar` es una **referencia a función**: un valor de tipo `(Int) -> Boolean` que apunta a `esPar`. También puedes referenciar funciones de una clase con la forma `Tipo::función`:

```kotlin
val nombres = listOf("ana", "diego")
println(nombres.map(String::uppercase)) // [ANA, DIEGO]
```

> [!NOTE]Nota
> En Java esto se llama *method reference* y se escribe igual: `String::toUpperCase`. Más adelante verás también `::class` (por ejemplo, `MainActivity::class`); no es una referencia a función, sino una referencia a la **clase**, pero usa el mismo operador.

## Lambdas que capturan variables

Una lambda puede usar las variables que están **alrededor** de donde se escribe. Se dice que las **captura**:

```kotlin
fun main() {
    var total = 0
    val precios = listOf(1200, 3500, 800)

    precios.forEach { precio -> total += precio }

    println(total) // 5500
}
```

La lambda lee y **modifica** `total`, una variable que no es suya. Esto también funciona cuando una función **devuelve** una lambda: la variable capturada sigue viva mientras exista la lambda.

```kotlin
fun crearContador(): () -> Int {
    var cuenta = 0
    return { ++cuenta }
}

fun main() {
    val siguiente = crearContador()
    println(siguiente()) // 1
    println(siguiente()) // 2
    println(siguiente()) // 3
}
```

`crearContador` terminó hace rato, pero la lambda que devolvió sigue recordando su propia `cuenta`.

> [!NOTE]Nota
> En Java, una lambda solo puede usar variables locales *effectively final*: puede leerlas, pero no modificarlas. En Kotlin sí puede modificarlas, como hace `total += precio` en el ejemplo.

## Lambdas como parámetros opcionales

Un parámetro de tipo función funciona como cualquier otro parámetro, así que puede tener un **valor por defecto** o aceptar `null`:

```kotlin
fun mostrarBoton(
    texto: String,
    alTocar: () -> Unit = {}       // por defecto, no hace nada
) {
    println("[ $texto ]")
    alTocar()
}

fun mostrarAviso(
    mensaje: String,
    alCerrar: (() -> Unit)? = null // puede no haber acción
) {
    println(mensaje)
    alCerrar?.invoke()             // se llama solo si no es null
}
```

Para un tipo de función que acepta `null`, los paréntesis son obligatorios: `(() -> Unit)?`. Sin ellos, `() -> Unit?` significaría otra cosa: una función que **devuelve** un `Unit?`. Y como no puedes llamar directamente a algo que podría ser `null`, se usa la llamada segura con `invoke()`, que es la forma explícita de ejecutar una función.

Vas a ver este patrón en todos los componentes de Compose: `onClick: () -> Unit` en un botón, o parámetros opcionales como `trailingIcon: (@Composable () -> Unit)? = null` en un campo de texto.

## Lambdas con receptor

Esta es la idea más importante del capítulo. Recuerda las **funciones de extensión** del capítulo anterior: dentro de `fun Int.esPar()`, `this` es el número sobre el que llamas la función. Una **lambda con receptor** es lo mismo, pero en forma de lambda.

Su tipo se escribe con el tipo del receptor, un punto y los paréntesis: `StringBuilder.() -> Unit` es "una lambda que se ejecuta **sobre** un `StringBuilder` y no devuelve nada". Dentro de ella, `this` es ese `StringBuilder`, así que puedes llamar a sus funciones directamente.

Kotlin trae una función que la usa, `buildString`:

```kotlin
val saludo = buildString {
    append("Hola, ")
    append("Ana")
    append("!")
}
println(saludo) // Hola, Ana!
```

`append` no es una función suelta: es una función de `StringBuilder`. Puedes llamarla sin prefijo porque la lambda se ejecuta con un `StringBuilder` como receptor. Así podrías escribir tu propia versión:

```kotlin
fun miBuildString(construir: StringBuilder.() -> Unit): String {
    val builder = StringBuilder()
    builder.construir()   // ejecuta la lambda con builder como receptor
    return builder.toString()
}
```

La función crea el receptor, ejecuta la lambda **sobre** él y devuelve el resultado. Quien la usa solo escribe el contenido entre llaves.

### Construir tus propios «bloques»

Con este mecanismo puedes diseñar funciones que se leen casi como un lenguaje propio. Por ejemplo, para describir una tarjeta de contacto:

```kotlin
class Tarjeta {
    val lineas = mutableListOf<String>()

    fun titulo(texto: String) {
        lineas.add(texto.uppercase())
    }

    fun dato(etiqueta: String, valor: String) {
        lineas.add("$etiqueta: $valor")
    }
}

fun tarjeta(contenido: Tarjeta.() -> Unit): Tarjeta {
    val tarjeta = Tarjeta()
    tarjeta.contenido()
    return tarjeta
}

fun main() {
    val ficha = tarjeta {
        titulo("Ana Pérez")
        dato("Correo", "ana@ejemplo.com")
        dato("Ciudad", "Santiago")
    }
    ficha.lineas.forEach(::println)
}
```

Dentro de las llaves, `titulo` y `dato` están disponibles porque el receptor es una `Tarjeta`. Fuera de ellas, no existen: no puedes llamar a `dato(...)` suelto en `main`.

### Por qué importa para Compose

Así está construido Jetpack Compose. Cuando escribas esto en la Parte VII:

```kotlin
Row {
    Text("Izquierda", modifier = Modifier.weight(1f))
    Text("Derecha", modifier = Modifier.weight(1f))
}
```

el contenido de `Row` es una lambda con receptor de tipo `RowScope.() -> Unit`. Por eso, dentro de las llaves de un `Row`, tienes disponible `Modifier.weight`, que es una función de `RowScope`; fuera de un `Row` o un `Column`, el compilador no la encuentra. Es el mismo mecanismo que hace que `dato(...)` solo exista dentro de `tarjeta { }`.

## Funciones de alcance

Kotlin trae cinco funciones pequeñas que ejecutan un bloque de código sobre un objeto: `let`, `run`, `with`, `apply` y `also`. Se llaman **funciones de alcance** (*scope functions*) porque, dentro del bloque, el objeto está disponible con un nombre corto.

Se diferencian en dos cosas:

- **Cómo llamas al objeto dentro del bloque:** como `it` (la lambda recibe el objeto como parámetro) o como `this` (es una lambda con receptor).
- **Qué devuelven:** el resultado del bloque o el propio objeto.

| Función | El objeto es… | Devuelve | Uso típico |
| :--- | :--- | :--- | :--- |
| `let` | `it` | El resultado del bloque | Ejecutar algo solo si el valor no es `null` (`?.let`) |
| `apply` | `this` | El objeto | Configurar un objeto recién creado |
| `also` | `it` | El objeto | Hacer algo adicional sin cambiar el flujo (registrar, validar) |
| `run` | `this` | El resultado del bloque | Calcular un valor a partir de un objeto |
| `with` | `this` | El resultado del bloque | Igual que `run`, pero recibe el objeto como argumento |

### `let`: sobre todo con valores que pueden ser `null`

`let` brilla combinada con la llamada segura `?.` del capítulo 13: el bloque se ejecuta **solo** si el valor no es `null`, y dentro de él `it` ya es de un tipo no anulable.

```kotlin
val correo: String? = buscarCorreo("Ana")

correo?.let {
    println("Enviando mensaje a $it")
}
```

Si `correo` es `null`, no pasa nada. Verás este patrón en los formularios de Compose, para mostrar un mensaje de error solo cuando existe: `error?.let { Text(it) }`.

### `apply`: configurar un objeto

`apply` ejecuta el bloque con el objeto como receptor y **devuelve el mismo objeto**. Es ideal para crear y configurar algo en una sola expresión:

```kotlin
val invitados = mutableListOf<String>().apply {
    add("Ana")
    add("Diego")
    sort()
}
```

### `also`: un paso adicional

`also` también devuelve el objeto, pero lo recibe como `it`. Se usa para agregar un paso que no cambia el resultado, como registrar lo que ocurre:

```kotlin
val total = calcularTotal(carrito).also {
    println("Total calculado: $it")
}
```

### `run` y `with`: calcular un valor

`run` y `with` ejecutan un bloque con el objeto como receptor y devuelven lo que calcule el bloque. Son intercambiables; la diferencia es solo de escritura:

```kotlin
val resumen = contacto.run { "$nombre <$correo>" }
val resumen2 = with(contacto) { "$nombre <$correo>" }
```

> [!TIP]Sugerencia
> No hace falta memorizar la tabla. En la práctica, `?.let` y `apply` cubren la mayoría de los casos. Si dudas entre usar una función de alcance y escribir el código de la forma normal, elige la forma normal: encadenar varias funciones de alcance hace el código más difícil de leer, no más elegante (recuerda KISS).

## Delegación con `by`

La palabra clave `by` significa "esto lo resuelve otro". Kotlin la usa en dos situaciones.

### Propiedades delegadas

Una **propiedad delegada** no guarda su valor por sí misma: se lo pide a otro objeto, el **delegado**, cada vez que la lees o la modificas. El ejemplo más común de Kotlin es `lazy`, que calcula el valor la **primera vez** que se usa y lo recuerda:

```kotlin
class Informe(private val datos: List<Int>) {
    val promedio: Double by lazy {
        println("Calculando…")
        datos.average()
    }
}

fun main() {
    val informe = Informe(listOf(4, 6, 8))
    println(informe.promedio) // Calculando…  y luego 6.0
    println(informe.promedio) // 6.0 (ya no vuelve a calcular)
}
```

Por dentro, el delegado es un objeto con una función `getValue` (y `setValue`, si la propiedad es `var`). Kotlin traduce cada lectura de `promedio` en una llamada a `getValue` del delegado. No necesitas escribir delegados propios para seguir el curso; basta con reconocer el patrón.

Lo encontrarás muy pronto en Compose:

```kotlin
var contador by remember { mutableStateOf(0) }
```

Aquí `contador` delega en un objeto de estado de Compose. Gracias a `by`, lees y escribes `contador` como una variable normal, en lugar de escribir `contador.value` cada vez. Lo verás en detalle en el capítulo de estado.

### Delegación de interfaces

`by` también sirve para que una clase implemente una interfaz **delegando** el trabajo en otro objeto que ya la implementa:

```kotlin
interface Registro {
    fun registrar(mensaje: String)
}

class RegistroConsola : Registro {
    override fun registrar(mensaje: String) = println(mensaje)
}

class Servicio(registro: Registro) : Registro by registro {
    fun procesar() {
        registrar("Procesando…") // lo resuelve el objeto delegado
    }
}

fun main() {
    Servicio(RegistroConsola()).procesar() // Procesando…
}
```

`Servicio` cumple la interfaz `Registro` sin escribir `registrar`: Kotlin genera esa función y la reenvía a `registro`. Es una alternativa a la herencia: `Servicio` reutiliza el comportamiento de `RegistroConsola` sin heredar de ella, y podrías pasarle cualquier otra implementación de `Registro`.

## Resumen

En este capítulo profundizaste en las lambdas:

- Con **`::`** puedes pasar una función existente como valor (`filter(::esPar)`, `map(String::uppercase)`).
- Una lambda **captura** las variables de su entorno y, a diferencia de Java, puede modificarlas. Una función puede devolver una lambda que recuerda su propio estado.
- Los parámetros de tipo función pueden tener **valor por defecto** (`= {}`) o aceptar `null` (`(() -> Unit)?`), y se invocan con `?.invoke()`.
- Una **lambda con receptor** (`T.() -> Unit`) se ejecuta sobre un objeto, al que accede como `this`. Así funcionan `buildString` y el contenido de `Row` y `Column` en Compose.
- Las **funciones de alcance** ejecutan un bloque sobre un objeto: `let` (sobre todo con `?.`), `apply` (configurar), `also` (paso adicional), `run` y `with` (calcular un valor).
- **`by`** delega: en propiedades (`by lazy`, `by remember`) y en interfaces (`: Registro by registro`).

Con esto **completas la programación orientada a objetos** y las características del lenguaje que necesitas para Android. En la próxima parte darás un salto importante: la **asincronía con coroutines**, imprescindible para que una app pueda pedir datos a internet sin congelarse.
