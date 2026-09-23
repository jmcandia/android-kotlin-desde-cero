# Capítulo 14: Manejo de excepciones: `try`, `catch` y `Result`

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

Hasta ahora has visto cómo **capturar** excepciones que lanza Kotlin. Pero tú también puedes **lanzar** excepciones cuando detectas que algo no está bien en tu programa.

La palabra clave `throw` interrumpe la ejecución y lanza una excepción:

```kotlin
fun retirar(saldo: Double, cantidad: Double): Double {
    if (cantidad > saldo) {
        throw IllegalArgumentException("Saldo insuficiente")
    }
    return saldo - cantidad
}
```

Al llamar a `retirar(100.0, 150.0)`, la función lanza una excepción con el mensaje "Saldo insuficiente" y la ejecución se detiene en ese punto. Quien invoque a `retirar` puede capturar esa excepción con `try` / `catch` y decidir qué hacer.

### Excepciones estándar

Kotlin incluye varias excepciones estándar que describen situaciones comunes:

- **`IllegalArgumentException`**: un argumento no es válido (como un número negativo donde esperas uno positivo).
- **`IllegalStateException`**: el objeto está en un estado incorrecto para la operación (como intentar leer de un archivo ya cerrado).
- **`NullPointerException`**: se intentó usar un valor `null` donde no se esperaba (poco común gracias al *null safety*, pero puede ocurrir con `!!`).

### Excepciones personalizadas

Si ninguna de las excepciones estándar describe tu situación, puedes crear tu propia clase de excepción heredando de `Exception`:

```kotlin
class SaldoInsuficienteException(mensaje: String) : Exception(mensaje)

fun retirar(saldo: Double, cantidad: Double): Double {
    if (cantidad > saldo) {
        throw SaldoInsuficienteException(
            "Intentaste retirar $cantidad, pero solo tienes $saldo"
        )
    }
    return saldo - cantidad
}
```

Ahora el código que llama a `retirar` puede capturar específicamente `SaldoInsuficienteException` y tratarla de forma distinta a otros errores:

```kotlin
try {
    val nuevoSaldo = retirar(100.0, 150.0)
    println("Nuevo saldo: $nuevoSaldo")
} catch (e: SaldoInsuficienteException) {
    println("Error: ${e.message}")
} catch (e: Exception) {
    println("Error inesperado: ${e.message}")
}
```

### `throw` interrumpe la ejecución

Es importante entender que `throw` **detiene** la función de inmediato. El código que viene después no se ejecuta:

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

## `Result<T>`: modelar éxito o fallo como un tipo de retorno

Lanzar excepciones funciona bien cuando quieres interrumpir el flujo y forzar al código que llama a reaccionar. Pero a veces prefieres que **el fallo sea parte del contrato** de la función: la función devuelve un resultado que puede ser exitoso o fallido, y quien la llama decide qué hacer.

Para eso existe **`Result<T>`**, un tipo que representa explícitamente **dos estados posibles**: éxito (con un valor de tipo `T`) o fallo (con una excepción).

### ¿Qué es `Result<T>`?

`Result<T>` es un tipo que encapsula el resultado de una operación que puede fallar. Puede contener:

- Un **valor exitoso** de tipo `T`, o
- Una **excepción** que describe el fallo.

A diferencia de lanzar excepciones (que interrumpen el flujo), `Result` convierte el fallo en un valor que puedes inspeccionar, pasar a otras funciones o transformar.

### Crear un `Result`

Puedes construir un `Result` con dos funciones:

- **`Result.success(valor)`**: crea un `Result` exitoso que contiene `valor`.
- **`Result.failure(excepcion)`**: crea un `Result` fallido que contiene la excepción.

Aquí tienes un ejemplo de función que devuelve `Result<Int>`:

```kotlin
fun dividir(a: Int, b: Int): Result<Int> {
    return if (b != 0) {
        Result.success(a / b)
    } else {
        Result.failure(IllegalArgumentException("No se puede dividir por cero"))
    }
}
```

El tipo de retorno `Result<Int>` deja claro que la función **puede fallar**, sin lanzar excepciones. Quien la llama **debe** inspeccionar el `Result` para saber qué ocurrió.

### Comparación: `throw` vs. `Result<T>`

Compara estas dos firmas:

```kotlin
fun dividir(a: Int, b: Int): Int          // puede lanzar una excepción (implícito)
fun dividir(a: Int, b: Int): Result<Int>  // puede fallar (explícito en el tipo)
```

