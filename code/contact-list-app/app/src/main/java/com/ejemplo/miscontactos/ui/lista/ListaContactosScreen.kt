package com.ejemplo.miscontactos.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ejemplo.miscontactos.R
import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.ui.componentes.AvatarContacto
import com.ejemplo.miscontactos.ui.componentes.EstadoCargando
import com.ejemplo.miscontactos.ui.componentes.EstadoError
import com.ejemplo.miscontactos.ui.componentes.EstadoVacio
import com.ejemplo.miscontactos.ui.theme.MisContactosTheme

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

/** Versión sin estado: solo dibuja lo que recibe. Es la que se previsualiza y se prueba. */
@OptIn(ExperimentalMaterial3Api::class)
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

@Composable
private fun BarraBusqueda(
    busqueda: String,
    soloFavoritos: Boolean,
    onBusquedaChange: (String) -> Unit,
    onAlternarSoloFavoritos: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        OutlinedTextField(
            value = busqueda,
            onValueChange = onBusquedaChange,
            placeholder = { Text(stringResource(R.string.buscar_contactos)) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (busqueda.isNotEmpty()) {
                    IconButton(onClick = { onBusquedaChange("") }) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = stringResource(R.string.limpiar_busqueda)
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            modifier = Modifier.fillMaxWidth()
        )
        FilterChip(
            selected = soloFavoritos,
            onClick = onAlternarSoloFavoritos,
            label = { Text(stringResource(R.string.filtro_solo_favoritos)) },
            leadingIcon = {
                Icon(
                    imageVector = if (soloFavoritos) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }
        )
    }
}

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
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

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

@Preview(showBackground = true)
@Composable
private fun ListaContactosPreview() {
    MisContactosTheme {
        ListaContactosContent(
            uiState = ListaContactosUiState(
                contactos = listOf(
                    Contacto(1, "Ana", "Rojas", "ana.rojas@example.com"),
                    Contacto(2, "Diego", "Soto", "diego.soto@example.com")
                ),
                favoritos = setOf(2)
            ),
            onBusquedaChange = {},
            onAlternarSoloFavoritos = {},
            onAlternarFavorito = {},
            onRecargar = {},
            onCargarMas = {},
            onVerContacto = {},
            onNuevoContacto = {}
        )
    }
}
