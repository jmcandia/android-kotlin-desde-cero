# Capítulo 7: Trabajar con cadenas

## Introducción

Como ya sabes, Kotlin cuenta con numerosos tipos de datos, como `String`, `Int` y `Double`. En los capítulos anteriores usaste cadenas muchas veces: para mostrar mensajes, leer datos y construir plantillas. En este capítulo nos centraremos en el tipo `String` y aprenderemos a **trabajar** con él más de cerca: medir su longitud, unir cadenas entre sí y repetirlas.

Recuerda que una cadena es una secuencia de cero o más caracteres entre comillas dobles; por ejemplo, `"Ana"` o `""`. Seguro que usarás cadenas con frecuencia en tus futuros proyectos, así que estos conceptos básicos te acompañarán durante todo el curso.

## La longitud de una cadena

A menudo necesitarás saber cuántos caracteres tiene una cadena. La **longitud** de una cadena es, precisamente, el número de caracteres que hay entre las comillas dobles. Para obtenerla, usa `.length`, que devuelve un valor de tipo `Int`:

```kotlin
val language = "Kotlin"
println(language.length) // 6

val empty = ""
println(empty.length) // 0
```

Fíjate en que `.length` no lleva paréntesis: es una **propiedad** de la cadena, no una función que se invoca.

## Concatenación de cadenas

Otra operación habitual con cadenas es la **concatenación**, que consiste en construir una cadena uniendo otras. Para concatenar dos cadenas, usamos el operador `+`:

```kotlin
val str1 = "ab"
val str2 = "cde"
val result = str1 + str2 // "abcde"
```

Al concatenar dos cadenas, se crea una **cadena nueva**; las originales no se modifican:

```kotlin
val one = "1"
val two = "2"
val twelve = one + two
println(one)    // 1, sin cambios
println(two)    // 2, sin cambios
println(twelve) // 12
```

Puedes concatenar varias cadenas en la misma expresión:

```kotlin
val firstName = "Ana"
val lastName = "Gómez"
val fullName = firstName + " " + lastName // Ana Gómez
```

> [!TIP]
> La concatenación con `+` funciona bien, pero cuando construyes un texto a partir de varias variables suele quedar más legible usar las **plantillas de cadena** que viste en el capítulo anterior. Por ejemplo, `"$firstName $lastName"` se lee mejor que `firstName + " " + lastName`.

### El orden importa

Al mezclar cadenas con otros tipos, el orden de los operandos es decisivo. Observa este ejemplo, que **no** funciona porque el primer operando es un número:

```kotlin
val errorString = 10 + "abc" // ¡un error aquí!
```

En cambio, esta otra situación sí funciona:

```kotlin
val stringAndNumbers = "abc" + 11 + 22
println(stringAndNumbers) // abc1122
```

¿Por qué? La concatenación se evalúa de **izquierda a derecha**. En el segundo caso, primero se une `"abc"` con `11`, lo que produce la cadena `"abc11"`; luego, a esa cadena se le añade `22`, dando `"abc1122"`. En el primer caso, en cambio, Kotlin intenta primero sumar `10 + "abc"`, es decir, sumar un número con un texto, y eso no tiene sentido: de ahí el error.

### Concatenar caracteres y números

Puedes concatenar un carácter con una cadena para obtener una nueva cadena:

```kotlin
val charPlusString = 'a' + "bc"
println(charPlusString) // abc

val stringPlusChar = "de" + 'f'
println(stringPlusChar) // def
```

Y, mientras el resultado ya sea una cadena, puedes seguir añadiéndole valores de otros tipos:

```kotlin
val charPlusStringPlusInt = 'a' + "bc" + 123
println(charPlusStringPlusInt) // abc123
```

Más adelante veremos con más detalle cómo trabajar con caracteres; por ahora, basta con que recuerdes que puedes concatenar caracteres, cadenas y números para obtener un valor de tipo `String`.

## Repetir una cadena

Si necesitas repetir una cadena dos o más veces, no hace falta escribirla varias veces: Kotlin ofrece la función `repeat`:

```kotlin
print("Hola".repeat(4)) // HolaHolaHolaHola
```

Ahora imagina que un amigo, un desarrollador con mucha experiencia, te revela su secreto para convertirte en mejor programador:

```text
POR HACER:
- Comer
- Dormir
- Programar
- Repetir
```

Intentemos convertir ese mensaje en un fragmento de código que muestre tu horario para cada día de la semana:

```kotlin
println("Comer. Dormir. Programar.\n".repeat(7)) // \n produce un salto de línea
```

Y aquí tienes tu horario semanal:

```text
Comer. Dormir. Programar.
Comer. Dormir. Programar.
Comer. Dormir. Programar.
Comer. Dormir. Programar.
Comer. Dormir. Programar.
Comer. Dormir. Programar.
Comer. Dormir. Programar.
```

## Cadenas sin formato

A veces necesitas incluir símbolos especiales, como tabulaciones o comillas, dentro de una cadena. Puedes hacerlo con la ayuda de **secuencias de escape**, que empiezan con una barra invertida `\`. Por ejemplo, `\"` inserta una comilla doble y `\n` un salto de línea:

```kotlin
// Muestra: 'H' es la primera letra de la cadena "Hola, mundo".
println("'H' es la primera letra de la cadena \"Hola, mundo\".")
```

Esto se ve un poco denso. Si tienes que escribir un texto largo con saltos de línea y caracteres especiales, puede resultar difícil de leer.

En estos casos, puedes usar una **cadena sin formato** (*raw string*). Una cadena sin formato puede contener saltos de línea y cualquier otro carácter sin necesidad de escaparlos. Solo tienes que escribir el texto entre comillas triples (`"""`):

```kotlin
val largeString = """
    Esta es la casa que construyó Jack.

    Esta es la malta que había en la casa que construyó Jack.

    Esta es la rata que se comió la malta
    que había en la casa que construyó Jack.

    Este es el gato
    que mató a la rata que se comió la malta
    que había en la casa que construyó Jack.
""".trimIndent() // elimina la primera y la última línea vacías y recorta la sangría
print(largeString)
```

Este texto se muestra así:

```text
Esta es la casa que construyó Jack.

Esta es la malta que había en la casa que construyó Jack.

Esta es la rata que se comió la malta
que había en la casa que construyó Jack.

Este es el gato
que mató a la rata que se comió la malta
que había en la casa que construyó Jack.
```

Como puedes ver, también usamos la función `.trimIndent()`. Esta función recorta todas las líneas según su sangría mínima común y elimina la primera y la última línea si están vacías. Por ejemplo:

```kotlin
val unevenString = """
        123
         456
          789""".trimIndent()
print(unevenString)
println()

val rawString = """123
         456
          789
""".trimIndent()
print(rawString)
```

Este texto se muestra así:

```text
123
 456
  789

123
         456
          789
```

En el primer caso, las tres líneas comparten una sangría mínima de 8 espacios, que `trimIndent()` recorta por igual. En el segundo, la primera línea (`123`) va pegada a las comillas triples, sin sangría, por lo que la sangría común es cero y las demás líneas conservan sus espacios.

## Resumen

En este capítulo trabajaste de cerca con el tipo `String`:

- La propiedad `.length` devuelve la cantidad de caracteres de una cadena.
- La **concatenación** con `+` une cadenas (y otros valores) en una cadena nueva; el orden importa, y para varias variables suelen ser más claras las plantillas de cadena.
- La función `repeat` repite una cadena varias veces.
- Las **cadenas sin formato** (comillas triples `"""`) permiten texto multilínea y caracteres especiales sin escaparlos, y `.trimIndent()` recorta la sangría común.
