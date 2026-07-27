# Anexo: Entrada estándar con `Scanner` de Java

- [Introducción](#introducción)
- [¿Qué es `Scanner`?](#qué-es-scanner)
- [Leer datos con `Scanner`](#leer-datos-con-scanner)
- [Delimitador personalizado](#delimitador-personalizado)
- [Comprobar si hay más datos](#comprobar-si-hay-más-datos)

---

## Introducción

En el capítulo de entrada y salida aprendiste a leer datos con `readln()`, que es la forma más sencilla y habitual de trabajar con la entrada estándar en Kotlin. Para la mayoría de los programas, es más que suficiente.

Sin embargo, a veces `readln()` se queda corto: leer datos palabra por palabra, buscar solo números de cierto tipo en un flujo de texto o controlar la lectura paso a paso. En esos casos, puedes recurrir a una herramienta de Java que también funciona en Kotlin: la clase `Scanner`. Como Kotlin es totalmente interoperable con Java (lo viste en el primer capítulo), puedes usar sus bibliotecas sin problema.

> [!NOTE]
> Este anexo usa la instrucción `if` (capítulo 8) y menciona las **excepciones** (capítulo 14), así que conviene leerlo una vez que hayas visto esos temas.

## ¿Qué es `Scanner`?

`Scanner` es otra forma de obtener datos de la entrada estándar. Permite a un programa leer valores de distintos tipos (cadenas, números, etc.) y ofrece más control que `readln()`.

Para usarlo, primero debes **importarlo** al principio de tu archivo:

```kotlin
import java.util.Scanner
```

Luego, creas una variable `Scanner` conectada a la entrada estándar:

```kotlin
val scanner = Scanner(System.`in`)
```

Aquí, `System.`in`` es el objeto que representa el flujo de entrada estándar (las comillas invertidas alrededor de `in` son necesarias porque `in` es una palabra reservada de Kotlin). El `Scanner` lo envuelve y te ofrece un conjunto de métodos prácticos para leer de él.

## Leer datos con `Scanner`

Una vez creado el `scanner`, puedes leer datos de distintos tipos con sus métodos:

```kotlin
val line = scanner.nextLine() // lee una línea completa, p. ej., "Hola, Kotlin"
val num = scanner.nextInt()   // lee un número entero, p. ej., 123
val word = scanner.next()     // lee una sola palabra, p. ej., "Hola"
```

Al llamar a uno de estos métodos, el programa se detiene y espera a que se introduzcan datos. Por ejemplo, el siguiente programa lee dos números y los muestra en orden inverso:

```kotlin
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    val num1 = scanner.nextInt() // lee el primer número
    val num2 = scanner.nextInt() // lee el segundo número

    println(num2) // muestra el segundo
    println(num1) // muestra el primero
}
```

`Scanner` ofrece muchos más métodos para leer otros tipos de datos; puedes consultarlos en la documentación oficial de la [clase `Scanner`](https://docs.oracle.com/javase/8/docs/api/java/util/Scanner.html).

Cuando termines de leer, conviene **cerrar** el `Scanner` con el método `close()`. A diferencia de `readln()`, un `Scanner` puede seguir consumiendo recursos si se deja abierto:

```kotlin
scanner.close()
```

## Delimitador personalizado

`Scanner` también puede leer directamente de una cadena, no solo del teclado:

```kotlin
val scanner = Scanner("123_456")
```

Pero ¿cómo leemos los dos números si no hay espacios que los separen? Por defecto, `Scanner` usa los espacios en blanco como **delimitador** (el símbolo que marca dónde termina un valor y empieza el siguiente). Con el método `useDelimiter()` puedes cambiarlo; en este caso, al guion bajo `_`:

```kotlin
scanner.useDelimiter("_")

println(scanner.nextInt()) // 123
println(scanner.nextInt()) // 456
```

## Comprobar si hay más datos

Supongamos que tenemos estos datos en el `Scanner`:

```kotlin
val scanner = Scanner("¡Hola, Kotlin!")
```

Ese texto tiene dos palabras: `¡Hola,` y `Kotlin!`. Si intentáramos leer una **tercera** palabra, se produciría una **excepción** y el programa se detendría de golpe.

Para evitarlo, `Scanner` ofrece el método `hasNext()`, que devuelve `true` si todavía queda algo por leer. Combinándolo con un `if`, leemos solo cuando es seguro hacerlo:

```kotlin
val scanner = Scanner("¡Hola, Kotlin!")

if (scanner.hasNext()) {
    println(scanner.next()) // ¡Hola,
}
if (scanner.hasNext()) {
    println(scanner.next()) // Kotlin!
}
if (scanner.hasNext()) {
    println(scanner.next()) // este bloque no se ejecuta: ya no quedan datos
}
```

De esta forma, el programa lee los datos disponibles sin arriesgarse a fallar cuando el flujo de entrada se termina. Existen también variantes como `hasNextInt()` o `hasNextDouble()`, que comprueban si el siguiente dato es de un tipo concreto.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/README.md">← Volver al índice</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