La primera puede lanzar una excepción, pero nada en la firma lo indica; quien la use puede no darse cuenta hasta que el programa se caiga. La segunda **declara** el fallo como parte del contrato, y el compilador te obliga a manejarlo (extraer el valor con `getOrNull()`, `getOrDefault()`, `onSuccess`, etc.).

## Consumir un `Result`

Una vez que tienes un `Result`, necesitas inspeccionarlo para saber si la operación tuvo éxito o falló. `Result` ofrece varias formas de hacerlo.

### Propiedades de estado

```kotlin
val resultado = dividir(10, 2)
println(resultado.isSuccess)  // true
println(resultado.isFailure)  // false
```

### Extraer el valor o la excepción

**`getOrNull()`**: devuelve el valor si hubo éxito, o `null` si hubo fallo:

```kotlin
val resultado = dividir(10, 0)
val numero = resultado.getOrNull()       // null (porque falló)
println(numero)                          // null
```

**`exceptionOrNull()`**: devuelve la excepción si hubo fallo, o `null` si hubo éxito:

```kotlin
val resultado = dividir(10, 0)
val excepcion = resultado.exceptionOrNull() // IllegalArgumentException
println(excepcion?.message)                 // "No se puede dividir por cero"
```

**`getOrDefault()`**: devuelve el valor si hubo éxito, o un valor por defecto si hubo fallo:

```kotlin
val numero = dividir(10, 0).getOrDefault(0)
println(numero) // 0
```

Como ves, `getOrNull()` enlaza directamente con el *null safety* del capítulo anterior: el fallo se convierte en `null`, que ya sabes manejar.

### Reaccionar con callbacks funcionales

En lugar de preguntar por el estado con `if`, puedes usar `onSuccess` y `onFailure`:

```kotlin
dividir(10, 2)
    .onSuccess { valor -> println("Resultado: $valor") }
    .onFailure { excepcion -> println("Error: ${excepcion.message}") }
```

Este estilo es muy legible: separa claramente el flujo exitoso del fallido.

También existe `fold`, que combina ambos casos en una sola expresión:

```kotlin
val mensaje = dividir(10, 0).fold(
    onSuccess = { "El resultado es $it" },
    onFailure = { "Error: ${it.message}" }
)
println(mensaje) // "Error: No se puede dividir por cero"
```

## `Result` como tipo de retorno explícito

La gran ventaja de `Result` es que **obliga** al código que llama a reconocer que la operación puede fallar. No es un detalle oculto; es parte visible de la firma de la función.

En aplicaciones reales, `Result` es muy común en las **capas de datos**. Por ejemplo, un repositorio que pide datos a una API REST puede devolver `Result<List<Contacto>>`, indicando que la operación puede fallar (sin conexión, error del servidor, timeout…). El `ViewModel` recibe ese `Result`, lo inspecciona, y actualiza el estado de la interfaz según el resultado:

```kotlin
// En el repositorio (capa de datos)
suspend fun obtenerContactos(): Result<List<Contacto>>

// En el ViewModel (capa de presentación)
repositorio.obtenerContactos()
    .onSuccess { contactos -> _uiState.value = UiState.Success(contactos) }
    .onFailure { excepcion -> _uiState.value = UiState.Error(excepcion.message) }
```

Este patrón lo verás en acción cuando construyas la arquitectura de la app en las partes VIII y IX.

## `runCatching`: convertir excepciones en `Result`

A veces tienes código que **puede lanzar excepciones** (como `toInt()`, o una operación de red) y quieres convertirlo en un `Result` sin escribir manualmente `try` / `catch` y `Result.success` / `Result.failure`. Para eso está **`runCatching`**.

`runCatching { }` ejecuta el bloque de código que le pases y devuelve un `Result`: `Result.success` si el bloque terminó sin excepciones, o `Result.failure` si lanzó alguna:

```kotlin
val resultado = runCatching { "42".toInt() }
println(resultado.isSuccess) // true
println(resultado.getOrNull()) // 42

val resultadoFallido = runCatching { "abc".toInt() }
println(resultadoFallido.isSuccess) // false
println(resultadoFallido.getOrNull()) // null
```

Es equivalente a escribir:

```kotlin
val resultado = try {
    Result.success("42".toInt())
} catch (e: Exception) {
    Result.failure(e)
}
```

pero mucho más conciso.

### `runCatching` como herramienta

