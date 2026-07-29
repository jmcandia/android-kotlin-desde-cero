# Capítulo 13: Null safety: manejo seguro de valores nulos

## Introducción

A lo largo del curso hemos mencionado varias veces la "ausencia de un valor". Ha llegado el momento de abordarla de frente.

En la vida real, muchos datos pueden simplemente **no existir**: una persona puede no tener segundo nombre, una búsqueda puede no encontrar resultados, una clave puede no estar en un mapa. Para representar esa ausencia, los lenguajes de programación usan un valor especial: **`null`** ("nada" o "ningún valor").

El problema es que `null` ha causado incontables errores a lo largo de la historia de la programación: si intentas usar como un dato normal algo que en realidad es `null`, el programa se cae. Kotlin fue diseñado para evitar precisamente eso, con un sistema llamado **null safety** ("seguridad frente a nulos") que te obliga a manejar la posible ausencia de valor **antes** de que cause un problema.

> [!NOTE]
> Si vienes de Java, seguramente conoces el temido `NullPointerException`. El null safety de Kotlin existe justamente para prevenir ese tipo de error, y lo hace en el momento de compilar, no cuando el programa ya está corriendo.

En este capítulo aprenderás qué es `null`, cómo Kotlin distingue los valores que pueden ser nulos de los que no, y las herramientas que ofrece para trabajar con ellos de forma segura.

## ¿Qué es `null`?

`null` es un valor especial que representa la **ausencia de un valor**. Cuando una variable es `null`, significa que no contiene ningún dato: no es un cero, ni una cadena vacía, sino literalmente "nada".

Por ejemplo, si guardas el segundo nombre de una persona y esa persona no tiene, tendría sentido que ese dato fuera `null`: no es que su segundo nombre sea un texto vacío, es que no existe.

### `null` no es lo mismo que `Unit`

En el capítulo de funciones conociste `Unit`, el valor que devuelve una función cuando no produce un resultado útil (como una función que solo imprime algo). Es fácil confundirlo con `null`, pero son cosas muy distintas:

| | `Unit` | `null` |
| :--- | :--- | :--- |
| ¿Qué es? | Un valor real | La ausencia de un valor |
| ¿Cuándo aparece? | Una función que no devuelve nada útil | Un dato que podría no existir |
| ¿Hay "algo"? | Sí, el propio valor `Unit` | No, no hay ningún valor |

En pocas palabras: `Unit` **es** un valor (que significa "listo, sin resultado útil"), mientras que `null` es la **ausencia** de un valor ("aquí no hay nada").

## Tipos que aceptan null

Aquí está la primera gran idea del null safety de Kotlin: **por defecto, las variables no pueden ser `null`**. Un tipo normal como `String` solo puede contener una cadena de verdad, nunca `null`:

```kotlin
val nombre: String = null // error: null no puede asignarse a un String
```

Para permitir que una variable sea `null`, debes marcar su tipo como **anulable** (*nullable*), añadiéndole un signo de interrogación `?`:

```kotlin
val segundoNombre: String? = null // correcto: String? sí acepta null
```

Con esa `?`, le dices a Kotlin —y a quien lea tu código— que ese valor podría no existir. Esta distinción es la base de todo: el propio tipo indica si algo puede ser `null` o no.

## El compilador te protege

¿Por qué es tan útil esta distinción? Porque el compilador la aprovecha para protegerte. Sobre un tipo normal, puedes usar los valores con total confianza:

```kotlin
val nombre: String = "Ana"
println(nombre.length) // 3, sin problemas
```

Pero sobre un tipo anulable, Kotlin **no te deja** acceder directamente, porque el valor podría ser `null` (y usar algo que es `null` provocaría un error):

```kotlin
val segundoNombre: String? = null
println(segundoNombre.length) // error: segundoNombre podría ser null
```

En lugar de permitir que tu programa se caiga cuando ya está en ejecución, Kotlin te avisa en el momento de compilar y te obliga a decidir qué hacer si el valor es `null`. Veamos las herramientas para eso.

## Herramientas para manejar null

### La llamada segura `?.`

La **llamada segura** `?.` accede a una propiedad o método solo si el valor **no** es `null`. Si es `null`, toda la expresión vale `null`, sin provocar ningún error:

