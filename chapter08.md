# Capítulo 8: Control de flujo: `if` y `when` como expresión

- [Introducción](#introducción)
- [La instrucción `if`](#la-instrucción-if)
- [La alternativa `else`](#la-alternativa-else)
- [Varias condiciones con `else if`](#varias-condiciones-con-else-if)
- [`if` como expresión](#if-como-expresión)
- [La instrucción `when`](#la-instrucción-when)
  - [`when` con varios valores](#when-con-varios-valores)
  - [`when` como expresión](#when-como-expresión)
  - [`when` sin argumento](#when-sin-argumento)
- [Resumen](#resumen)

---

## Introducción

En el capítulo de operadores aprendiste a escribir **condiciones**: expresiones que dan como resultado `true` o `false`, como `edad >= 18`. Hasta ahora, sin embargo, nuestros programas ejecutaban todas sus instrucciones en orden, de arriba abajo, sin excepción.

Pero un programa útil necesita **tomar decisiones**: mostrar un mensaje solo en ciertos casos, elegir entre dos caminos, reaccionar distinto según un valor. A esa capacidad de alterar el orden de ejecución según una condición se le llama **control de flujo**.

En este capítulo verás las dos herramientas principales de Kotlin para tomar decisiones: la instrucción `if` y la instrucción `when`. Y descubrirás una característica muy propia de Kotlin: que ambas, además de ejecutar código, pueden **devolver un valor**, lo que hace tu código más breve y expresivo.

## La instrucción `if`

Imagina que quieres mostrar un mensaje solo si una persona es mayor de edad. Para eso está la instrucción `if`: ejecuta un bloque de código **únicamente cuando una condición es verdadera**.

```kotlin
val edad = 20
if (edad >= 18) {
    println("Eres mayor de edad.")
}
// Salida: Eres mayor de edad.
```

La condición va entre paréntesis y el código a ejecutar, entre llaves. Si la condición es `true`, se ejecuta el bloque; si es `false`, simplemente se salta.

> [!NOTE]
> La condición de un `if` siempre debe ser un valor `Boolean`. A diferencia de otros lenguajes como C, en Kotlin no puedes usar un número (por ejemplo, `1` o `0`) en lugar de `true` o `false`.

## La alternativa `else`

Muchas veces no solo quieres hacer algo cuando la condición se cumple, sino **otra cosa** cuando no. Para eso se añade el bloque `else`, que se ejecuta cuando la condición es `false`:

```kotlin
val edad = 15
if (edad >= 18) {
    println("Eres mayor de edad.")
} else {
    println("Eres menor de edad.")
}
// Salida: Eres menor de edad.
```

Así, siempre se ejecuta uno de los dos bloques: nunca ambos, nunca ninguno.

## Varias condiciones con `else if`

Cuando hay más de dos posibilidades, puedes encadenar condiciones con `else if`. Kotlin las evalúa de arriba abajo y ejecuta el **primer** bloque cuya condición sea verdadera; el resto se ignora:

```kotlin
val nota = 75
if (nota >= 90) {
    println("Excelente")
} else if (nota >= 60) {
    println("Aprobado")
} else {
    println("Reprobado")
}
// Salida: Aprobado
```

En este ejemplo, `nota >= 90` es falsa, pero `nota >= 60` es verdadera, así que se muestra `Aprobado` y no se evalúa el `else` final.

## `if` como expresión

Hasta aquí usamos `if` para **ejecutar** instrucciones. Pero en Kotlin, `if` tiene una capacidad extra: también es una **expresión**, es decir, produce un valor que puedes guardar en una variable.

Supongamos que quieres guardar el mayor de dos números:

```kotlin
val a = 7
val b = 12
val mayor = if (a > b) a else b
println(mayor) // 12
```

El `if` evalúa la condición y devuelve el valor de la rama correspondiente: como `a > b` es falsa, devuelve `b`. Cuando usas `if` como expresión, el `else` es **obligatorio**, porque el programa siempre debe poder entregar un valor.

También puedes usar bloques con llaves. En ese caso, el valor de cada rama es el de su **última línea**:

```kotlin
val a = 7
val b = 12
val mayor = if (a > b) {
    println("Gana a")
    a
} else {
    println("Gana b")
    b
}
println(mayor) // 12
```

> [!NOTE]
> Si vienes de Java o C#, esto reemplaza al operador ternario (`a > b ? a : b`). Kotlin no tiene operador ternario, justamente porque el propio `if` ya devuelve un valor.

## La instrucción `when`

Cuando tienes muchas posibilidades basadas en un **mismo valor**, una cadena de `else if` se vuelve larga y difícil de leer. Para esos casos, Kotlin ofrece `when`, que compara un valor contra varias opciones.

Pensemos en un semáforo:

```kotlin
val color = "verde"
when (color) {
    "rojo" -> println("Detente")
    "amarillo" -> println("Precaución")
    "verde" -> println("Avanza")
    else -> println("Color desconocido")
}
// Salida: Avanza
```

`when` toma el valor entre paréntesis y lo compara, de arriba abajo, con cada opción escrita a la izquierda de la flecha `->`. Al encontrar la primera coincidencia, ejecuta el código de esa rama. La rama `else` significa "en cualquier otro caso" y funciona igual que el `else` de un `if`.

### `when` con varios valores

Puedes agrupar varios valores en una misma rama, separándolos con comas:

```kotlin
val dia = 7
when (dia) {
    6, 7 -> println("Fin de semana")
    else -> println("Día laboral")
}
// Salida: Fin de semana
```

### `when` como expresión

Al igual que el `if`, `when` también es una expresión y puede **devolver un valor**:

```kotlin
val dia = 3
val tipo = when (dia) {
    6, 7 -> "Fin de semana"
    else -> "Día laboral"
}
println(tipo) // Día laboral
```

Cuando se usa como expresión, el `else` también es obligatorio, por la misma razón que en el `if`: siempre debe haber un valor que devolver.

### `when` sin argumento

A veces las ramas no dependen de un único valor, sino de **condiciones distintas**. En ese caso, puedes usar `when` sin nada entre paréntesis: cada rama es una condición `Boolean` y se ejecuta la primera que sea verdadera. Es una forma más limpia de escribir una cadena de `else if`:

```kotlin
val nota = 75
val categoria = when {
    nota >= 90 -> "Excelente"
    nota >= 60 -> "Aprobado"
    else -> "Reprobado"
}
println(categoria) // Aprobado
```

> [!TIP]
> Cuando tengas una cadena larga de `else if`, evalúa reemplazarla por un `when`: suele quedar más corto y más fácil de leer.

## Resumen

En este capítulo aprendiste a que tu programa tome decisiones:

- La instrucción `if` ejecuta un bloque cuando una condición es verdadera. Con `else` añades la alternativa, y con `else if`, más casos.
- En Kotlin, `if` y `when` además son **expresiones**: devuelven un valor que puedes guardar en una variable (por eso el lenguaje no necesita un operador ternario). Al usarlos como expresión, el `else` es obligatorio.
- `when` es ideal cuando comparas un mismo valor contra muchas opciones y, en su forma sin argumento, cuando tienes varias condiciones distintas.

En el próximo capítulo veremos cómo **repetir** instrucciones con los bucles. Ahí aparecerán los **rangos** (como `1..10`), que más adelante también podrás usar dentro de un `when` para comprobar si un valor cae dentro de un intervalo.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter07.md">← Anterior</a>
        </td>
        <td style="text-align: center;">
            <a href="/README.md">Ir al índice</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter09.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->