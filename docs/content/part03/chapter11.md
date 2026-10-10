# Capítulo 11: Colecciones I: `List`, `Set` y `Map`

> [!NOTE]Lo que aprenderás en esta parte
> Al terminar la **Parte III** podrás:
>
> - Guardar y recorrer grupos de datos con `List`, `Set` y `Map`, eligiendo el adecuado.
> - Transformar y filtrar colecciones con `map`, `filter` y `forEach`.
> - Evitar los errores más comunes por valores nulos con el sistema de *null safety*.
> - Manejar errores con `try`/`catch`/`finally` y con el tipo `Result`.

> [!TIP]Antes de empezar, recupera lo que ya sabes
> Responde de memoria; no importa acertar, se trata de activar lo que ya sabes. Vuelve a estas preguntas al terminar el capítulo.
>
> 1. ¿Cuál es la diferencia entre declarar una variable con `val` y con `var`?
> 2. ¿Cómo escribirías un `for` que recorra los números del 1 al 5?
> 3. ¿Qué devuelve la expresión `3 in 1..5`?

## Introducción

Hasta ahora, cada variable guardaba **un solo valor**: un número, un texto, un booleano. Pero muchas veces necesitas manejar **muchos** valores relacionados: los nombres de tus contactos, los productos de un carrito de compras, las puntuaciones de una partida.

Podrías crear una variable para cada uno (`nombre1`, `nombre2`, `nombre3`…), pero se vuelve inmanejable enseguida: ¿y si son cien? ¿y si no sabes cuántos habrá?

Para eso existen las **colecciones**: estructuras que guardan varios valores bajo un mismo nombre. En este capítulo conocerás las tres más importantes de Kotlin —**listas**, **conjuntos** y **mapas**—, cuándo usar cada una y cómo trabajar con ellas. Más adelante te serán imprescindibles: casi cualquier aplicación necesita manejar conjuntos de datos (una lista de contactos, un carrito de compras, un conjunto de etiquetas), y las colecciones son la herramienta para ello.

## Listas

Una **lista** (`List`) es una colección **ordenada** de elementos que, además, admite **duplicados**. "Ordenada" significa que cada elemento ocupa una posición fija y puedes acceder a él por esa posición.

### Crear y leer una lista

Para crear una lista, usa la función `listOf()` y coloca los elementos entre paréntesis, separados por comas:

```kotlin
val frutas = listOf("manzana", "pera", "naranja")
```

Cada elemento tiene una posición, llamada **índice**, que empieza en `0` (no en 1). Para acceder a un elemento, escribes el nombre de la lista y su índice entre corchetes:

```kotlin
println(frutas[0]) // manzana
println(frutas[1]) // pera
println(frutas[2]) // naranja
```

> [!WARNING] Advertencia
> Como los índices empiezan en 0, en una lista de 3 elementos las posiciones válidas son 0, 1 y 2. Si pides un índice que no existe (por ejemplo, `frutas[5]`), el programa se detiene con un error.

Para saber cuántos elementos tiene una lista, usa la propiedad `.size`:

```kotlin
println(frutas.size) // 3
```

Y para obtener el primer y el último elemento, tienes `.first()` y `.last()`:

```kotlin
println(frutas.first()) // manzana
println(frutas.last())  // naranja
```

### Recorrer una lista

Como una lista es una secuencia de elementos, puedes recorrerla con un bucle `for`, tal como recorrías un rango en el capítulo de bucles:

```kotlin
for (fruta in frutas) {
    println(fruta)
}
```

**Salida:**

```plaintext
manzana
pera
naranja
```

### Buscar en una lista

Para comprobar si un elemento está en la lista, usa el operador `in` (el mismo que viste con los rangos). Devuelve un `Boolean`:

```kotlin
println("pera" in frutas) // true
println("kiwi" in frutas) // false
```

Las listas creadas con `listOf()` son de **solo lectura**: puedes consultarlas, pero no puedes agregar, quitar ni cambiar sus elementos. Si lo intentas, el código no compila. ¿Y si necesitas una lista que cambie? Para eso está la lista mutable.

## Listas mutables

Cuando necesitas una lista que **cambie** con el tiempo —agregar un producto al carrito, quitar una tarea completada—, usas una **lista mutable** (`MutableList`), que se crea con `mutableListOf()`:

```kotlin
val tareas = mutableListOf("Estudiar", "Cocinar")
 
tareas.add("Dormir")     // agrega al final: [Estudiar, Cocinar, Dormir]
tareas.remove("Cocinar") // quita un elemento: [Estudiar, Dormir]
tareas[0] = "Programar"  // cambia el elemento de la posición 0: [Programar, Dormir]
 
println(tareas) // [Programar, Dormir]
```

### `val` y colecciones mutables

Quizás te llame la atención que hayamos declarado `tareas` con `val` y, aun así, la hayamos modificado. Aquí aparece un matiz importante que anticipamos al hablar de variables: **`val` impide reasignar la variable, pero no congela su contenido**.

```kotlin
val tareas = mutableListOf("Estudiar")
tareas.add("Cocinar") // correcto: modificamos el contenido de la lista

tareas = mutableListOf("Otra cosa") // error: no podemos reasignar un val
```

En otras palabras, con `val` la variable `tareas` siempre apuntará a **la misma lista**, pero esa lista puede cambiar por dentro. Si además quieres que el contenido tampoco cambie, usa una lista de solo lectura (`listOf`).

## Conjuntos (`Set`)

Un **conjunto** (`Set`) es una colección que **no admite elementos duplicados**. A diferencia de una lista, no está pensado para acceder a los elementos por su posición, sino para responder rápido a la pregunta "¿está este elemento aquí?".

