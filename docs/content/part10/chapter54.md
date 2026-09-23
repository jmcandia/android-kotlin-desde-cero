# Capítulo 54: Pantalla de detalle: eliminación segura con evento como estado

## Introducción

La pantalla de detalle muestra la información completa de un contacto y permite marcarlo como favorito, editarlo o eliminarlo. Es más simple que la lista en cuanto a datos que maneja, pero introduce un problema nuevo: **eliminar un contacto es una acción destructiva** que necesita confirmación del usuario y, una vez completada, debe **navegar hacia atrás automáticamente**. Resolveremos esto con el patrón "evento como estado" del capítulo 40.

## El estado de la pantalla: `DetalleContactoUiState`

A diferencia de la lista, esta pantalla sí puede modelarse con una `sealed interface`, porque sus posibilidades son mutuamente excluyentes: o todavía no hay datos (cargando), o falló la carga (error), o hay un contacto que mostrar (contenido):

```kotlin
package com.ejemplo.miscontactos.ui.detalle

import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.ErrorDatos

sealed interface DetalleContactoUiState {
    data object Cargando : DetalleContactoUiState

    data class Error(val error: ErrorDatos) : DetalleContactoUiState

    data class Contenido(
        val contacto: Contacto,
        val esFavorito: Boolean = false,
        val eliminando: Boolean = false,
        val eliminado: Boolean = false,
        val errorAccion: ErrorDatos? = null
    ) : DetalleContactoUiState
}
```

Dentro de `Contenido` sí usamos una `data class` con varios campos, porque una vez que hay un contacto cargado, la pantalla vuelve a necesitar mostrar varias cosas simultáneamente: si se está favoriteando, si se está eliminando, si la eliminación ya terminó, o si una acción (favorito o borrado) falló.

Fíjate especialmente en **`eliminado: Boolean`**: no es un dato del contacto, es un **evento**. Cuando pasa a `true`, la pantalla debe reaccionar navegando hacia atrás, tal como estudiaste en el capítulo 40 con el ejemplo de guardar y volver.

## El ViewModel: `DetalleContactoViewModel`

```kotlin
package com.ejemplo.miscontactos.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ejemplo.miscontactos.data.ContactosRepository
import com.ejemplo.miscontactos.model.comoErrorDatos
import com.ejemplo.miscontactos.ui.navigation.DetalleContactoRuta
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class DetalleContactoViewModel @Inject constructor(
    private val repository: ContactosRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val id: Int = savedStateHandle.toRoute<DetalleContactoRuta>().id

    private val _uiState = MutableStateFlow<DetalleContactoUiState>(DetalleContactoUiState.Cargando)
    val uiState: StateFlow<DetalleContactoUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.favoritos.collect { favoritos ->
                actualizarContenido { it.copy(esFavorito = id in favoritos) }
            }
        }
        viewModelScope.launch {
            repository.cambios.collect { cargar() }
        }
        cargar()
    }

    // ... (cargar, alternarFavorito, eliminar, errorAccionMostrado, actualizarContenido)
}
```

El `id` del contacto no llega como parámetro de función: se lee del `SavedStateHandle` mediante `savedStateHandle.toRoute<DetalleContactoRuta>().id`, la forma de leer argumentos de navegación tipados que veremos en detalle en el capítulo 56. Como en la pantalla de lista, el `init` se suscribe tanto a `favoritos` como a `cambios`, manteniendo el detalle sincronizado si el contacto se edita desde otra pantalla.

### Cargar el contacto

```kotlin
private fun cargar() {
    viewModelScope.launch {
        _uiState.update { DetalleContactoUiState.Cargando }
        repository.obtenerContacto(id)
            .onSuccess { contacto ->
                _uiState.update {
                    DetalleContactoUiState.Contenido(contacto = contacto)
                }
            }
            .onFailure { error ->
                _uiState.update { DetalleContactoUiState.Error(error.comoErrorDatos()) }
            }
    }
}
```

