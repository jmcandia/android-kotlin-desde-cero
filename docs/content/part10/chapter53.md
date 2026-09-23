# Capítulo 53: Pantalla de lista: búsqueda, paginación y favoritos

## Introducción

Con el repositorio listo, construimos la primera pantalla real de la aplicación: la **lista de contactos**. Es la pantalla más compleja de las tres, porque combina varias técnicas que estudiaste por separado a lo largo del curso: búsqueda con `debounce`, scroll infinito, actualización mediante deslizamiento (*pull-to-refresh*) y un filtro de favoritos, todo coordinado desde un único `UiState`.

En este capítulo construiremos `ListaContactosUiState`, `ListaContactosViewModel` y `ListaContactosScreen`.

## El estado de la pantalla: `ListaContactosUiState`

A diferencia del `UiState` con `sealed class`/`sealed interface` que usaste en capítulos anteriores (cap. 39, cap. 46), esta pantalla necesita mostrar **varias cosas a la vez**: una lista de contactos, si se está cargando más, si hay un error, y el texto de búsqueda actual. Como anticipaba la nota del capítulo 39, aquí `UiState` se modela como una **`data class`**:

```kotlin
package com.ejemplo.miscontactos.ui.lista

import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.ErrorDatos

data class ListaContactosUiState(
    val contactos: List<Contacto> = emptyList(),
    val busqueda: String = "",
    val favoritos: Set<Int> = emptySet(),
    val soloFavoritos: Boolean = false,
    val cargando: Boolean = false,
    val cargandoMas: Boolean = false,
    val hayMas: Boolean = false,
    val desdeCache: Boolean = false,
    val error: ErrorDatos? = null
) {
    /** Lo que realmente se muestra: todos los contactos cargados o solo los favoritos. */
    val contactosVisibles: List<Contacto>
        get() = if (soloFavoritos) contactos.filter { it.id in favoritos } else contactos
}
```

Fíjate en la propiedad calculada `contactosVisibles`: en lugar de que cada composable repita la lógica del filtro de favoritos, el propio `UiState` decide qué lista mostrar. Es el mismo principio de "la Vista solo dibuja" que viste en el capítulo 27: toda decisión vive en el estado, no en la interfaz.

## El ViewModel: `ListaContactosViewModel`

```kotlin
package com.ejemplo.miscontactos.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.miscontactos.data.ContactosRepository
import com.ejemplo.miscontactos.model.comoErrorDatos
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListaContactosViewModel @Inject constructor(
    private val repository: ContactosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaContactosUiState())
    val uiState: StateFlow<ListaContactosUiState> = _uiState.asStateFlow()

    private var paginaActual = 0
    private var cargaJob: Job? = null

    init {
        viewModelScope.launch {
            repository.favoritos.collect { ids ->
                _uiState.update { it.copy(favoritos = ids) }
            }
        }
        viewModelScope.launch {
            repository.cambios.collect { recargar() }
        }
        recargar()
    }

    // ... (recargar, cargarMas, cambiarBusqueda, alternarSoloFavoritos, alternarFavorito, cargarPagina)
}
```

En el bloque `init` lanzamos **dos coroutines independientes** que se ejecutan durante toda la vida del `ViewModel`, además de disparar la primera carga:

1. La primera se suscribe a `repository.favoritos` y mantiene el `UiState` sincronizado con los favoritos guardados en Room, sin importar qué otra pantalla los haya modificado.
2. La segunda se suscribe a `repository.cambios`: cada vez que se crea, edita o elimina un contacto (en el formulario o en el detalle), esta pantalla se refresca sola llamando a `recargar()`.

> [!NOTE]Nota
> Dos coroutines dentro de un mismo `init` no se bloquean entre sí: `viewModelScope.launch` lanza cada una en su propia tarea. La primera queda "escuchando" `favoritos` indefinidamente, mientras la segunda hace lo mismo con `cambios`, y ambas conviven con la carga inicial.

### Cargar y recargar