Se crea con `setOf()`:

```kotlin
val colores = setOf("rojo", "verde", "rojo", "azul")
println(colores)      // [rojo, verde, azul] — el duplicado se descarta
println(colores.size) // 3
```

Como ves, el `"rojo"` repetido aparece una sola vez. Igual que con las listas, puedes comprobar la pertenencia con `in` y recorrerlo con `for`:

```kotlin
println("verde" in colores) // true
 
for (color in colores) {
    println(color)
}
```

Como un conjunto no maneja posiciones, **no** puedes acceder a sus elementos por índice (`colores[0]` no es válido). También existe `mutableSetOf()` para conjuntos que puedes modificar con `add` y `remove`; y, al no admitir duplicados, agregar un elemento que ya existe simplemente no tiene efecto.

## Mapas (`Map`)

Un **mapa** (`Map`) guarda pares de **clave** y **valor**: a cada clave le corresponde un valor, y usas la clave para recuperar ese valor. Piensa en una agenda telefónica: buscas por nombre (la clave) y obtienes el número (el valor).

Se crea con `mapOf()`, y cada par se escribe con la palabra `to`:

```kotlin
val edades = mapOf("Ana" to 30, "Diego" to 25)
```

Para obtener el valor de una clave, la pones entre corchetes:

```kotlin
println(edades["Ana"])   // 30
println(edades["Diego"]) // 25
```

Las claves de un mapa son **únicas** (no puede haber dos iguales), pero los valores sí pueden repetirse. Puedes obtener todas las claves con `.keys` y todos los valores con `.values`:

```kotlin
println(edades.keys)   // [Ana, Diego]
println(edades.values) // [30, 25]
```

Y recorrer un mapa con un `for`, obteniendo la clave y el valor de cada par:

```kotlin
for ((nombre, edad) in edades) {
    println("$nombre tiene $edad años")
}
```

> [!NOTE]Nota
> Esa forma de recibir la clave y el valor en dos variables a la vez —`(nombre, edad)`— se llama *desestructuración*. La veremos en detalle más adelante; por ahora, quédate con que es una manera cómoda de recorrer un mapa.

Para mapas que cambian, usa `mutableMapOf()`, que permite agregar pares nuevos o actualizar los existentes:

```kotlin
val stock = mutableMapOf("manzanas" to 5)
stock["peras"] = 8    // agrega una clave nueva
stock["manzanas"] = 3 // actualiza el valor de una clave existente
println(stock)        // {manzanas=3, peras=8}
```

> [!NOTE]Nota
> Si pides una clave que **no** existe en el mapa, obtienes un caso especial relacionado con la *ausencia de valor*, que veremos al estudiar los valores nulos. Por ahora, trabajaremos con claves que sí existen.

## ¿Cuál usar?

Las tres colecciones guardan varios valores, pero cada una resuelve una necesidad distinta:

- Usa una **lista** (`List`) cuando el orden importa o cuando puede haber elementos repetidos (una lista de tareas, los mensajes de un chat).
- Usa un **conjunto** (`Set`) cuando no quieres duplicados y solo te interesa saber si algo pertenece o no (las etiquetas únicas de un artículo).
- Usa un **mapa** (`Map`) cuando necesitas asociar cada elemento con otro dato, una clave que apunta a un valor (un nombre y su edad, un producto y su precio).

Además, recuerda que cada una tiene su versión de **solo lectura** (`listOf`, `setOf`, `mapOf`) y su versión **mutable** (`mutableListOf`, `mutableSetOf`, `mutableMapOf`). Empieza siempre por la de solo lectura y pasa a la mutable solo si de verdad necesitas modificar la colección: es el mismo criterio de preferir `val` sobre `var` que vimos al principio del curso.

## Resumen

En este capítulo conociste las colecciones básicas de Kotlin:

- Las **colecciones** guardan varios valores bajo un mismo nombre.
- Una **lista** (`List`) está ordenada, admite duplicados y accedes a sus elementos por índice (empezando en 0).
- Un **conjunto** (`Set`) no admite duplicados y no se maneja por posición.
- Un **mapa** (`Map`) asocia claves únicas con valores.
- Cada colección tiene una versión de solo lectura y una mutable. Un `val` con una colección mutable no se puede reasignar, pero su contenido sí puede cambiar.
- Puedes recorrer cualquier colección con un `for` y comprobar la pertenencia con `in`.

## Para cerrar

Sin ejecutar el código, **predice** qué imprime y explica por qué:

```kotlin
val lista = mutableListOf("rojo", "verde")
lista.add("rojo")
val conjunto = setOf("rojo", "verde", "rojo")
println(lista.size)
println(conjunto.size)
```

Intenta resolverlo por tu cuenta. Consulta la respuesta solo después de haber escrito tu predicción y tu razonamiento.

??? success "Ver respuesta y razonamiento"
    Imprime `3` y luego `2`.

    - La **lista** admite duplicados: al agregar `"rojo"` por segunda vez, ahora tiene tres elementos (`"rojo"`, `"verde"`, `"rojo"`).
    - El **conjunto** descarta los duplicados al construirse: solo conserva `"rojo"` y `"verde"`.

    **Error común:** creer que agregar a una lista un elemento que ya existe "no hace nada". Eso es cierto en un `Set`, pero no en una `List`. Si quieres evitar duplicados, elige la colección por su comportamiento (conjunto), no confíes en la lista.

En el próximo capítulo daremos un gran salto: aprenderás a **transformar y filtrar** colecciones con operaciones como `map` y `filter`, que hacen el código mucho más expresivo.