Nota que `cargar()` siempre **reemplaza** el estado desde cero, incluso si ya había un `Contenido` visible: al recargar tras un cambio externo (por ejemplo, se editó el contacto desde el formulario), es correcto volver a mostrar `Cargando` brevemente y luego el contenido actualizado.

### Alternar favorito y eliminar

```kotlin
fun alternarFavorito() {
    viewModelScope.launch { repository.alternarFavorito(id) }
}

fun eliminar() {
    viewModelScope.launch {
        actualizarContenido { it.copy(eliminando = true, errorAccion = null) }
        repository.eliminar(id)
            .onSuccess {
                actualizarContenido { it.copy(eliminando = false, eliminado = true) }
            }
            .onFailure { error ->
                actualizarContenido {
                    it.copy(eliminando = false, errorAccion = error.comoErrorDatos())
                }
            }
    }
}

fun errorAccionMostrado() {
    actualizarContenido { it.copy(errorAccion = null) }
}
```

`eliminar()` sigue el mismo patrón de tres pasos que ya usaste en los capítulos 39 y 46: marca `eliminando = true` antes de la llamada, y según el resultado, marca `eliminado = true` (el evento que disparará la navegación) o guarda el error en `errorAccion` para mostrarlo en un snackbar. `errorAccionMostrado()` lo limpia después de mostrarlo, evitando que se repita en una recomposición.

### El ayudante `actualizarContenido`

```kotlin
private inline fun actualizarContenido(
    transformar: (DetalleContactoUiState.Contenido) -> DetalleContactoUiState.Contenido
) {
    _uiState.update { estado ->
        if (estado is DetalleContactoUiState.Contenido) transformar(estado) else estado
    }
}
```

Como varias funciones (`alternarFavorito`, `eliminar`, la suscripción a `favoritos`) solo tienen sentido cuando el estado ya es `Contenido`, centralizamos esa comprobación en `actualizarContenido`: si el estado actual es `Cargando` o `Error`, la actualización simplemente se ignora. Esto evita repetir un `if (estado is Contenido)` en cada función.

## La pantalla: `DetalleContactoScreen`

```kotlin
@Composable
fun DetalleContactoScreen(
    onEditar: (Int) -> Unit,
    onVolver: () -> Unit,
    viewModel: DetalleContactoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val eliminado = (uiState as? DetalleContactoUiState.Contenido)?.eliminado ?: false
    LaunchedEffect(eliminado) {
        if (eliminado) onVolver()
    }

    DetalleContactoContent(
        uiState = uiState,
        onAlternarFavorito = viewModel::alternarFavorito,
        onEliminar = viewModel::eliminar,
        onErrorAccionMostrado = viewModel::errorAccionMostrado,
        onEditar = onEditar,
        onVolver = onVolver
    )
}
```

Este `LaunchedEffect(eliminado)` es exactamente el patrón "evento como estado" del capítulo 40: en lugar de que el `ViewModel` llame directamente a una función de navegación (que no debería conocer), expone un booleano en el `UiState`, y es la Screen quien observa ese booleano y decide navegar. Como la clave del efecto es `eliminado`, solo se ejecuta cuando ese valor cambia de `false` a `true`, nunca en cada recomposición.

### Confirmación con `AlertDialog`

```kotlin
@Composable
fun DetalleContactoContent(
    uiState: DetalleContactoUiState,
    onAlternarFavorito: () -> Unit,
    onEliminar: () -> Unit,
    onErrorAccionMostrado: () -> Unit,
    onEditar: (Int) -> Unit,
    onVolver: () -> Unit
) {
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val contenido = uiState as? DetalleContactoUiState.Contenido

    val errorAccion = contenido?.errorAccion
    LaunchedEffect(errorAccion) {
        if (errorAccion != null) {
            snackbarHostState.showSnackbar(mensajeDe(errorAccion))
            onErrorAccionMostrado()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_detalle)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    if (contenido != null) {
                        IconToggleButton(checked = contenido.esFavorito, onCheckedChange = { onAlternarFavorito() }) {
                            Icon(
                                imageVector = if (contenido.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = null
                            )
                        }
                        IconButton(onClick = { onEditar(contenido.contacto.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.editar_contacto))
                        }
                        IconButton(onClick = { confirmarEliminar = true }) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.eliminar_contacto))
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (uiState) {
                is DetalleContactoUiState.Cargando -> EstadoCargando()
                is DetalleContactoUiState.Error -> EstadoError(error = uiState.error, onReintentar = null)
                is DetalleContactoUiState.Contenido -> FichaContacto(contacto = uiState.contacto)
            }
        }
    }

    if (confirmarEliminar) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text(stringResource(R.string.confirmar_eliminar_titulo)) },
            text = { Text(stringResource(R.string.confirmar_eliminar_mensaje)) },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar()
                }) { Text(stringResource(R.string.eliminar_contacto)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}
```