```kotlin
val segundoNombre: String? = null
println(segundoNombre?.length) // null (no se cae)

val otro: String? = "Luis"
println(otro?.length) // 4
```

### El operador Elvis `?:`

A menudo, cuando un valor es `null`, quieres usar un **valor por defecto** en su lugar. Para eso está el operador **Elvis** `?:` (se llama así porque, girado de lado, recuerda al peinado de Elvis). Devuelve el valor de la izquierda si no es `null`, o el de la derecha si lo es:

```kotlin
val segundoNombre: String? = null
val nombreAMostrar = segundoNombre ?: "(sin segundo nombre)"
println(nombreAMostrar) // (sin segundo nombre)
```

Se combina muy bien con la llamada segura. Por ejemplo, para obtener la longitud, o `0` si el valor es `null`:

```kotlin
val longitud = segundoNombre?.length ?: 0
println(longitud) // 0
```

### Comprobar con `if`

También puedes comprobar explícitamente si un valor es `null` con un `if`. Lo interesante es que, dentro del bloque donde ya verificaste que no es `null`, Kotlin te permite usarlo como un tipo normal:

```kotlin
val segundoNombre: String? = "Elena"

if (segundoNombre != null) {
    println(segundoNombre.length) // 5, aquí Kotlin ya sabe que no es null
}
```

A esta comodidad se le llama *smart cast* ("conversión inteligente"): tras comprobar que algo no es `null`, Kotlin lo trata como no anulable dentro de ese bloque, sin que tengas que hacer nada más.

### La aserción `!!`

Por último, existe el operador `!!`, que le dice a Kotlin: "confía en mí, esto no es `null`". Convierte un tipo anulable en no anulable, pero si te equivocas y el valor **sí** es `null`, el programa se cae con un error:

```kotlin
val segundoNombre: String? = "Diego"
println(segundoNombre!!.length) // 5

val vacio: String? = null
println(vacio!!.length) // error en tiempo de ejecución: el valor era null
```

Ya te habías cruzado con `!!` en el capítulo de entrada y salida, con `readLine()!!`. Úsalo con cuidado y solo cuando estés completamente seguro de que el valor no puede ser `null`; en la mayoría de los casos, `?.` y `?:` son opciones más seguras.

## ¿De dónde salen los valores null?

Ya te has topado con `null` sin darte cuenta. Por ejemplo, cuando accedes a un mapa con una clave que **no** existe, obtienes `null`:

```kotlin
val edades = mapOf("Ana" to 30)
println(edades["Ana"])   // 30
println(edades["Pedro"]) // null (esa clave no está)
```

Por eso, el acceso a un mapa por clave devuelve un tipo anulable. Lo mismo ocurre con operaciones como `find`, que devuelve `null` si ningún elemento cumple la condición. Ahora ya tienes las herramientas para manejar esos casos de forma segura; por ejemplo, con el operador Elvis:

```kotlin
val edad = edades["Pedro"] ?: 0
println(edad) // 0
```

## Resumen

En este capítulo aprendiste a manejar la ausencia de valores de forma segura:

- `null` representa la **ausencia de un valor**. No es lo mismo que `Unit`, que es un valor real que significa "sin resultado útil".
- Por defecto, en Kotlin los tipos **no** aceptan `null`. Para permitirlo, marca el tipo con `?` (por ejemplo, `String?`).
- El compilador no te deja usar directamente un valor anulable, lo que evita errores en tiempo de ejecución.
- Herramientas para manejar nulos:
  - `?.` (llamada segura): accede solo si el valor no es `null`.
  - `?:` (Elvis): usa un valor por defecto cuando algo es `null`.
  - `if (x != null)`: comprueba y, dentro del bloque, trata el valor como no anulable (*smart cast*).
  - `!!`: fuerza el valor como no nulo, pero se cae si era `null` (úsalo con cuidado).

El null safety es una de las grandes ventajas de Kotlin: convierte una fuente clásica de errores en algo que el compilador te ayuda a controlar.

En el próximo capítulo veremos cómo reaccionar ante situaciones inesperadas con el **manejo de excepciones** (`try`, `catch`).
