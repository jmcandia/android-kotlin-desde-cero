# Ejercicios: Parte VIII

Estos ejercicios repasan la Parte VIII: el `ViewModel` y el `UiState`, los efectos puntuales, el repositorio y la inyección de dependencias. Antes de pasar a la Parte IX, resuélvelos y comprueba el hito final.

---

## Ejercicio 1: ¿Dónde va cada responsabilidad?

### Descripción

Una pantalla muestra una lista de facturas y un botón "Recargar". Al pulsarlo, la app pide las facturas al servidor; mientras tanto muestra un indicador de progreso y, si algo falla, un mensaje de error.

Clasifica cada una de estas responsabilidades en **interfaz (Compose)**, **`ViewModel`** o **repositorio**:

1. Dibujar la lista y el indicador de progreso.
2. Decidir que el estado pasa de "cargando" a "éxito".
3. Hacer la petición HTTP a la API.
4. Traducir el toque en "Recargar" a una llamada a una función del `ViewModel`.
5. Exponer el estado de la pantalla como un `StateFlow` de solo lectura.

??? tip "Pista"
    Pregúntate qué trabajo hace cada punto: mostrar (interfaz), poseer y decidir el estado de presentación (`ViewModel`) u obtener datos (repositorio). El botón *reacciona* en la interfaz, pero delega la decisión en el `ViewModel`.

??? success "Ver solución comentada"
    1. **Interfaz.** Compose solo describe *qué* mostrar según el estado que recibe.
    2. **`ViewModel`.** Es el dueño del estado de presentación (`UiState`): decide cuándo está cargando y cuándo ha terminado.
    3. **Repositorio.** El acceso a datos (red, caché) vive en la capa de datos, no en la interfaz ni en el `ViewModel`.
    4. **Interfaz → `ViewModel`.** El botón invoca una función del `ViewModel` (un *evento*); el `ViewModel` reacciona.
    5. **`ViewModel`.** Expone su estado como `StateFlow` (de solo lectura) para que la interfaz lo observe.

    **Por qué funciona.** Este reparto es el flujo unidireccional de MVVM: la interfaz **emite eventos hacia arriba** y el estado **baja hacia abajo**. Si la interfaz decidiera estados o hablara directamente con la red, cambiaríamos el problema por otro peor: nadie podría probarla sin emulador.

---

## Ejercicio 2: El estado que se escapa del `ViewModel`

### Descripción

Observa este `ViewModel`. Tiene dos problemas de diseño: encuentra ambos y corrígelos.

```kotlin
class FacturasViewModel(private val repo: FacturasRepository) : ViewModel() {
    val uiState = MutableStateFlow(FacturasUiState())
    var contador = 0

    fun recargar() {
        uiState.value = uiState.value.copy(cargando = true)
        viewModelScope.launch {
            val resultado = repo.obtener()
            uiState.value = FacturasUiState(facturas = resultado)
        }
    }
}
```

??? tip "Pista"
    Busca dos olores: el estado que la interfaz podría modificar directamente (¿el `MutableStateFlow` es público?) y una actualización que sobrescribe el valor anterior en lugar de derivarlo de él (¿qué pasa si dos corrutinas actualizan casi a la vez?).

??? success "Ver solución y razonamiento"
    **Problema 1 — el estado mutable se expone como público.** Cualquiera que reciba el `ViewModel` puede escribir `viewModel.uiState.value = ...` y saltarse la lógica. Solución: guarda un `MutableStateFlow` **privado** y expón un `StateFlow` de solo lectura:

    ```kotlin
    private val _uiState = MutableStateFlow(FacturasUiState())
    val uiState: StateFlow<FacturasUiState> = _uiState.asStateFlow()
    ```

    **Problema 2 — se asigna el valor directamente en vez de derivarlo del anterior.** `_uiState.value = uiState.value.copy(...)` puede perder cambios si dos corrutinas actualizan a la vez. Solución: usar `update { }`, que es atómico:

    ```kotlin
    _uiState.update { it.copy(cargando = true) }
    ```

    (El `var contador` público es un tercer olor: el estado que la interfaz observa debe viajar en el `UiState`, no en propiedades sueltas.)

    **Por qué funciona.** Centralizar el estado en un `StateFlow` privado, actualizado con `update { }`, garantiza que solo la lógica del `ViewModel` lo modifique y que dos actualizaciones no se pisen. Este es el patrón que usan los tutoriales y el proyecto final.

---

## Hito: comprueba tu avance (Parte VIII)

Marca cada afirmación que puedas hacer **sin mirar los apuntes**. Si alguna te resulta difícil, repasa el capítulo indicado.

- [ ] Gestiono el estado de una pantalla con un `ViewModel` y un `UiState`. *(cap. 42)*
- [ ] Expongo el estado como `StateFlow` de solo lectura y lo actualizo con `update { }`. *(cap. 42)*
- [ ] Disparo efectos puntuales (Snackbar, cargar una vez) con `LaunchedEffect` sin repetirlos. *(cap. 43)*
- [ ] Separo el acceso a datos en un repositorio detrás de una interfaz. *(cap. 44)*
- [ ] Conecto las dependencias (a mano o con Hilt) y puedo probarlas con dobles de prueba. *(cap. 45)*

Si puedes con todo, tienes la arquitectura lista para conectar la app a datos reales en la Parte IX.
