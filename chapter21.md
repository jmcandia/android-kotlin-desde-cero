# Capítulo 21: Genéricos, funciones de extensión y lambdas

- [Introducción](#introducción)
- [Genéricos](#genéricos)
- [Funciones de extensión](#funciones-de-extensión)
- [Lambdas a fondo](#lambdas-a-fondo)
  - [Los tipos de función](#los-tipos-de-función)
  - [Funciones de orden superior](#funciones-de-orden-superior)
  - [La lambda al final](#la-lambda-al-final)
- [Resumen](#resumen)

---

## Introducción

Has recorrido un largo camino: dominas los fundamentos de Kotlin y la programación orientada a objetos. Para cerrar esta parte, verás tres herramientas que no son imprescindibles para empezar, pero que hacen tu código mucho más **expresivo y reutilizable**, y que aparecen por todas partes en el desarrollo Android moderno:

- Los **genéricos**, para escribir código que funcione con cualquier tipo de dato.
- Las **funciones de extensión**, para añadir funciones a clases que ya existen.
- Las **lambdas a fondo**, retomando y profundizando lo que viste con las colecciones.

Ya te has cruzado con las tres sin conocerlas del todo: usaste `List<String>` (genéricos), llamaste métodos como `.first()` y escribiste `map { it * 2 }` (lambdas). Ahora entenderás cómo funcionan por dentro.

## Genéricos

Cuando escribiste `List<String>` o `List<Int>`, ese `<...>` son los **genéricos** en acción. Los genéricos te permiten escribir **una sola** pieza de código que funciona con **muchos tipos** distintos, sin perder la seguridad de tipos.

Imagina que quieres una clase "caja" que guarde un valor. Si hicieras una `CajaDeInt`, una `CajaDeString`, etcétera, estarías repitiendo el mismo código (recuerda DRY). En su lugar, defines una clase **genérica**, usando un **parámetro de tipo** entre `<>` (por convención se llama `T`, de *type*):

```kotlin
class Caja<T>(val contenido: T)
```

Aquí `T` es un marcador de posición para un tipo real, que se decide al crear el objeto:

```kotlin
val cajaNumero = Caja(5)     // T es Int
val cajaTexto = Caja("hola") // T es String

println(cajaNumero.contenido) // 5
println(cajaTexto.contenido)  // hola
```

Kotlin recuerda el tipo de cada caja: `cajaNumero.contenido` es un `Int` y `cajaTexto.contenido` es un `String`. Eso es lo valioso: el mismo código sirve para cualquier tipo, pero cada uso mantiene su tipo concreto.

Las **funciones** también pueden ser genéricas. Por ejemplo, una que devuelva el primer elemento de cualquier lista:

```kotlin
fun <T> primero(lista: List<T>): T = lista[0]

println(primero(listOf(10, 20, 30)))    // 10  (T es Int)
println(primero(listOf("a", "b", "c"))) // a   (T es String)
```

Así funcionan por dentro `List<T>` y todas las colecciones que ya usaste: son clases genéricas.

## Funciones de extensión

A veces querrías añadir una función a una clase que **ya existe** —`String`, `Int` o una clase de una librería— pero que no puedes o no quieres modificar. Las **funciones de extensión** de Kotlin te permiten hacer exactamente eso: agregar funciones nuevas a un tipo existente.

Para definir una, escribes el tipo que quieres extender, un punto y el nombre de la función. Por ejemplo, añadamos a `Int` una función que diga si es par:

```kotlin
fun Int.esPar(): Boolean = this % 2 == 0
```

Dentro de la función, `this` se refiere al objeto sobre el que la llamas. Y la usas como si fuera un método más de `Int`:

```kotlin
println(4.esPar()) // true
println(7.esPar()) // false
```

Es importante entender que esto no modifica realmente la clase `Int` (no le añades nada por dentro): es una comodidad del lenguaje que hace que tu código se lea de forma natural. En el segundo capítulo mencionamos las funciones de extensión como una de las características distintivas de Kotlin; esto es lo que eran.

> [!NOTE]
> En Java, para "añadir" comportamiento a una clase que no controlas, sueles crear métodos utilitarios estáticos (`Utilidades.esPar(numero)`). Las funciones de extensión de Kotlin logran lo mismo, pero se leen mucho mejor: `numero.esPar()`.

## Lambdas a fondo

En el capítulo de colecciones usaste lambdas de forma práctica: pequeñas funciones entre llaves, como `{ it * 2 }`. Ahora veamos cómo funcionan realmente, porque son una de las herramientas más potentes de Kotlin.

### Los tipos de función

Así como un `Int` es un tipo y un `String` es otro, **una función también tiene un tipo**. Ese tipo describe qué recibe y qué devuelve. Por ejemplo:

- `(Int) -> Int` es el tipo de una función que recibe un `Int` y devuelve un `Int`.
- `(Int, Int) -> Int` recibe dos `Int` y devuelve un `Int`.
- `(String) -> Unit` recibe un `String` y no devuelve nada útil.

Puedes guardar una lambda en una variable, indicando su tipo de función:

```kotlin
val doble: (Int) -> Int = { numero -> numero * 2 }
println(doble(5)) // 10
```

Aquí `doble` es una variable cuyo valor es una función. La invocas como a cualquier otra: `doble(5)`.

### Funciones de orden superior

Como las funciones son valores, puedes **pasarlas como argumento** a otras funciones. Una función que recibe (o devuelve) otra función se llama **función de orden superior**.

Por ejemplo, una función que aplica una operación a dos números, donde la operación es una lambda que le pasas:

```kotlin
fun operar(a: Int, b: Int, operacion: (Int, Int) -> Int): Int {
    return operacion(a, b)
}
```

El parámetro `operacion` es de tipo función `(Int, Int) -> Int`. Ahora puedes decidir la operación al llamarla:

```kotlin
val suma = operar(3, 4, { x, y -> x + y })
println(suma) // 7

val producto = operar(3, 4, { x, y -> x * y })
println(producto) // 12
```

La misma función `operar` hace cosas distintas según la lambda que le pases. Esto es exactamente lo que hacen `map`, `filter` y compañía: son funciones de orden superior que reciben una lambda.

### La lambda al final

Kotlin ofrece un atajo: si el **último** parámetro de una función es una lambda, puedes escribirla **fuera** de los paréntesis. Así, esta llamada:

```kotlin
operar(3, 4, { x, y -> x + y })
```

se puede escribir de forma más limpia:

```kotlin
operar(3, 4) { x, y -> x + y }
```

¡Y aquí se cierra el círculo! Ahora entiendes por qué `lista.map { it * 2 }` se escribe así: `map` es una función de orden superior cuyo último (y único) parámetro es una lambda, y por eso la lambda va fuera de los paréntesis, que, al quedar vacíos, desaparecen.

## Resumen

En este capítulo conociste tres herramientas que hacen tu código más expresivo:

- Los **genéricos** (`<T>`) permiten escribir clases y funciones que trabajan con cualquier tipo, manteniendo la seguridad de tipos. Así funcionan `List<T>` y las demás colecciones.
- Las **funciones de extensión** añaden funciones nuevas a tipos existentes (`fun Int.esPar()`), y dentro de ellas `this` es el objeto receptor. Se leen de forma natural: `numero.esPar()`.
- Las **lambdas** tienen un **tipo de función** (`(Int) -> Int`) y pueden guardarse en variables y pasarse como argumento. Una función que recibe otra función es una **función de orden superior**. Si la lambda es el último parámetro, se escribe fuera de los paréntesis, que es por lo que `map { ... }` luce como luce.

Con esto **completas la programación orientada a objetos** y las características esenciales del lenguaje Kotlin. En la próxima parte del curso darás un salto emocionante: la **asincronía con coroutines**, imprescindible para que la aplicación pueda pedir datos a internet sin congelarse.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter20.md">← Anterior</a>
        </td>
        <td style="text-align: center;">
            <a href="/README.md">Ir al índice</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter22.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
