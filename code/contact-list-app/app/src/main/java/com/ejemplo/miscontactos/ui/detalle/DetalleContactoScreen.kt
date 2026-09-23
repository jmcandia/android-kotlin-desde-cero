package com.ejemplo.miscontactos.ui.detalle

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ejemplo.miscontactos.R
import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.ui.componentes.AvatarContacto
import com.ejemplo.miscontactos.ui.componentes.EstadoCargando
import com.ejemplo.miscontactos.ui.componentes.EstadoError
import com.ejemplo.miscontactos.ui.componentes.mensajeDe

@Composable
fun DetalleContactoScreen(
    onEditar: (Int) -> Unit,
    onVolver: () -> Unit,
    viewModel: DetalleContactoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Evento resuelto como estado: cuando el contacto quedó eliminado, volvemos a la lista.
    val eliminado = (uiState as? DetalleContactoUiState.Contenido)?.eliminado == true
    LaunchedEffect(eliminado) {
        if (eliminado) onVolver()
    }

    DetalleContactoContent(
        uiState = uiState,
        onVolver = onVolver,
        onEditar = onEditar,
        onAlternarFavorito = viewModel::alternarFavorito,
        onEliminar = viewModel::eliminar,
        onReintentar = viewModel::cargar,
        onErrorAccionMostrado = viewModel::errorAccionMostrado
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleContactoContent(
    uiState: DetalleContactoUiState,
    onVolver: () -> Unit,
    onEditar: (Int) -> Unit,
    onAlternarFavorito: () -> Unit,
    onEliminar: () -> Unit,
    onReintentar: () -> Unit,
    onErrorAccionMostrado: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    var confirmarEliminar by rememberSaveable { mutableStateOf(false) }
    val contenido = uiState as? DetalleContactoUiState.Contenido

    val errorAccion = contenido?.errorAccion
    val mensajeError = errorAccion?.let { mensajeDe(it) }
    LaunchedEffect(errorAccion) {
        if (mensajeError != null) {
            snackbarHostState.showSnackbar(mensajeError)
            onErrorAccionMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.titulo_detalle)) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver)
                        )
                    }
                },
                actions = {
                    if (contenido != null) {
                        IconToggleButton(
                            checked = contenido.esFavorito,
                            onCheckedChange = { onAlternarFavorito() }
                        ) {
                            Icon(
                                imageVector = if (contenido.esFavorito) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = stringResource(
                                    if (contenido.esFavorito) R.string.quitar_favorito else R.string.marcar_favorito
                                )
                            )
                        }
                        IconButton(onClick = { onEditar(contenido.contacto.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.editar))
                        }
                        IconButton(
                            onClick = { confirmarEliminar = true },
                            enabled = !contenido.eliminando
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.eliminar))
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)
        when (uiState) {
            is DetalleContactoUiState.Cargando -> EstadoCargando(modifier)
            is DetalleContactoUiState.Error ->
                EstadoError(error = uiState.error, onReintentar = onReintentar, modifier = modifier)
            is DetalleContactoUiState.Contenido ->
                FichaContacto(contacto = uiState.contacto, modifier = modifier)
        }
    }

    if (confirmarEliminar && contenido != null) {
        AlertDialog(
            onDismissRequest = { confirmarEliminar = false },
            title = { Text(stringResource(R.string.confirmar_eliminar_titulo)) },
            text = {
                Text(stringResource(R.string.confirmar_eliminar_texto, contenido.contacto.nombreCompleto))
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmarEliminar = false
                    onEliminar()
                }) {
                    Text(stringResource(R.string.eliminar))
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmarEliminar = false }) {
                    Text(stringResource(R.string.cancelar))
                }
            }
        )
    }
}

@Composable
private fun FichaContacto(contacto: Contacto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AvatarContacto(contacto = contacto, tamano = 120.dp)
        Text(
            text = contacto.nombreCompleto,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .semantics { heading() }
        )

        DatoContacto(Icons.Default.Email, stringResource(R.string.etiqueta_email), contacto.email)
        DatoContacto(Icons.Default.Phone, stringResource(R.string.etiqueta_telefono), contacto.telefono)
        DatoContacto(Icons.Default.Home, stringResource(R.string.etiqueta_direccion), contacto.direccion)
        DatoContacto(Icons.Default.LocationOn, stringResource(R.string.etiqueta_ciudad), contacto.ciudad)
    }
}

@Composable
private fun DatoContacto(icono: ImageVector, etiqueta: String, valor: String?) {
    ListItem(
        overlineContent = { Text(etiqueta) },
        headlineContent = { Text(valor ?: stringResource(R.string.sin_dato)) },
        leadingContent = { Icon(icono, contentDescription = null) }
    )
}
