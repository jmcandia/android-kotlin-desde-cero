# Capítulo 9: Bucles: `for`, `while` y `do-while`

- [Introducción](#introducción)
- [Rangos](#rangos)
- [El bucle `for`](#el-bucle-for)
- [El bucle `while`](#el-bucle-while)
- [El bucle `do-while`](#el-bucle-do-while)
- [`break` y `continue`](#break-y-continue)
- [¿Cuál usar?](#cuál-usar)
- [Resumen](#resumen)

---

## Introducción

En el capítulo anterior aprendiste a que tu programa **tome decisiones** con `if` y `when`. Ahora aprenderá a hacer algo igual de importante: **repetir** instrucciones.

Piensa en una tarea sencilla como mostrar los números del 1 al 100. Escribir cien `println` sería absurdo. Y si mañana necesitaras del 1 al 1000, tendrías que volver a escribirlo todo. Los **bucles** resuelven este problema: permiten ejecutar un mismo bloque de código muchas veces, sin repetirlo.

En este capítulo verás primero los **rangos**, que representan secuencias de valores, y luego los tres tipos de bucles de Kotlin: `for`, `while` y `do-while`. También aprenderás a controlar el curso de un bucle con `break` y `continue`.

## Rangos

Antes de repetir algo un número determinado de veces, necesitamos una forma de expresar, por ejemplo, "los números del 1 al 5". Para eso, Kotlin ofrece los **rangos**, que se crean con el operador `..`:

```kotlin
val numeros = 1..5 // representa 1, 2, 3, 4, 5
```

Un rango `a..b` **incluye ambos extremos**. Además, existen algunas variantes muy útiles:

- `5 downTo 1` recorre de mayor a menor: `5, 4, 3, 2, 1`.
- `1..10 step 2` avanza de dos en dos: `1, 3, 5, 7, 9`.
- `1 until 5` excluye el último valor: `1, 2, 3, 4`.

Los rangos tienen un segundo uso, además de recorrerlos. Como adelantamos en el capítulo anterior, el operador `in` permite comprobar si un valor está **dentro** de un rango, lo que devuelve un `Boolean`:

```kotlin
val n = 5
println(n in 1..10) // true
```

Esto es muy práctico dentro de un `if` o un `when`. Pero el uso más frecuente de los rangos es recorrerlos con un bucle, que es justo lo que veremos ahora.

## El bucle `for`

Supongamos que quieres saludar cinco veces. Podrías escribir cinco `println`, pero con cien saludos sería inviable. El bucle `for` repite un bloque de código **para cada valor** de un rango:

```kotlin
for (i in 1..5) {
    println("¡Hola! (vuelta $i)")
}
```

**Salida:**

```plaintext
¡Hola! (vuelta 1)
¡Hola! (vuelta 2)
¡Hola! (vuelta 3)
¡Hola! (vuelta 4)
¡Hola! (vuelta 5)
```

En cada vuelta (que se llama **iteración**), la variable `i` toma el siguiente valor del rango. No necesitas declararla con `val` ni `var`: el `for` la crea automáticamente por ti.

El `for` no se limita a los números: también puede recorrer los caracteres de una cadena, uno por uno:

```kotlin
for (letra in "Kotlin") {
    println(letra)
}
```

**Salida:**

```plaintext
K
o
t
l
i
n
```

## El bucle `while`

El `for` es ideal cuando sabes de antemano cuántas veces repetir. Pero a veces no lo sabes: lo que quieres es repetir **mientras** se cumpla una condición. Para eso está el bucle `while`, que ejecuta su bloque una y otra vez mientras la condición sea verdadera:

```kotlin
var contador = 1
while (contador <= 5) {
    println(contador)
    contador++
}
```

**Salida:**

```plaintext
1
2
3
4
5
```

Antes de cada vuelta se evalúa la condición. Cuando `contador` llega a `6`, la condición `contador <= 5` es falsa y el bucle termina.

> [!WARNING]
> Dentro del `while` debe haber algo que, tarde o temprano, haga que la condición se vuelva falsa (en el ejemplo, `contador++`). Si lo olvidas, la condición nunca cambia y el programa se queda repitiendo para siempre: es lo que se conoce como **bucle infinito**.

## El bucle `do-while`

El `while` comprueba la condición **antes** de ejecutar el bloque. Por eso, si la condición es falsa desde el principio, el bloque no se ejecuta ni una sola vez.

A veces necesitas lo contrario: ejecutar el bloque **al menos una vez** y comprobar la condición **después**. Para eso está el `do-while`:

```kotlin
var numero = 0
do {
    println("Número: $numero")
    numero++
} while (numero < 3)
```

**Salida:**

```plaintext
Número: 0
Número: 1
Número: 2
```

Aquí el bloque se ejecuta primero y la condición se evalúa al final de cada vuelta. Por eso, aunque la condición fuera falsa desde el inicio, el bloque se ejecutaría una vez.

## `break` y `continue`

Dentro de un bucle, a veces necesitas alterar su curso normal. Kotlin ofrece dos instrucciones para ello.

`break` **termina** el bucle de inmediato, aunque queden vueltas por hacer:

```kotlin
for (i in 1..10) {
    if (i == 5) break
    println(i)
}
// Imprime: 1, 2, 3, 4
```

`continue` **salta** el resto de la vuelta actual y pasa directamente a la siguiente:

```kotlin
for (i in 1..5) {
    if (i == 3) continue
    println(i)
}
// Imprime: 1, 2, 4, 5 (se salta el 3)
```

## ¿Cuál usar?

Los tres bucles pueden resolver problemas parecidos, pero cada uno brilla en una situación:

- Usa `for` cuando sepas **sobre qué** recorrer: un rango de números o los caracteres de una cadena (más adelante, también listas y colecciones).
- Usa `while` cuando repitas **mientras** se cumpla una condición y no sepas de antemano cuántas vueltas serán.
- Usa `do-while` cuando necesites que el bloque se ejecute **al menos una vez**.

## Resumen

En este capítulo aprendiste a repetir instrucciones:

- Un **rango** (`1..5`) representa una secuencia de valores, con variantes como `downTo`, `step` y `until`. Con el operador `in` también puedes comprobar si un valor cae dentro de un rango.
- El bucle `for` repite para cada valor de un rango o secuencia.
- El bucle `while` repite mientras una condición sea verdadera (cuidado con los bucles infinitos).
- El bucle `do-while` funciona como `while`, pero ejecuta el bloque al menos una vez.
- `break` termina el bucle y `continue` salta a la siguiente vuelta.

En el próximo capítulo daremos un paso importante para organizar y reutilizar tu código: aprenderás a crear tus propias **funciones**.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter08.md">← Anterior</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter10.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
