# Capítulo 10: Funciones: definición, parámetros y valores de retorno

- [Introducción](#introducción)
- [¿Para qué sirven las funciones?](#para-qué-sirven-las-funciones)
- [Invocar una función](#invocar-una-función)
- [Definir tu propia función](#definir-tu-propia-función)
- [Parámetros y argumentos](#parámetros-y-argumentos)
- [Valores de retorno](#valores-de-retorno)
- [`Unit`: cuando una función no devuelve nada](#unit-cuando-una-función-no-devuelve-nada)
- [Funciones de una sola expresión](#funciones-de-una-sola-expresión)
- [Argumentos por defecto](#argumentos-por-defecto)
- [Argumentos con nombre](#argumentos-con-nombre)
- [Ámbito de las variables](#ámbito-de-las-variables)
- [Descomponer un problema en funciones](#descomponer-un-problema-en-funciones)
- [Resumen](#resumen)

---

## Introducción

Ya has usado varias funciones sin detenerte a pensarlo: `println`, `readln`, `toInt`. Una **función** es un bloque de código con nombre que realiza una tarea concreta. La invocas por su nombre cuando la necesitas, y hace su trabajo.

En el capítulo de bucles viste cómo evitar repetir código para hacer lo mismo muchas veces seguidas. Las funciones resuelven un problema relacionado, pero distinto: te permiten **agrupar** un conjunto de instrucciones bajo un nombre para **reutilizarlas** desde distintas partes del programa, y así mantener tu código ordenado y legible.

En este capítulo aprenderás a invocar funciones, a **definir las tuyas propias**, a pasarles datos mediante parámetros y a obtener resultados con valores de retorno.

## ¿Para qué sirven las funciones?

Antes de seguir, vale la pena detenerse en *por qué* las funciones son tan importantes. Una función bien pensada te aporta varias ventajas:

- **Evitar la repetición.** Si necesitas la misma lógica en varios lugares, la escribes una vez dentro de una función y la invocas cuando haga falta. Si algún día hay que corregirla, la cambias en un solo sitio. Este es uno de los principios más conocidos del desarrollo de software: **DRY** (*Don't Repeat Yourself*, «no te repitas»).
- **Legibilidad.** Un nombre como `calcularTotal()` describe *qué* hace ese bloque sin obligarte a leer todas sus líneas. Tu programa pasa a leerse casi como una lista de pasos.
- **Dividir un problema grande en partes pequeñas.** En lugar de un `main` enorme y difícil de seguir, repartes el trabajo en varias funciones, cada una con una responsabilidad clara. Es mucho más fácil resolver (y corregir) problemas pequeños que uno gigante.

> [!TIP]
> Como buena práctica, dale a cada función un nombre que describa su acción (a menudo empezando por un verbo: `calcularTotal`, `mostrarMenu`, `validarEdad`) y procura que haga **una sola cosa**. Que cada función tenga una única responsabilidad se conoce como principio **SRP** (*Single Responsibility Principle*), el primero de los cinco principios **SOLID**. Junto con DRY, KISS y el resto de SOLID, los definimos con más calma en el [anexo de principios de diseño](/appendix-design-principle.md).

## Invocar una función

Cuando quieres usar una función, la **invocas** (o la llamas) escribiendo su nombre seguido de paréntesis. Ya lo has hecho muchas veces:

```kotlin
println("Hola")
```

Si la función necesita datos de entrada, se los pasas entre los paréntesis. Esos datos se llaman **argumentos**:

```kotlin
val texto = "Hola"
println(texto) // le pasamos un argumento
```

Algunas funciones no necesitan ningún argumento; en ese caso, los paréntesis van vacíos. Por ejemplo, `println()` sin argumentos imprime una línea en blanco:

```kotlin
println()
```

Así, en su forma general, una función puede recibir cero, uno o varios argumentos:

```kotlin
funcion1()            // sin argumentos
funcion2(arg1)        // un argumento
funcion3(arg1, arg2)  // dos argumentos
```

## Definir tu propia función

Hasta ahora usamos funciones que Kotlin ya trae listas. Pero lo más potente es que puedes **crear las tuyas**.

Imagina que en varios lugares de tu programa necesitas mostrar un saludo de bienvenida. En vez de repetir el mismo `println` una y otra vez, defínelo una sola vez dentro de una función:

```kotlin
fun saludar() {
    println("¡Hola! Bienvenido.")
}
```

Analicemos sus partes:

- `fun` es la palabra clave para **definir** una función.
- `saludar` es el **nombre** que le damos (con las mismas reglas que los nombres de variables y en `lowerCamelCase`).
- Los paréntesis `()` contienen los parámetros (aquí, ninguno).
- Entre las llaves `{ }` va el **cuerpo**: las instrucciones que se ejecutan cuando la invocas.

Definir una función no ejecuta su código; solo lo deja preparado. Para ejecutarlo, hay que **invocarla** por su nombre:

```kotlin
fun main() {
    saludar() // ¡Hola! Bienvenido.
    saludar() // podemos llamarla las veces que queramos
}

fun saludar() {
    println("¡Hola! Bienvenido.")
}
```

## Parámetros y argumentos

Nuestra función `saludar` siempre dice lo mismo. Sería más útil si pudiera saludar a una persona por su nombre. Para pasarle información, usamos **parámetros**.

Un parámetro es una variable que la función recibe entre sus paréntesis; se declara con un nombre y un tipo:

```kotlin
fun saludar(nombre: String) {
    println("¡Hola, $nombre!")
}
```

Ahora, al invocarla, le pasamos un **argumento**:

```kotlin
saludar("Ana")   // ¡Hola, Ana!
saludar("Diego") // ¡Hola, Diego!
```

Fíjate en la diferencia: el **parámetro** (`nombre`) es la variable que aparece en la *definición*; el **argumento** (`"Ana"`) es el valor concreto que pasas al *invocarla*.

Una función puede tener varios parámetros, separados por comas:

```kotlin
fun presentar(nombre: String, edad: Int) {
    println("$nombre tiene $edad años.")
}

presentar("Ana", 30) // Ana tiene 30 años.
```

## Valores de retorno

Algunas funciones no solo hacen algo, sino que **calculan y devuelven** un resultado que puedes usar después. Para eso se indica el **tipo de retorno** después de los paréntesis (tras dos puntos) y se usa la palabra clave `return` para entregar el valor:

```kotlin
fun sumar(a: Int, b: Int): Int {
    return a + b
}
```

Aquí, `: Int` indica que la función devuelve un `Int`. Al invocarla, puedes guardar ese resultado en una variable:

```kotlin
val resultado = sumar(3, 4)
println(resultado) // 7
```

La palabra `return` hace dos cosas: entrega el valor y **termina** la función de inmediato, de modo que las líneas que vinieran después no se ejecutarían.

Las funciones que reciben datos y devuelven un resultado se parecen mucho a las funciones matemáticas: reciben una entrada y producen una salida.

## `Unit`: cuando una función no devuelve nada

¿Y qué pasa con funciones como `saludar`, que no devuelven ningún resultado? En realidad, en Kotlin **todas** las funciones devuelven algo. Cuando una función no devuelve un valor útil, devuelve un valor especial llamado `Unit`, que en la práctica significa "no hay resultado".

No necesitas escribir `Unit` ni `return`: si no indicas un tipo de retorno, Kotlin asume que la función devuelve `Unit`. Por eso `saludar` funciona sin ninguno de los dos.

> [!NOTE]
> Si vienes de Java o C#, puedes pensar en `Unit` como el equivalente de `void`.

## Funciones de una sola expresión

Muchas funciones simplemente devuelven el resultado de una única expresión, como `sumar`. Para esos casos, Kotlin ofrece una forma más breve: en lugar de llaves y `return`, usas un signo `=`:

```kotlin
fun sumar(a: Int, b: Int) = a + b
```

Esta es una **función de una sola expresión**. Kotlin infiere el tipo de retorno a partir de la expresión (aquí, `Int`), así que no hace falta escribirlo. Es equivalente a la versión con llaves y `return`, pero más compacta:

```kotlin
val resultado = sumar(3, 4)
println(resultado) // 7
```

## Argumentos por defecto

A veces quieres que un parámetro tenga un valor predeterminado, para no tener que pasarlo siempre. Puedes darle un **valor por defecto** en la definición, con `=`:

```kotlin
fun saludar(nombre: String = "invitado") {
    println("¡Hola, $nombre!")
}

saludar()      // ¡Hola, invitado!  (usa el valor por defecto)
saludar("Ana") // ¡Hola, Ana!       (usa el argumento que le pasamos)
```

Si no pasas ese argumento, la función usa el valor por defecto; si lo pasas, usa el tuyo.

## Argumentos con nombre

Cuando una función tiene varios parámetros, al invocarla puedes indicar **el nombre** de cada argumento. Esto hace la llamada más clara y, además, te permite pasarlos en cualquier orden:

```kotlin
fun crearRectangulo(ancho: Int, alto: Int) {
    println("Rectángulo de $ancho x $alto")
}

crearRectangulo(alto = 3, ancho = 5) // Rectángulo de 5 x 3
```

Los **argumentos con nombre** son especialmente útiles cuando una función tiene muchos parámetros o varios valores por defecto, porque dejan claro qué significa cada valor en la llamada.

## Ámbito de las variables

Cuando declaras una variable **dentro** de una función, esa variable solo existe ahí: nace cuando la función se ejecuta y desaparece cuando termina. Se le llama **variable local**, y no puedes usarla desde fuera de la función. Lo mismo ocurre con los parámetros: son variables locales de su función.

```kotlin
fun calcular() {
    val resultado = 2 + 2 // variable local
    println(resultado)    // aquí sí existe
}

fun main() {
    calcular()
    println(resultado) // error: 'resultado' no existe fuera de calcular()
}
```

A la zona del programa donde una variable existe y se puede usar se le llama su **ámbito** (en inglés, *scope*). Que cada función tenga su propio ámbito es algo bueno: te permite reutilizar nombres como `resultado` o `i` en distintas funciones sin que se estorben, y evita que una parte del programa modifique por error variables de otra.

## Descomponer un problema en funciones

Veamos cómo estas ideas se combinan en un pequeño programa. Supongamos que queremos saludar a una persona y decirle si es mayor de edad. En lugar de meter todo dentro de `main`, repartimos el trabajo en funciones pequeñas:

```kotlin
fun esMayorDeEdad(edad: Int): Boolean {
    return edad >= 18
}

fun saludar(nombre: String, edad: Int) {
    println("¡Hola, $nombre!")
    if (esMayorDeEdad(edad)) {
        println("Eres mayor de edad.")
    } else {
        println("Eres menor de edad.")
    }
}

fun main() {
    saludar("Ana", 20)
}
```

**Salida:**

```plaintext
¡Hola, Ana!
Eres mayor de edad.
```

Fíjate en cómo se reparte el trabajo: `main` invoca a `saludar`, y `saludar`, a su vez, invoca a `esMayorDeEdad`. Cada función hace una parte pequeña y bien definida, y juntas resuelven el problema completo. Cuando invocas una función, el programa "salta" a ejecutar su cuerpo y, al terminar (o al llegar a un `return`), vuelve al punto desde donde la llamaste para continuar.

## Resumen

En este capítulo aprendiste a trabajar con funciones:

- Las funciones te ayudan a **evitar la repetición**, mejorar la legibilidad y dividir un problema grande en partes pequeñas.
- Una función es un bloque de código con nombre que realiza una tarea. La **invocas** por su nombre seguido de paréntesis.
- Defines tus propias funciones con `fun`. Los **parámetros** (en la definición) reciben datos, y los **argumentos** (en la invocación) son los valores concretos que pasas.
- Con un **tipo de retorno** y `return`, una función devuelve un resultado. Si no devuelve nada útil, devuelve `Unit`.
- Las **funciones de una sola expresión** (`fun f() = ...`) son una forma breve de escribir funciones sencillas.
- Los **argumentos por defecto** y los **argumentos con nombre** hacen tus funciones más flexibles y tus llamadas más claras.
- Las variables declaradas dentro de una función son **locales**: solo existen dentro de su ámbito.

Con las funciones completas los fundamentos de la programación en Kotlin. En la próxima parte del curso empezarás a trabajar con **colecciones**, que te permitirán manejar conjuntos de datos como listas de elementos.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter09.md">← Anterior</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter11.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
