# Capítulo 20: Genéricos y funciones de extensión

## Introducción

Has recorrido un largo camino: dominas los fundamentos de Kotlin y la programación orientada a objetos. Para cerrar esta parte, verás dos herramientas que hacen tu código mucho más **expresivo y reutilizable**, y que aparecen por todas partes en el desarrollo Android moderno:

- Los **genéricos**, para escribir código que funcione con cualquier tipo de dato.
- Las **funciones de extensión**, para añadir funciones a clases que ya existen.

Ya te has cruzado con las dos sin conocerlas del todo: usaste `List<String>` (genéricos) y llamaste funciones como `.first()` sobre una lista (extensiones). Ahora entenderás cómo funcionan por dentro.

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

> [!NOTE]Nota
> En Java, para "añadir" comportamiento a una clase que no controlas, sueles crear métodos utilitarios estáticos (`Utilidades.esPar(numero)`). Las funciones de extensión de Kotlin logran lo mismo, pero se leen mucho mejor: `numero.esPar()`.

## Resumen

En este capítulo conociste dos herramientas que hacen tu código más expresivo:

- Los **genéricos** (`<T>`) permiten escribir clases y funciones que trabajan con cualquier tipo, manteniendo la seguridad de tipos. Así funcionan `List<T>` y las demás colecciones.
- Las **funciones de extensión** añaden funciones nuevas a tipos existentes (`fun Int.esPar()`), y dentro de ellas `this` es el objeto receptor. Se leen de forma natural: `numero.esPar()`.

En el próximo capítulo, el último de esta parte, volverás a las **lambdas** para verlas a fondo: lambdas con receptor, funciones de alcance y delegación con `by`. Son la base sobre la que se construye Jetpack Compose.