```kotlin
/** Vuelve a pedir la primera página (al iniciar, al reintentar o al deslizar para actualizar). */
fun recargar() {
    cargaJob?.cancel()
    cargaJob = viewModelScope.launch { cargarPagina(0) }
}

fun cargarMas() {
    val estado = _uiState.value
    if (estado.cargando || estado.cargandoMas || !estado.hayMas) return
    cargaJob = viewModelScope.launch { cargarPagina(paginaActual + 1) }
}
```

`cargaJob` guarda una referencia al `Job` de la carga en curso (capítulo 23). Antes de lanzar una nueva carga desde `recargar()`, **cancelamos** la anterior: si el usuario desliza para refrescar mientras la lista todavía estaba cargando la página inicial, no queremos que ambas peticiones compitan por escribir el mismo estado.

`cargarMas()` incluye una **guarda** de tres condiciones: no vuelve a pedir otra página si ya hay una carga en curso (`cargando` o `cargandoMas`) o si no quedan más páginas (`!hayMas`). Sin esta comprobación, el scroll infinito podría disparar la misma petición varias veces.

### Búsqueda con `debounce`

```kotlin
/** Cada tecla reinicia la espera: solo se busca cuando el usuario deja de escribir. */
fun cambiarBusqueda(texto: String) {
    _uiState.update { it.copy(busqueda = texto) }
    cargaJob?.cancel()
    cargaJob = viewModelScope.launch {
        delay(ESPERA_BUSQUEDA_MS)
        cargarPagina(0)
    }
}

private companion object {
    const val ESPERA_BUSQUEDA_MS = 400L
}
```

Si lanzáramos una búsqueda a la API por cada letra que el usuario escribe, generaríamos una petición de red por tecla. En cambio, cada llamada a `cambiarBusqueda` **cancela** la coroutine de búsqueda anterior (`cargaJob?.cancel()`) y lanza una nueva que espera 400 milisegundos (`delay`) antes de disparar la petición real. Si el usuario sigue escribiendo antes de que se cumplan esos 400 ms, la espera se cancela y se reinicia. Esta técnica se llama ***debounce***, y es el mismo patrón de cancelación que viste con `Job` en el capítulo 23.

### Favoritos: local e inmediato

```kotlin
fun alternarSoloFavoritos() {
    _uiState.update { it.copy(soloFavoritos = !it.soloFavoritos) }
}

fun alternarFavorito(id: Int) {
    viewModelScope.launch { repository.alternarFavorito(id) }
}
```

`alternarSoloFavoritos` es puramente local: cambia el filtro sin tocar la red ni la base de datos, y la propiedad `contactosVisibles` del `UiState` se encarga de recalcular la lista. `alternarFavorito(id)` delega en el repositorio; como el `UiState` ya está suscrito a `repository.favoritos` desde el `init`, el cambio se refleja automáticamente, sin necesidad de actualizar `_uiState` manualmente aquí.

### Cargar una página

```kotlin
private suspend fun cargarPagina(pagina: Int) {
    _uiState.update {
        if (pagina == 0) it.copy(cargando = true, error = null)
        else it.copy(cargandoMas = true, error = null)
    }

    val busqueda = _uiState.value.busqueda.trim().ifBlank { null }
    repository.obtenerPagina(pagina, busqueda)
        .onSuccess { resultado ->
            paginaActual = resultado.pagina
            _uiState.update { estado ->
                estado.copy(
                    contactos = if (pagina == 0) {
                        resultado.contactos
                    } else {
                        estado.contactos + resultado.contactos
                    },
                    hayMas = resultado.hayMas,
                    desdeCache = resultado.desdeCache,
                    cargando = false,
                    cargandoMas = false
                )
            }
        }
        .onFailure { error ->
            _uiState.update {
                it.copy(cargando = false, cargandoMas = false, error = error.comoErrorDatos())
            }
        }
}
```

Si `pagina == 0`, **reemplazamos** la lista completa (una nueva búsqueda o un refresco). Si es una página siguiente, **concatenamos** los nuevos contactos a los que ya había (`estado.contactos + resultado.contactos`), preservando el orden. `cargando` distingue la carga inicial (que suele mostrar un indicador a pantalla completa) de `cargandoMas` (un indicador pequeño al final de la lista).

