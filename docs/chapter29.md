# Capítulo 29: Layouts y listas: `Column`, `Row`, `LazyColumn`

## Introducción

Hasta ahora has mostrado composables de a uno. Pero una pantalla real combina **muchos** elementos: textos, imágenes, botones, unos debajo de otros o en fila. En este capítulo aprenderás a **organizarlos** con los layouts de Compose —`Column`, `Row` y `Box`—, a controlar cómo se distribuyen y alinean, y a mostrar **listas** de forma eficiente con `LazyColumn`, que será la base de la lista de Pokémon de la aplicación.

## El problema: los elementos se superponen

Si colocas dos composables juntos sin más, Compose los dibuja en el **mismo lugar**, uno encima del otro:

```kotlin
@Composable
fun Pantalla() {
    Text("Primero")
    Text("Segundo") // ¡se dibuja encima del anterior!
}
```

Para arreglarlo, necesitas un **layout**: un composable cuyo trabajo es **organizar** a sus hijos. Compose ofrece tres básicos, que resuelven las tres formas fundamentales de disponer elementos:

![Layout](assets/images/chapter29/layout-column-row-box.svg)

## `Column`: en vertical

Un `Column` organiza a sus hijos **en vertical**, uno debajo del otro:

```kotlin
Column {
    Text("Primero")
    Text("Segundo")
    Text("Tercero")
}
```

Ahora los tres textos aparecen apilados de arriba abajo, en el orden en que los escribiste.

## `Row`: en horizontal

Un `Row` organiza a sus hijos **en horizontal**, uno al lado del otro:

```kotlin
Row {
    Text("Izquierda")
    Text("Centro")
    Text("Derecha")
}
```

Es idéntico a `Column`, pero en el eje horizontal.

## `Box`: superponer elementos

Un `Box` **apila** a sus hijos, uno **encima** de otro. Es útil para superponer cosas: un texto sobre una imagen, una insignia sobre un ícono, etcétera.

```kotlin
Box {
    Text("Fondo")
    Text("Encima") // se dibuja sobre el anterior
}
```

Combinando estos tres layouts (y anidándolos unos dentro de otros) puedes construir prácticamente cualquier pantalla.

## Distribución y alineación

Dentro de un `Column` o un `Row`, a menudo querrás controlar **cómo se reparten** los hijos y **cómo se alinean**. Para eso, estos layouts reciben dos parámetros. La clave es distinguir sus dos ejes:

- En un `Column`, el eje principal es **vertical**. Controlas la distribución vertical con `verticalArrangement` y la alineación horizontal con `horizontalAlignment`.
- En un `Row`, el eje principal es **horizontal**. Controlas la distribución horizontal con `horizontalArrangement` y la alineación vertical con `verticalAlignment`.

Por ejemplo, un `Column` que separa sus hijos con espacio y los centra horizontalmente:

```kotlin
Column(
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
) {
    Text("Primero")
    Text("Segundo")
}
```

Algunos valores útiles de `Arrangement` son `spacedBy(...)` (un espacio fijo entre elementos), `SpaceBetween` (reparte el espacio sobrante entre ellos) y `Center` (los agrupa al centro). Y de `Alignment`, `Start`, `CenterHorizontally` y `End` (o `Top`, `CenterVertically` y `Bottom` en un `Row`).

## El modificador `weight`

En el capítulo de fundamentos mencionamos que hay modificadores que solo funcionan dentro de ciertos layouts. `weight` es el más importante: dentro de un `Row` o un `Column`, reparte el **espacio disponible** entre los hijos de forma proporcional.

```kotlin
Row {
    Text("Izquierda", modifier = Modifier.weight(1f))
    Text("Derecha", modifier = Modifier.weight(1f))
}
```

Aquí ambos textos reciben el mismo peso (`1f`), así que se reparten el ancho **a la mitad**. Si a uno le dieras `weight(2f)` y al otro `weight(1f)`, el primero ocuparía el doble de espacio que el segundo.

## `LazyColumn`: listas eficientes

Un `Column` dibuja **todos** sus hijos de una vez. Eso está bien para unos pocos elementos, pero ¿y si tienes una lista de cientos de Pokémon? Dibujarlos todos a la vez sería lento y desperdiciaría memoria, sobre todo porque la mayoría ni siquiera caben en la pantalla.

Para eso está el **`LazyColumn`**: una columna con desplazamiento (*scroll*) que solo compone los elementos **visibles** en cada momento, y los va reutilizando a medida que te desplazas. Así puede mostrar listas enormes sin problemas.

En vez de escribir cada hijo a mano, le pasas la lista con la función `items`:

```kotlin
val pokemones = listOf("Bulbasaur", "Charmander", "Squirtle")

LazyColumn {
    items(pokemones) { nombre ->
        Text(nombre)
    }
}
```

`items(pokemones)` recorre la lista y, por cada elemento, ejecuta la lambda que describe cómo mostrarlo (aquí, un `Text` con su nombre). El desplazamiento funciona automáticamente. También existe `LazyRow`, su equivalente horizontal.

> [!NOTE]
> Si vienes del desarrollo Android tradicional, `LazyColumn` cumple el papel del antiguo `RecyclerView`, pero con muchísimo menos código: no necesitas adaptadores ni *view holders*.

Este es, precisamente, el componente con el que mostraremos la lista de Pokémon que llegue de la PokeAPI: una `LazyColumn` con un `items` que recorre los resultados.

## Resumen

En este capítulo aprendiste a organizar la interfaz:

- Sin un layout, los composables se **superponen**. Los tres layouts básicos son `Column` (vertical), `Row` (horizontal) y `Box` (apilados).
- `Column` y `Row` controlan la **distribución** (`Arrangement`) en su eje principal y la **alineación** (`Alignment`) en el eje cruzado.
- El modificador **`weight`**, dentro de un `Row` o `Column`, reparte el espacio disponible de forma proporcional.
- **`LazyColumn`** muestra listas con desplazamiento de forma eficiente, componiendo solo los elementos visibles; se llena con la función `items`. Su versión horizontal es `LazyRow`.

En el próximo capítulo daremos color y estilo a la interfaz con **Material 3**: componentes listos para usar, temas, tipografía y esquemas de color.
