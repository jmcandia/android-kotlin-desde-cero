# Capítulo 14b: `Result<T>`: manejo funcional de éxito y fallo

## Introducción

En el capítulo anterior aprendiste a manejar errores con el enfoque clásico: excepciones con `try`/`catch` y `throw`. Lanzar excepciones funciona bien cuando quieres interrumpir el flujo y forzar al código que llama a reaccionar. Pero a veces prefieres que **el fallo sea parte del contrato** de la función: la función devuelve un resultado que puede ser exitoso o fallido, y quien la llama decide qué hacer.

Para eso existe **`Result<T>`**, un tipo que representa explícitamente **dos estados posibles**: éxito (con un valor de tipo `T`) o fallo (con una excepción).

## ¿Qué es `Result<T>`?

`Result<T>` es un tipo que encapsula el resultado de una operación que puede fallar. Puede contener:

- Un **valor exitoso** de tipo `T`, o
- Una **excepción** que describe el fallo.

A diferencia de lanzar excepciones (que interrumpen el flujo), `Result` convierte el fallo en un valor que puedes inspeccionar, pasar a otras funciones o transformar.

## Crear un `Result`

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

Compara esta versión con la del capítulo anterior: la firma es `Result<Int>` en lugar de `Int`. Quien la llama sabe, **solo con mirar el tipo de retorno**, que la operación puede fallar.

## Consumir un `Result`

Una vez que tienes un `Result`, hay varias formas de consumirlo.

### Propiedades `isSuccess` e `isFailure`

```kotlin
val resultado = dividir(10, 0)

if (resultado.isSuccess) {
    println("Éxito")
} else {
    println("Falló")
}
```

### Funciones de extracción

- **`getOrNull()`**: devuelve el valor si hubo éxito, o `null` si falló.
- **`exceptionOrNull()`**: devuelve la excepción si falló, o `null` si hubo éxito.
- **`getOrDefault(default)`**: devuelve el valor si hubo éxito, o el valor por defecto indicado.

```kotlin
val valor = dividir(10, 0).getOrNull()
println(valor) // null, porque la división falló

val valorPorDefecto = dividir(10, 0).getOrDefault(0)
println(valorPorDefecto) // 0
```

### Callbacks funcionales: `onSuccess` y `onFailure`

```kotlin
dividir(10, 2)
    .onSuccess { println("El resultado es $it") }
    .onFailure { println("El error es: ${it.message}") }
```

Ambos reciben una lambda: `onSuccess` se ejecuta solo si hubo éxito (recibiendo el valor), y `onFailure` solo si falló (recibiendo la excepción). Puedes encadenarlos porque cada uno devuelve el `Result` original.

### `fold` para transformar ambos casos

`fold` recibe dos lambdas y las combina en un único resultado:

```kotlin
val mensaje = dividir(10, 2).fold(
    onSuccess = { valor -> "El resultado es $valor" },
    onFailure = { error -> "El error es: ${error.message}" }
)
println(mensaje)
```

`fold` transforma el `Result<Int>` en un `String`: cualquiera de las dos lambdas produce el valor final.

## `Result` como tipo de retorno explícito

Una de las ventajas principales de `Result` es que **declara en la firma** que la función puede fallar:

```kotlin
fun parsearEdad(texto: String): Result<Int> {
    return runCatching { texto.toInt() }
}
```

Quien llama a `parsearEdad` **no puede ignorar** el fallo: si lo intenta, el compilador no le impide extraer solo el valor, pero el tipo comunica explícitamente que existe ese caso. Es una forma de documentación que el compilador verifica.

Compara las dos firmas:

```kotlin
fun parsearEdad(texto: String): Int               // ¿puede fallar? No lo sabes
fun parsearEdad(texto: String): Result<Int>       // puede fallar; queda claro
```

## `runCatching`: convertir excepciones en `Result`

Cuando trabajas con código que lanza excepciones (por ejemplo, APIs de terceros o funciones del sistema), **`runCatching`** te permite convertir ese estilo en `Result`:

```kotlin
val resultado: Result<Int> = runCatching {
    "abc".toInt()
}
```

`runCatching` ejecuta el bloque y:

- Si no lanza excepción, devuelve `Result.success` con el valor.
- Si lanza una excepción, la **captura** y devuelve `Result.failure` con ella.

Es exactamente lo que harías con `try`/`catch`, pero en una expresión que devuelve un valor.

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

Para resumir ambos capítulos:

- Usa **`try` / `catch`** cuando quieras **reaccionar** a un error en el momento: mostrar un mensaje, reintentar, registrar el problema.
- Usa **`throw`** cuando detectes una situación inválida que **no debería ocurrir** y quieras interrumpir el flujo para forzar su corrección.
- Usa **`Result<T>`** (con `Result.success` / `Result.failure`) cuando el fallo sea **parte del contrato** de la función y quieras que quien la llame decida cómo manejarlo.
- Usa **`runCatching`** como atajo para convertir código que lanza excepciones en un `Result`, especialmente al trabajar con APIs de terceros que usan `throw`.
- En cualquier caso, maneja solo los errores que **esperas** y sabes cómo resolver. Capturar todo a ciegas puede ocultar problemas reales y dificultar encontrar la causa.

## Resumen

En este capítulo aprendiste el enfoque funcional para manejar errores:

- **`Result<T>`** es un tipo que representa explícitamente éxito (`Result.success`) o fallo (`Result.failure`).
- Un `Result` se **consume** con propiedades (`isSuccess`, `isFailure`), funciones de extracción (`getOrNull()`, `exceptionOrNull()`, `getOrDefault()`) o callbacks funcionales (`onSuccess`, `onFailure`, `fold`).
- `Result` como tipo de retorno **declara explícitamente** que la función puede fallar, comunicando el contrato en la firma.
- **`runCatching`** ejecuta código que puede lanzar excepciones y convierte el resultado en un `Result`.
- **`throw`** se usa para errores inesperados o internos; **`Result`** se usa para operaciones que pueden fallar legítimamente como parte de su comportamiento normal.

Con esto cierras la parte de código robusto de Kotlin. El siguiente anexo trata el formateo avanzado de cadenas con `String.format`; y a partir de ahí entrarás de lleno en la **programación orientada a objetos**, donde crearás tus propias clases.
