# Ejercicios: Parte VII

Estos ejercicios te permitirán aplicar lo aprendido en la Parte VII: componentes de Jetpack Compose, `Modifier` y layouts, estado con `remember`/`mutableStateOf`, `Scaffold`, `LazyColumn`, formularios y navegación. Como en las demás partes, el repaso acumulativo combina conceptos anteriores.

---

## Repaso acumulativo (Partes I–VII): el buscador de tareas en Compose

Este reto reúne tres capas de conocimiento: **colecciones** y `String` (Parte III), `data class` y lambdas (Parte IV) y **estado, layouts y listas en Compose** (Parte VII). Intenta resolverlo antes de mirar la solución.

### Enunciado

Crea una pantalla que muestre una lista de tareas con un campo de búsqueda. Al escribir, la lista se filtra al instante; si no hay coincidencias, se muestra un mensaje. La pantalla debe poder previsualizarse en Android Studio.

### Requisitos

- Modela cada tarea con una `data class Tarea(val titulo: String, val hecha: Boolean)`.
- La pantalla es un composable `BuscadorTareas(tareas: List<Tarea>)` que **recibe** las tareas (no las crea dentro).
- Guarda el texto de búsqueda con `remember { mutableStateOf("") }` y conéctalo a un `OutlinedTextField` con `value`/`onValueChange`.
- Filtra con `filter { it.titulo.contains(texto, ignoreCase = true) }`.
- Muestra los resultados en una `LazyColumn` con `items(...)`; si la lista filtrada está vacía, muestra un `Text("Sin resultados")`.
- Organiza la pantalla en una `Column` con espaciado y añade un `@Preview` que llame a `BuscadorTareas` con datos de ejemplo.

### Ejemplo de resultado (descripción)

Un campo "Buscar tarea" arriba; debajo, la lista de tareas que coinciden. Al escribir "café" solo queda "Comprar café"; al escribir "zzz" aparece el texto "Sin resultados".

### Pistas

??? tip "Pista 1 — Qué concepto usar"
    - **Parte IV**: una `data class` da `toString` y `equals` gratis; el filtrado recibe una *lambda* `(Tarea) -> Boolean`.
    - **Parte VII (estado)**: `var texto by remember { mutableStateOf("") }` conserva el valor entre recomposiciones; sin `remember`, el texto se perdería en cada recomposición.
    - **Parte VII (listas)**: `LazyColumn` es la versión eficiente de una lista larga; dentro se usa `items(lista) { elemento -> ... }`. Un `items` no debe ir dentro de una `Column` con `verticalScroll`.

??? tip "Pista 2 — Estructura del composable (fragmento parcial)"
    ```kotlin
    @Composable
    fun BuscadorTareas(tareas: List<Tarea>) {
        var texto by remember { mutableStateOf("") }
        val filtradas = tareas.filter { /* coincide con `texto` */ }

        Column(/* ... */) {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Buscar tarea") }
            )
            // si `filtradas` está vacía -> "Sin resultados"
            // si no -> LazyColumn con items(filtradas)
        }
    }
    ```

??? success "Ver solución comentada"
    ```kotlin
    import androidx.compose.foundation.layout.*
    import androidx.compose.foundation.lazy.LazyColumn
    import androidx.compose.foundation.lazy.items
    import androidx.compose.material3.*
    import androidx.compose.runtime.*
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp

    data class Tarea(val titulo: String, val hecha: Boolean)

    @Composable
    fun BuscadorTareas(tareas: List<Tarea>) {
        var texto by remember { mutableStateOf("") }
        val filtradas = tareas.filter {
            it.titulo.contains(texto, ignoreCase = true)
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            OutlinedTextField(
                value = texto,
                onValueChange = { texto = it },
                label = { Text("Buscar tarea") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            if (filtradas.isEmpty()) {
                Text("Sin resultados")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtradas) { tarea ->
                        Text(
                            text = if (tarea.hecha) "✔ ${tarea.titulo}" else tarea.titulo,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun BuscadorTareasPreview() {
        MaterialTheme {
            BuscadorTareas(
                tareas = listOf(
                    Tarea("Comprar café", false),
                    Tarea("Estudiar Compose", true),
                    Tarea("Llamar a Ana", false)
                )
            )
        }
    }
    ```

    **Por qué funciona.** El estado vive en el composable con `remember` + `mutableStateOf`; cuando cambia `texto`, Compose **recompone** `BuscadorTareas` y recalcula `filtradas`. La `LazyColumn` solo compone las filas visibles, y el `@Preview` permite ver el resultado sin ejecutar la app. El composable **recibe** las tareas en lugar de crearlas: eso lo hace reutilizable y fácil de previsualizar (state hoisting).

    **Errores comunes.**
    - Olvidar `mutableStateOf`: el `TextField` no se actualizaría al escribir.
    - Escribir `remember` sin `mutableStateOf` (o al revés): el valor no sería observable o se reiniciaría.
    - Poner `LazyColumn` dentro de una `Column` con `Modifier.verticalScroll`: Compose lanza una excepción por anidar dos desplazamientos verticales no acotados.
    - Filtrar creando una nueva lista en cada recomposición con operaciones costosas: para listas grandes, considera `derivedStateOf` o mover el filtrado al `ViewModel` (Parte VIII).

    **Para ir más allá.** Añade un `Scaffold` con `topBar` y mueve el `OutlinedTextField` a la barra; después marca tareas como hechas al pulsarlas añadiendo un parámetro `onToggle: (Tarea) -> Unit` y elevando el estado (capítulo 34).

---

## Hito: comprueba tu avance (Parte VII)

Marca cada afirmación que puedas hacer **sin mirar los apuntes**. Si alguna te resulta difícil, repasa el capítulo indicado.

- [ ] Escribo un composable que recibe parámetros y lo previsualizo con `@Preview`. *(cap. 30)*
- [ ] Compongo pantallas con `Column`, `Row` y `Box` ajustando el aspecto con `Modifier`. *(cap. 31)*
- [ ] Guardo estado que sobrevive a la recomposición con `remember`/`mutableStateOf` y lo elevo cuando conviene. *(cap. 34)*
- [ ] Uso `Scaffold` y `LazyColumn` para estructurar pantallas y listas largas. *(cap. 35)*
- [ ] Navego entre pantallas con Navigation Compose. *(caps. 37–39)*
- [ ] Aplico Material 3 y recursos (cadenas, colores, íconos) de forma coherente. *(caps. 32–33, 40)*

Si puedes con todo, estás listo para llevar este estado a un `ViewModel` y conectar la interfaz con una arquitectura MVVM en la Parte VIII.
