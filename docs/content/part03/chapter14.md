# Capítulo 14: Manejo de excepciones: `try`, `catch` y `finally`

> [!TIP]Antes de empezar, recupera lo que ya sabes
> Responde de memoria; no importa acertar, se trata de activar lo que ya sabes. Vuelve a estas preguntas al terminar el capítulo.
>
> 1. ¿Qué es el *smart cast* y en qué situación se aplica?
> 2. ¿Qué operador usas para dar un valor por defecto cuando algo puede ser `null`?
> 3. ¿Por qué la conversión `"abc".toInt()` puede fallar aunque el código compile?

## Introducción

En el capítulo 13 aprendiste a proteger tu código de los valores nulos con null safety. Pero hay otro tipo de problema que ocurre **en tiempo de ejecución**, incluso cuando tu código compila perfectamente: los **errores** que interrumpen el flujo normal de un programa, como dividir por cero, acceder a un índice inexistente de una lista o convertir un texto que no es un número.

En este capítulo aprenderás a manejar esos errores con `try`, `catch` y `finally`, y a lanzar tus propias excepciones con `throw`.

> [!NOTE]Nota importante
> Existen dos enfoques para manejar errores en Kotlin: el **paradigma clásico imperativo** (las excepciones con `try`/`catch`, que ves en este capítulo) y el **enfoque funcional** (el tipo `Result<T>`, que verás en el próximo capítulo). Ambos son válidos y se complementan: en este capítulo aprenderás el primero, que es la base para entender el segundo.

## ¿Qué es una excepción?

Una **excepción** es un evento que ocurre cuando el programa encuentra un error en tiempo de ejecución y **interrumpe el flujo normal** de ejecución para señalizarlo.

Por ejemplo:

```kotlin
val numeros = listOf(1, 2, 3)
println(numeros[10]) // lanza IndexOutOfBoundsException
println("Esta línea nunca se ejecuta")
```

Si nadie maneja la excepción, el programa se **detiene** y muestra el error. El texto después de la línea que falla no se ejecuta.

## `try` y `catch`

Para manejar una excepción y evitar que el programa se detenga, usas un bloque `try` / `catch`:

```kotlin
try {
    val numeros = listOf(1, 2, 3)
    println(numeros[10])
} catch (e: IndexOutOfBoundsException) {
    println("Se produjo un error: ${e.message}")
}
println("Esta línea SÍ se ejecuta")
```

La estructura es:

- **`try { ... }`**: el código que puede lanzar una excepción.
- **`catch (e: TipoDeExcepcion) { ... }`**: el código que se ejecuta **solo si** se lanzó una excepción del tipo indicado (o de un subtipo). La excepción queda disponible en la variable `e`.
- El código después del bloque `try`/`catch` se ejecuta pase lo que pase (si no hubo error, o después de manejar el error).

### Capturar excepciones específicas

Kotlin tiene muchos tipos de excepción predefinidos. Algunos de los más comunes:

| Excepción | Cuándo ocurre |
|---|---|
| `IndexOutOfBoundsException` | Accedes a un índice inexistente de una lista o arreglo. |
| `NumberFormatException` | Intentas convertir a número un texto que no lo es (`"abc".toInt()`). |
| `ArithmeticException` | Operación aritmética inválida, como dividir un `Int` entre cero. |
| `NullPointerException` | Intentas usar una referencia nula en una operación que no la permite. |

Capturar el tipo **específico** te permite reaccionar con precisión:

```kotlin
try {
    val numero = readln().toInt()
    println("El número es $numero")
} catch (e: NumberFormatException) {
    println("'$e' no es un número válido")
}
```

Si el texto no es un número, se ejecuta el bloque `catch`; si lo es, se ejecuta el `try` completo y el `catch` se omite.

## `finally`

El bloque `finally` se ejecuta **siempre**, pase lo que pase: si hubo excepción o no, si la capturaste o no.

```kotlin
try {
    println("Intentando abrir un recurso...")
    // ... código que puede lanzar excepción
} catch (e: Exception) {
    println("Se produjo un error: ${e.message}")
} finally {
    println("Esto se ejecuta siempre")
}
```

Su uso típico es liberar recursos que debes cerrar independientemente del resultado: cerrar un archivo, una conexión o un stream.

## Lanzar tus propias excepciones con `throw`

Además de manejar excepciones que lanza el sistema, puedes lanzar las tuyas cuando detectas una situación inválida:

```kotlin
fun dividir(a: Int, b: Int): Int {
    if (b == 0) {
        throw IllegalArgumentException("No se puede dividir por cero")
    }
    println("Esta línea sí se ejecuta si b != 0")
    return a / b
}

val resultado = dividir(10, 0) // lanza la excepción aquí
println("Esta línea nunca se ejecuta") // no llega aquí
```

Si nadie captura la excepción con `try` / `catch`, el programa se detiene y muestra el error.

## Resumen

En este capítulo aprendiste a manejar los errores en tiempo de ejecución con el enfoque clásico:

- Una **excepción** interrumpe el flujo normal cuando ocurre un error; si no se maneja, el programa se detiene.
- `try` / `catch` te permite manejar la excepción y seguir adelante. `finally` ejecuta código pase lo que pase.
- Puedes capturar tipos **específicos** de excepción (como `NumberFormatException`) para reaccionar con precisión.
- Con **`throw`** lanzas tus propias excepciones cuando detectas una situación inválida. `throw` **interrumpe** la ejecución inmediatamente; el código posterior no se ejecuta.

## Para cerrar

**Predice** qué imprime este programa (enumera las letras en orden) y explica qué se ejecuta y por qué:

```kotlin
try {
    val n = "12a".toInt()
    println("A")
} catch (e: NumberFormatException) {
    println("B")
} finally {
    println("C")
}
println("D")
```

Razona tu predicción antes de consultar la respuesta.

??? success "Ver respuesta y razonamiento"
    Imprime `B`, `C` y `D`, en ese orden. `"12a"` no es un número, así que `toInt()` lanza `NumberFormatException`; la línea `A` nunca se ejecuta. El bloque `catch` imprime `B` y el programa **continúa**. El bloque `finally` se ejecuta **siempre** (haya habido error o no), así que imprime `C`, y luego sigue el flujo normal con `D`.

    **Error común:** creer que `finally` solo se ejecuta cuando hay una excepción, o que después de capturar el error el programa se detiene. Ninguna de las dos es cierta: `catch` te permite seguir, y `finally` está pensado justo para lo que debe ocurrir "pase lo que pase" (por ejemplo, cerrar un recurso).

En el **próximo capítulo** verás un enfoque alternativo y más funcional para manejar errores: el tipo `Result<T>`, que convierte el fallo en un valor que puedes inspeccionar y transformar, en lugar de interrumpir el flujo con excepciones.
