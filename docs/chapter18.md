# Capítulo 18: `data class`, `copy` y desestructuración

## Introducción

En los capítulos anteriores creaste clases con datos y comportamiento. Pero muy a menudo necesitarás clases cuyo **único propósito** es **guardar datos**: un usuario con su nombre y su correo, un punto con sus coordenadas, un producto con su precio. No tienen lógica compleja; solo agrupan información.

Para esos casos, escribir una clase normal resulta más trabajoso de lo necesario. Kotlin ofrece un atajo pensado justo para esto: la **`data class`** (clase de datos), que genera automáticamente varias funciones útiles. En este capítulo verás qué te regala una `data class`, cómo crear copias modificadas con `copy` y cómo repartir sus datos en variables mediante la **desestructuración** (que veníamos anunciando desde capítulos anteriores).

Este tema es muy común en el desarrollo Android: los datos que recibes de una API, de una base de datos o de un formulario suelen modelarse, precisamente, como `data class`.

## El problema: clases que solo guardan datos

Supongamos una clase normal para representar un usuario:

```kotlin
class Usuario(val nombre: String, val edad: Int)
```

Funciona, pero al usarla notarás tres molestias. Primero, si la imprimes, no ves sus datos, sino algo críptico:

```kotlin
val usuario = Usuario("Ana", 30)
println(usuario) // Usuario@1b6d3586  (poco útil)
```

Segundo, dos usuarios con **los mismos datos** no se consideran iguales al compararlos con `==`:

```kotlin
val u1 = Usuario("Ana", 30)
val u2 = Usuario("Ana", 30)
println(u1 == u2) // false  (aunque tengan los mismos datos)
```

Y tercero, no hay una forma cómoda de crear una copia con algún dato cambiado.

Todo esto ocurre porque, para Kotlin, `Usuario` es una clase cualquiera: no sabe que su propósito es representar datos.

## La solución: `data class`

Basta con anteponer la palabra clave `data` a la clase:

```kotlin
data class Usuario(val nombre: String, val edad: Int)
```

Con ese simple cambio, Kotlin genera automáticamente varias funciones basadas en las propiedades del constructor. Veamos las más importantes.

### `toString()` legible

Ahora, al imprimir un objeto, ves sus datos de forma clara:

```kotlin
val usuario = Usuario("Ana", 30)
println(usuario) // Usuario(nombre=Ana, edad=30)
```

### Comparación por contenido

Dos objetos con los mismos datos ahora **sí** se consideran iguales:

```kotlin
val u1 = Usuario("Ana", 30)
val u2 = Usuario("Ana", 30)
println(u1 == u2) // true
```

Una `data class` compara por el **contenido** (los valores de sus propiedades), no por si son el mismo objeto en memoria.

> [!NOTE]
> En Kotlin, `==` compara contenido (por dentro llama al método `equals()`). Si vienes de Java, ten presente que allí `==` compara referencias; el equivalente en Kotlin para comparar referencias es `===`.

### `copy()`: copias modificadas

Como muchas `data class` se diseñan con propiedades `val` (inmutables), no puedes cambiar un objeto existente. En su lugar, creas una **copia** con los cambios que quieras, usando `copy()`:

```kotlin
val usuario = Usuario("Ana", 30)
val usuarioMayor = usuario.copy(edad = 31)

println(usuario)      // Usuario(nombre=Ana, edad=30)  (sin cambios)
println(usuarioMayor) // Usuario(nombre=Ana, edad=31)  (copia con edad nueva)
```

`copy()` crea un objeto nuevo idéntico al original, salvo las propiedades que le indiques (aquí, `edad`). El objeto original no se toca. Esta es una forma muy común y segura de "modificar" datos inmutables: en lugar de cambiar el objeto, produces uno nuevo.

### Desestructuración

Por último, una `data class` te permite **repartir** sus propiedades en variables separadas de una sola vez. A esto se le llama **desestructuración**, y es lo que veníamos anunciando en capítulos anteriores:

```kotlin
val usuario = Usuario("Ana", 30)
val (nombre, edad) = usuario

println(nombre) // Ana
println(edad)   // 30
```

En una sola línea, `val (nombre, edad) = usuario` crea dos variables y les asigna, **en orden**, las propiedades del objeto (por eso el orden de las variables debe coincidir con el del constructor).

Esto es especialmente cómodo al recorrer colecciones. ¿Recuerdas cómo recorríamos un mapa?

```kotlin
val edades = mapOf("Ana" to 30, "Diego" to 25)
for ((nombre, edad) in edades) {
    println("$nombre tiene $edad años")
}
```

Eso funciona porque cada par de un mapa es, por dentro, un objeto que se puede desestructurar, igual que una `data class`.

## ¿Cuándo usar una `data class`?

Usa una `data class` cuando el propósito principal de la clase sea **contener datos**, sin lógica compleja: modelos de información, resultados, configuraciones, respuestas de una API.

Para clases en las que lo importante es el **comportamiento** o la **identidad** (no sus datos), o que participan en jerarquías de herencia, es mejor una clase normal. De hecho, una `data class` no puede ser `abstract` ni `open`, y debe tener al menos una propiedad en su constructor.

En resumen: si te descubres creando una clase solo para agrupar unos cuantos datos, casi siempre querrás que sea una `data class`.

## Resumen

En este capítulo conociste una de las herramientas más prácticas de Kotlin:

- Una **`data class`** es una clase pensada para **guardar datos**. Se declara anteponiendo `data`.
- Kotlin le genera automáticamente: un `toString()` legible, comparación por contenido (`==`), un método `copy()` y soporte para desestructuración.
- `copy()` crea un objeto nuevo con algunas propiedades cambiadas, sin modificar el original: ideal para datos inmutables (`val`).
- La **desestructuración** (`val (a, b) = objeto`) reparte las propiedades en variables, en el orden del constructor.
- Usa `data class` para clases que principalmente contienen datos.

En el próximo capítulo verás `object` y `companion object`, que te permitirán crear objetos únicos (*singletons*) y agrupar funciones y constantes ligadas a una clase.
