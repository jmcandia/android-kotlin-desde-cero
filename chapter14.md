# Capítulo 14: Manejo de excepciones: `try`, `catch` y `Result`

- [Introducción](#introducción)
- [¿Qué es una excepción?](#qué-es-una-excepción)
- [`try` y `catch`](#try-y-catch)
- [`finally`](#finally)
- [Capturar excepciones específicas](#capturar-excepciones-específicas)
- [Lanzar tus propias excepciones con `throw`](#lanzar-tus-propias-excepciones-con-throw)
- [Una alternativa: `Result` y `runCatching`](#una-alternativa-result-y-runcatching)
- [¿Cuál usar?](#cuál-usar)
- [Resumen](#resumen)

---

## Introducción

Hasta ahora, cuando algo salía mal en tiempo de ejecución, nuestros programas simplemente se caían. Lo viste varias veces a lo largo del curso: al dividir un número entero por cero, al pedir un índice que no existe en una lista o al usar `!!` sobre un valor que era `null`.

En todos esos casos, Kotlin lanza una **excepción**: una señal de que ocurrió algo inesperado que impide continuar con normalidad. Si nadie la maneja, el programa se detiene de golpe.

Pero muchos de esos fallos son previsibles y no deberían tumbar la aplicación. Si el usuario escribe "abc" cuando esperabas un número, lo razonable es mostrarle un mensaje y pedirle que lo intente de nuevo, no que el programa se cierre. En este capítulo aprenderás a **manejar excepciones** para que tu programa reaccione con elegancia ante los errores.

## ¿Qué es una excepción?

Una **excepción** es un evento que interrumpe el flujo normal de un programa cuando ocurre un error. Cuando se produce (se dice que la excepción se **lanza**), la ejecución se detiene en ese punto; y si nadie la captura, el programa termina mostrando un mensaje de error.

Por ejemplo, la función `toInt()` lanza una excepción si el texto no representa un número válido:

```kotlin
val numero = "abc".toInt() // lanza una excepción: "abc" no es un número
println("Esta línea no se ejecuta")
```

Al ejecutarse, este programa se detiene en la primera línea con un error del tipo `NumberFormatException`, y la segunda línea nunca llega a ejecutarse.

## `try` y `catch`

Para evitar que una excepción tumbe el programa, envuelves el código que podría fallar en un bloque `try` y lo manejas en un bloque `catch`:

```kotlin
try {
    val numero = "abc".toInt()
    println(numero)
} catch (e: Exception) {
    println("Eso no es un número válido.")
}
println("El programa continúa.")
```

**Salida:**

```plaintext
Eso no es un número válido.
El programa continúa.
```

Así funciona:

- El código dentro de `try` se ejecuta normalmente.
- Si ocurre una excepción, la ejecución **salta** de inmediato al bloque `catch`, que recibe la excepción en una variable (aquí, `e`).
- Después del `catch`, el programa **continúa** con normalidad, en lugar de detenerse.

Si dentro del `try` no ocurre ninguna excepción, el bloque `catch` simplemente se ignora.

## `finally`

A veces necesitas ejecutar cierto código **pase lo que pase**, haya o no haya excepción; por ejemplo, para cerrar un archivo o liberar un recurso. Para eso está el bloque `finally`, que se ejecuta siempre al final:

```kotlin
try {
    println("Intentando...")
    val numero = "abc".toInt()
} catch (e: Exception) {
    println("Ocurrió un error.")
} finally {
    println("Esto se ejecuta siempre.")
}
```

**Salida:**

```plaintext
Intentando...
Ocurrió un error.
Esto se ejecuta siempre.
```

## Capturar excepciones específicas

El tipo `Exception` es general y captura casi cualquier error. Pero también puedes capturar tipos **específicos** para reaccionar de forma distinta según el problema:

```kotlin
try {
    val numero = "abc".toInt()
} catch (e: NumberFormatException) {
    println("El texto no tiene formato de número.")
}
```

Capturar el tipo exacto (`NumberFormatException`, en lugar del genérico `Exception`) hace tu código más preciso: manejas solo el error que esperabas y no ocultas otros por accidente.

## Lanzar tus propias excepciones con `throw`

Igual que Kotlin lanza excepciones, tú también puedes hacerlo cuando detectas una situación inválida en tu programa, con la palabra clave `throw`:

```kotlin
fun raizCuadrada(numero: Int): Double {
    if (numero < 0) {
        throw IllegalArgumentException("No se puede calcular la raíz de un número negativo")
    }
    // ... cálculo de la raíz ...
}
```

Al lanzar una excepción, interrumpes la función y avisas de que algo no está bien. Quien invoque a `raizCuadrada` podrá capturar esa excepción con un `try` / `catch` y decidir cómo reaccionar.

## Una alternativa: `Result` y `runCatching`

El manejo con `try` / `catch` funciona bien, pero a veces resulta más cómodo tratar el posible fallo como un **valor** que puedas inspeccionar, en lugar de un bloque que interrumpe el flujo. Para eso, Kotlin ofrece `runCatching`.

`runCatching { }` ejecuta el código que le pases y, en vez de dejar que la excepción se propague, devuelve un objeto **`Result`** que representa lo ocurrido: puede ser un **éxito** (con el valor obtenido) o un **fallo** (con la excepción).

```kotlin
val resultado = runCatching { "abc".toInt() }
```

Sobre ese `Result` puedes preguntar qué pasó, sin que el programa se caiga. Con `getOrNull()` obtienes el valor si hubo éxito, o `null` si hubo fallo:

```kotlin
val numero = runCatching { "abc".toInt() }.getOrNull()
println(numero) // null (la conversión falló)

val numeroValido = runCatching { "42".toInt() }.getOrNull()
println(numeroValido) // 42
```

Como ves, `getOrNull()` enlaza directamente con el null safety del capítulo anterior: el fallo se convierte en `null`, que ya sabes manejar. También puedes entregar un valor por defecto con `getOrDefault`:

```kotlin
val numero = runCatching { "abc".toInt() }.getOrDefault(0)
println(numero) // 0
```

> [!NOTE]
> Este enfoque —tratar el éxito o el fallo como un valor— es muy común en aplicaciones reales. Lo usaremos, por ejemplo, al manejar las respuestas de la PokeAPI, que pueden llegar correctamente o fallar (por falta de conexión, un error del servidor, etc.).

## ¿Cuál usar?

- Usa `try` / `catch` cuando quieras **reaccionar** a un error en el momento: mostrar un mensaje, reintentar, registrar el problema.
- Usa `runCatching` y `Result` cuando prefieras **tratar el fallo como un valor** y decidir qué hacer con él después (por ejemplo, convertirlo en `null` con `getOrNull()` o en un valor por defecto).
- En cualquier caso, maneja solo los errores que **esperas** y sabes cómo resolver. Capturar todo a ciegas puede ocultar problemas reales y dificultar encontrar la causa.

## Resumen

En este capítulo aprendiste a manejar los errores en tiempo de ejecución:

- Una **excepción** interrumpe el flujo normal cuando ocurre un error; si no se maneja, el programa se detiene.
- `try` / `catch` te permite manejar la excepción y seguir adelante. `finally` ejecuta código pase lo que pase.
- Puedes capturar tipos **específicos** de excepción (como `NumberFormatException`) para reaccionar con precisión.
- Con `throw` lanzas tus propias excepciones cuando detectas una situación inválida.
- `runCatching` devuelve un `Result` que representa éxito o fallo, y te deja tratar el error como un valor (por ejemplo, con `getOrNull()` o `getOrDefault()`).

Con esto cierras la parte de código robusto de Kotlin. El siguiente capítulo, opcional, trata el formateo avanzado de cadenas con `String.format`; y a partir de ahí entrarás de lleno en la **programación orientada a objetos**, donde crearás tus propias clases.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter13.md">← Anterior</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter15.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
