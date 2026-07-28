# Capítulo 12: Colecciones II: operaciones funcionales

- [Introducción](#introducción)
- [Antes de empezar: las lambdas](#antes-de-empezar-las-lambdas)
- [`map`: transformar cada elemento](#map-transformar-cada-elemento)
- [`filter`: quedarte con algunos elementos](#filter-quedarte-con-algunos-elementos)
- [`forEach`: hacer algo con cada elemento](#foreach-hacer-algo-con-cada-elemento)
- [Más operaciones útiles](#más-operaciones-útiles)
- [Encadenar operaciones](#encadenar-operaciones)
- [Resumen](#resumen)

---

## Introducción

En el capítulo anterior aprendiste a guardar varios valores en colecciones y a recorrerlos con un bucle `for`. Pero recorrer una colección es solo el principio: muy a menudo querrás **transformar** sus elementos (por ejemplo, aplicarles un descuento) o **seleccionar** solo algunos (los que cumplen cierta condición).

Podrías hacerlo con bucles y listas mutables, como hasta ahora. Pero Kotlin ofrece un conjunto de **operaciones funcionales** que resuelven estas tareas de forma mucho más breve y expresiva. En este capítulo conocerás las más importantes: `map`, `filter`, `forEach` y algunas más. Estas operaciones serán muy útiles en la aplicación: por ejemplo, para convertir los datos que lleguen de la PokeAPI en la lista que se muestra en pantalla.

## Antes de empezar: las lambdas

Las operaciones que veremos necesitan que les digas **qué hacer con cada elemento** de la colección. Para eso, les pasas una pequeña función escrita en el momento y sin nombre, llamada **lambda**.

Una lambda se escribe entre llaves `{ }`. Por ejemplo, esta toma un número y devuelve su doble:

```kotlin
{ numero -> numero * 2 }
```

A la izquierda de la flecha `->` va el parámetro (el dato que entra); a la derecha, lo que la lambda devuelve.

Cuando la lambda tiene un **solo parámetro**, Kotlin te permite omitir su nombre y referirte a él con la palabra `it`. Así, estas dos lambdas son equivalentes:

```kotlin
{ numero -> numero * 2 }
{ it * 2 }
```

Con esta idea básica ya podemos usar las operaciones sobre colecciones.

> [!NOTE]
> Esto es solo una primera aproximación a las lambdas, la necesaria para trabajar con colecciones. Más adelante, en el capítulo de lambdas y funciones de orden superior, las estudiaremos a fondo. Por ahora, quédate con que una lambda es una función pequeña que le pasas a otra función.

## `map`: transformar cada elemento

Imagina que tienes una lista de precios sin impuesto y quieres una **nueva** lista con el impuesto ya aplicado. Con lo que sabes hasta ahora, tendrías que crear una lista vacía y llenarla, elemento por elemento, dentro de un bucle.

La operación `map` hace todo eso por ti: crea una colección nueva aplicando una transformación a **cada** elemento. Le pasas una lambda que describe la transformación:

```kotlin
val precios = listOf(100, 200, 300)
val conImpuesto = precios.map { it + it / 10 }
println(conImpuesto) // [110, 220, 330]
```

`map` recorre la lista, aplica la lambda a cada elemento y junta los resultados en una lista nueva. La lista original no se modifica.

La transformación puede ser cualquier cosa. Por ejemplo, obtener la longitud de cada palabra:

```kotlin
val palabras = listOf("hola", "kotlin", "sol")
val longitudes = palabras.map { it.length }
println(longitudes) // [4, 6, 3]
```

## `filter`: quedarte con algunos elementos

Otras veces no quieres transformar los elementos, sino **seleccionar** solo los que cumplen una condición. Para eso está `filter`, que crea una nueva colección con los elementos para los que la lambda devuelve `true`:

```kotlin
val numeros = listOf(1, 2, 3, 4, 5, 6)
val pares = numeros.filter { it % 2 == 0 }
println(pares) // [2, 4, 6]
```

La lambda `{ it % 2 == 0 }` es una condición: para cada número, indica si debe conservarse (`true`) o descartarse (`false`). `filter` se queda solo con los que la cumplen.

## `forEach`: hacer algo con cada elemento

Si solo quieres **ejecutar una acción** con cada elemento (por ejemplo, imprimirlo), sin crear una colección nueva, usa `forEach`:

```kotlin
val frutas = listOf("manzana", "pera", "naranja")
frutas.forEach { println(it) }
```

Es una forma compacta de escribir un bucle. De hecho, `frutas.forEach { println(it) }` hace lo mismo que:

```kotlin
for (fruta in frutas) {
    println(fruta)
}
```

La diferencia es de estilo: `forEach` es más breve, mientras que el `for` puede resultar más claro cuando el cuerpo es largo.

## Más operaciones útiles

Kotlin ofrece muchas más operaciones sobre colecciones. Estas son algunas de las más frecuentes:

- `count { ... }` — cuenta cuántos elementos cumplen una condición.
- `any { ... }` — devuelve `true` si **al menos uno** cumple la condición.
- `all { ... }` — devuelve `true` si **todos** la cumplen.
- `find { ... }` — devuelve el **primer** elemento que cumple la condición.

Veámoslas con una lista de números:

```kotlin
val numeros = listOf(4, 8, 15, 16, 23, 42)

println(numeros.count { it > 10 }) // 4    (cuántos cumplen)
println(numeros.any { it > 40 })   // true (¿hay al menos uno?)
println(numeros.all { it > 0 })    // true (¿todos cumplen?)
println(numeros.find { it > 10 })  // 15   (el primero que cumple)
```

Y para ordenar, `sorted()` devuelve la colección ordenada, o `sortedBy { ... }` la ordena según un criterio que tú definas:

```kotlin
val palabras = listOf("pera", "kiwi", "manzana")
println(palabras.sorted())               // [kiwi, manzana, pera] — orden alfabético
println(palabras.sortedBy { it.length }) // [pera, kiwi, manzana] — por longitud
```

## Encadenar operaciones

La verdadera potencia de estas operaciones aparece al **encadenarlas**: el resultado de una alimenta a la siguiente. Por ejemplo, para obtener los cuadrados de los números pares de una lista:

```kotlin
val numeros = listOf(1, 2, 3, 4, 5, 6)
val resultado = numeros
    .filter { it % 2 == 0 } // primero, nos quedamos con los pares: [2, 4, 6]
    .map { it * it }        // luego, elevamos cada uno al cuadrado: [4, 16, 36]
println(resultado) // [4, 16, 36]
```

Se lee de arriba abajo, como una serie de pasos: filtra y luego transforma. Compáralo con la versión equivalente usando un bucle y una lista mutable, y verás cuánto más claro y directo resulta.

## Resumen

En este capítulo aprendiste a trabajar con colecciones de forma expresiva:

- Las **operaciones funcionales** transforman y consultan colecciones sin necesidad de escribir bucles a mano.
- Una **lambda** (`{ it * 2 }`) es una función pequeña que le pasas a la operación para indicar qué hacer con cada elemento.
- `map` transforma cada elemento en uno nuevo; `filter` selecciona los que cumplen una condición; `forEach` ejecuta una acción con cada uno.
- Otras operaciones útiles: `count`, `any`, `all`, `find`, `sorted` y `sortedBy`.
- Puedes **encadenar** operaciones para expresar transformaciones complejas paso a paso.

Casi todas estas operaciones crean colecciones **nuevas** (o devuelven un valor), sin modificar la original. Las volverás a usar constantemente en la aplicación.

En el próximo capítulo aprenderás sobre el **null safety** de Kotlin: cómo el lenguaje te ayuda a manejar de forma segura la *ausencia de un valor*, ese concepto que hemos ido mencionando a lo largo del curso.

---

<!-- markdownlint-disable MD033 -->
<table style="width: 100%; border-collapse: collapse;">
    <tr>
        <td style="text-align: left;">
            <a href="/chapter11.md">← Anterior</a>
        </td>
        <td style="text-align: right;">
            <a href="/chapter13.md">Siguiente →</a>
        </td>
    </tr>
</table>
<!-- markdownlint-enable MD033 -->