`runCatching` es útil cuando trabajas con APIs de terceros que usan excepciones y quieres convertirlas a `Result` para manejarlas de forma funcional:

```kotlin
val numero = runCatching { readln().toInt() }
    .getOrDefault(0)
println("Número ingresado (o 0 si falló): $numero")
```

O para registrar el error y seguir adelante:

```kotlin
runCatching { operacionPeligrosa() }
    .onFailure { println("Falló: ${it.message}") }
```

> [!NOTE]Nota importante
> `Result<T>` representa el resultado exitoso o fallido de una operación. `runCatching` es una herramienta conveniente para ejecutar código que puede lanzar excepciones y convertir el resultado en un `Result`.

## `throw` vs. `Result`: ¿cuándo usar cada uno?

Ambos enfoques son válidos, pero tienen casos de uso distintos:

| Situación | Recomendación |
|---|---|
| Error **inesperado** que no puedes recuperar (bug interno, programación defensiva) | `throw` — interrumpe el flujo y fuerza a corregir el problema. |
| Error **esperado** que forma parte del flujo normal (validación, operación de red, parseo de entrada del usuario) | `Result<T>` — el fallo es parte del contrato, y quien llama decide cómo manejarlo. |
| Estás **dentro** de una capa de lógica y quieres propagar el error hacia arriba sin ocultarlo | `throw` — las capas superiores lo capturarán. |
| Estás en la **frontera** entre capas (por ejemplo, un repositorio que habla con una API) y quieres que la capa superior decida qué hacer con el fallo | `Result<T>` — el `ViewModel` inspecciona el `Result` y actualiza el estado de la UI. |

En la práctica:

- **`throw`** es más común para errores internos (bugs, precondiciones violadas).
- **`Result`** es ideal para **operaciones que pueden fallar de forma legítima** (leer un archivo que puede no existir, hacer una petición de red que puede fallar, parsear datos de entrada que pueden ser inválidos).

## ¿Cuándo usar cada herramienta?

Para resumir:

- Usa **`try` / `catch`** cuando quieras **reaccionar** a un error en el momento: mostrar un mensaje, reintentar, registrar el problema.
- Usa **`throw`** cuando detectes una situación inválida que **no debería ocurrir** y quieras interrumpir el flujo para forzar su corrección.
- Usa **`Result<T>`** (con `Result.success` / `Result.failure`) cuando el fallo sea **parte del contrato** de la función y quieras que quien la llame decida cómo manejarlo.
- Usa **`runCatching`** como atajo para convertir código que lanza excepciones en un `Result`, especialmente al trabajar con APIs de terceros que usan `throw`.
- En cualquier caso, maneja solo los errores que **esperas** y sabes cómo resolver. Capturar todo a ciegas puede ocultar problemas reales y dificultar encontrar la causa.

## Resumen

En este capítulo aprendiste a manejar los errores en tiempo de ejecución:

- Una **excepción** interrumpe el flujo normal cuando ocurre un error; si no se maneja, el programa se detiene.
- `try` / `catch` te permite manejar la excepción y seguir adelante. `finally` ejecuta código pase lo que pase.
- Puedes capturar tipos **específicos** de excepción (como `NumberFormatException`) para reaccionar con precisión.
- Con **`throw`** lanzas tus propias excepciones cuando detectas una situación inválida. Puedes usar excepciones estándar (`IllegalArgumentException`, `IllegalStateException`) o crear clases personalizadas.
- `throw` **interrumpe** la ejecución inmediatamente; el código posterior no se ejecuta.
- **`Result<T>`** es un tipo que representa explícitamente éxito (`Result.success`) o fallo (`Result.failure`).
- Un `Result` se **consume** con propiedades (`isSuccess`, `isFailure`), funciones de extracción (`getOrNull()`, `exceptionOrNull()`, `getOrDefault()`) o callbacks funcionales (`onSuccess`, `onFailure`, `fold`).
- `Result` como tipo de retorno **declara explícitamente** que la función puede fallar, obligando al código que llama a manejar ambos casos.
- **`runCatching`** ejecuta código que puede lanzar excepciones y convierte el resultado en un `Result`.
- **`throw`** se usa para errores inesperados o internos; **`Result`** se usa para operaciones que pueden fallar legítimamente como parte de su comportamiento normal.

Con esto cierras la parte de código robusto de Kotlin. El siguiente anexo trata el formateo avanzado de cadenas con `String.format`; y a partir de ahí entrarás de lleno en la **programación orientada a objetos**, donde crearás tus propias clases.