Los botones de la barra superior (favorito, editar, eliminar) solo se dibujan **cuando `contenido != null`**: no tiene sentido ofrecer acciones sobre un contacto que todavía se está cargando o que falló al cargar.

El ícono de eliminar no borra directamente: solo cambia `confirmarEliminar` a `true`, lo que hace aparecer un `AlertDialog` de Material 3. Únicamente si el usuario toca el botón de confirmación dentro del diálogo se llama a `onEliminar()`. `confirmarEliminar` vive en `rememberSaveable` (capítulo 32) para sobrevivir a un cambio de configuración, como la rotación de pantalla, sin volver a preguntar innecesariamente ni perder la confirmación en curso.

El segundo `LaunchedEffect`, con clave `errorAccion`, muestra un snackbar cuando `alternarFavorito()` o `eliminar()` fallan, y llama a `onErrorAccionMostrado()` para limpiar el error una vez mostrado — el mismo patrón de "consumir el evento" que ya usaste en el capítulo 40.

### La ficha del contacto

```kotlin
@Composable
private fun FichaContacto(contacto: Contacto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarContacto(contacto = contacto, modifier = Modifier.size(96.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(contacto.nombreCompleto, style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(24.dp))
        DatoContacto(icono = Icons.Default.Email, valor = contacto.email)
        DatoContacto(icono = Icons.Default.Phone, valor = contacto.telefono)
        DatoContacto(icono = Icons.Default.Home, valor = contacto.direccion)
        DatoContacto(icono = Icons.Default.LocationCity, valor = contacto.ciudad)
    }
}

@Composable
private fun DatoContacto(icono: ImageVector, valor: String?, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Icon(icono, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.width(12.dp))
        Text(valor ?: stringResource(R.string.sin_informacion))
    }
}
```

`DatoContacto` recibe un `valor: String?` porque, como recordarás del modelo de dominio (capítulo 48), `telefono`, `direccion` y `ciudad` son opcionales: cuando son `null`, se muestra el texto `sin_informacion` en lugar de dejar la fila vacía o esconderla, dándole al usuario una señal consistente de "este dato no se cargó" en vez de un hueco silencioso.

## Resumen

- `DetalleContactoUiState` vuelve a ser una `sealed interface` (`Cargando` / `Error` / `Contenido`) porque sus tres posibilidades son mutuamente excluyentes, pero `Contenido` es una `data class` porque, una vez con datos, hay varias cosas que coexisten (favorito, eliminando, eliminado, error de acción).
- El `id` del contacto se obtiene con `savedStateHandle.toRoute<DetalleContactoRuta>().id`, el mecanismo de argumentos tipados que se explica a fondo en el capítulo 56.
- `eliminar()` es un ejemplo más del patrón "evento como estado" (capítulo 40): el `ViewModel` no navega; expone `eliminado = true` en el `UiState`, y un `LaunchedEffect(eliminado)` en la Screen decide volver atrás.
- La eliminación se confirma con un `AlertDialog` antes de llamar a `onEliminar()`, y su estado de visibilidad se guarda con `rememberSaveable` para sobrevivir a la rotación.
- El ayudante privado `actualizarContenido` evita repetir la comprobación `is Contenido` en cada función del `ViewModel`.

En el próximo capítulo construiremos la pantalla de formulario, la más exigente en validación: campos obligatorios, formato de correo, y errores que pueden venir tanto del cliente como del servidor.