## La pantalla: `ListaContactosScreen`

Siguiendo el patrón de separar la versión "con estado" de la versión "sin estado" que ya usaste en tutoriales anteriores, dividimos el composable en dos:

```kotlin
/** Versión con estado: obtiene el ViewModel y conecta sus funciones con la pantalla. */
@Composable
fun ListaContactosScreen(
    onVerContacto: (Int) -> Unit,
    onNuevoContacto: () -> Unit,
    viewModel: ListaContactosViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ListaContactosContent(
        uiState = uiState,
        onBusquedaChange = viewModel::cambiarBusqueda,
        onAlternarSoloFavoritos = viewModel::alternarSoloFavoritos,
        onAlternarFavorito = viewModel::alternarFavorito,
        onRecargar = viewModel::recargar,
        onCargarMas = viewModel::cargarMas,
        onVerContacto = onVerContacto,
        onNuevoContacto = onNuevoContacto
    )
}
```

`ListaContactosContent` es la versión sin estado, la que realmente dibuja la pantalla y la que se puede previsualizar y probar de forma aislada:

```kotlin
@Composable
fun ListaContactosContent(
    uiState: ListaContactosUiState,
    onBusquedaChange: (String) -> Unit,
    onAlternarSoloFavoritos: () -> Unit,
    onAlternarFavorito: (Int) -> Unit,
    onRecargar: () -> Unit,
    onCargarMas: () -> Unit,
    onVerContacto: (Int) -> Unit,
    onNuevoContacto: () -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text(stringResource(R.string.titulo_lista)) }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onNuevoContacto) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.nuevo_contacto))
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BarraBusqueda(
                busqueda = uiState.busqueda,
                soloFavoritos = uiState.soloFavoritos,
                onBusquedaChange = onBusquedaChange,
                onAlternarSoloFavoritos = onAlternarSoloFavoritos
            )

            if (uiState.desdeCache) {
                AvisoDatosGuardados()
            }

            val error = uiState.error
            when {
                uiState.cargando && uiState.contactos.isEmpty() -> EstadoCargando()
                error != null && uiState.contactos.isEmpty() ->
                    EstadoError(error = error, onReintentar = onRecargar)
                else -> PullToRefreshBox(
                    isRefreshing = uiState.cargando,
                    onRefresh = onRecargar,
                    modifier = Modifier.fillMaxSize()
                ) {
                    ListaContactos(
                        uiState = uiState,
                        onAlternarFavorito = onAlternarFavorito,
                        onCargarMas = onCargarMas,
                        onVerContacto = onVerContacto
                    )
                }
            }
        }
    }
}
```

Observa el `when` que decide qué dibujar:
- Si está cargando **y** todavía no hay ningún contacto (la primera carga), muestra `EstadoCargando()` a pantalla completa.
- Si hay un error **y** la lista está vacía, muestra `EstadoError` con un botón de reintentar.
- En cualquier otro caso —incluida una recarga con datos ya visibles— envuelve la lista en `PullToRefreshBox`, que dibuja el indicador circular de "deslizar para actualizar" sincronizado con `uiState.cargando`.

`AvisoDatosGuardados()` es una franja de color que se muestra únicamente cuando `desdeCache` es `true`, avisando al usuario de que estos contactos no son la versión más reciente del servidor:

```kotlin
@Composable
private fun AvisoDatosGuardados() {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = stringResource(R.string.aviso_datos_guardados),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
    }
}
```

## Scroll infinito con `LazyColumn`

```kotlin
@Composable
private fun ListaContactos(
    uiState: ListaContactosUiState,
    onAlternarFavorito: (Int) -> Unit,
    onCargarMas: () -> Unit,
    onVerContacto: (Int) -> Unit
) {
    val contactos = uiState.contactosVisibles

    if (contactos.isEmpty() && !uiState.cargando) {
        val mensaje = when {
            uiState.soloFavoritos -> stringResource(R.string.favoritos_vacios)
            uiState.busqueda.isNotBlank() ->
                stringResource(R.string.busqueda_sin_resultados, uiState.busqueda)
            else -> stringResource(R.string.lista_vacia)
        }
        // LazyColumn para que "deslizar para actualizar" funcione también con la lista vacía.
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item { EstadoVacio(mensaje = mensaje) }
        }
        return
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(contactos, key = { it.id }) { contacto ->
            ContactoItem(
                contacto = contacto,
                esFavorito = contacto.id in uiState.favoritos,
                onAlternarFavorito = { onAlternarFavorito(contacto.id) },
                onClick = { onVerContacto(contacto.id) }
            )
            HorizontalDivider()
        }

        // Al llegar al final de la lista, se pide la página siguiente.
        if (uiState.hayMas && !uiState.soloFavoritos) {
            item {
                LaunchedEffect(contactos.size) { onCargarMas() }
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}
```

El truco del scroll infinito está en este fragmento:

```kotlin
if (uiState.hayMas && !uiState.soloFavoritos) {
    item {
        LaunchedEffect(contactos.size) { onCargarMas() }
        // ...un indicador de carga...
    }
}
```

Cuando quedan más páginas por cargar, agregamos un último elemento a la `LazyColumn` que contiene un `LaunchedEffect(contactos.size)` (capítulo 40). Como Compose solo dibuja los elementos visibles de una lista perezosa, este ítem **entra en composición justo cuando el usuario llega al final** de lo que ya está cargado, y en ese momento su `LaunchedEffect` se dispara y llama a `onCargarMas()`. La clave `contactos.size` asegura que el efecto se relance cada vez que llega una nueva página (el tamaño de la lista cambia), y no se repita mientras el tamaño se mantiene igual.

También nota el `soloFavoritos` en la condición: cuando el filtro de favoritos está activo, no tiene sentido seguir pidiendo más páginas al servidor, porque el filtrado ocurre sobre los contactos ya cargados.

## El ítem de la lista: `ContactoItem`

```kotlin
@Composable
fun ContactoItem(
    contacto: Contacto,
    esFavorito: Boolean,
    onAlternarFavorito: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ListItem(
        headlineContent = { Text(contacto.nombreCompleto) },
        supportingContent = { Text(contacto.email) },
        leadingContent = { AvatarContacto(contacto = contacto) },
        trailingContent = {
            IconToggleButton(checked = esFavorito, onCheckedChange = { onAlternarFavorito() }) {
                Icon(
                    imageVector = if (esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(
                        if (esFavorito) R.string.quitar_favorito else R.string.marcar_favorito
                    ),
                    tint = if (esFavorito) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        modifier = modifier.clickable(onClick = onClick)
    )
}
```

`ListItem` de Material 3 organiza el contenido en tres zonas (principal, secundaria y final), y `IconToggleButton` cambia de ícono y color según `esFavorito`, ofreciendo una descripción de accesibilidad distinta en cada estado (`marcar_favorito` / `quitar_favorito`).

## Resumen

- `ListaContactosUiState` es una `data class` (no una `sealed class`) porque muestra varias cosas a la vez; centraliza la decisión de qué mostrar en la propiedad calculada `contactosVisibles`.
- El `ViewModel` se suscribe en el `init` a `repository.favoritos` y `repository.cambios`, manteniéndose sincronizado con el resto de la app.
- La búsqueda usa ***debounce***: cada tecla cancela la espera anterior y lanza una nueva de 400 ms antes de pedir datos.
- El scroll infinito se logra con un ítem final en la `LazyColumn` cuyo `LaunchedEffect(contactos.size)` dispara `cargarMas()` al hacerse visible.
- `PullToRefreshBox` conecta el gesto de deslizar hacia abajo con `uiState.cargando` y `onRecargar`.
- Un aviso visual (`desdeCache`) le informa al usuario cuando está viendo datos guardados localmente, coherente con la estrategia *offline-first* del capítulo 52.

En el próximo capítulo construiremos la pantalla de detalle, con el patrón de "evento como estado" para eliminar un contacto de forma segura.
