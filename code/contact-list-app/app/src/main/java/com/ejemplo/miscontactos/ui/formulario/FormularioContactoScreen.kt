package com.ejemplo.miscontactos.ui.formulario

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ejemplo.miscontactos.R
import com.ejemplo.miscontactos.ui.componentes.EstadoCargando
import com.ejemplo.miscontactos.ui.componentes.mensajeDe

@Composable
fun FormularioContactoScreen(
    onVolver: () -> Unit,
    viewModel: FormularioContactoViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.guardado) {
        if (uiState.guardado) onVolver()
    }

    FormularioContactoContent(
        uiState = uiState,
        onCampoChange = viewModel::cambiarCampo,
        onGuardar = viewModel::guardar,
        onVolver = onVolver,
        onErrorGeneralMostrado = viewModel::errorGeneralMostrado
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioContactoContent(
    uiState: FormularioContactoUiState,
    onCampoChange: (Campo, String) -> Unit,
    onGuardar: () -> Unit,
    onVolver: () -> Unit,
    onErrorGeneralMostrado: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val errorGeneral = uiState.errorGeneral
    val mensajeError = errorGeneral?.let { mensajeDe(it) }
    LaunchedEffect(errorGeneral) {
        if (mensajeError != null) {
            snackbarHostState.showSnackbar(mensajeError)
            onErrorGeneralMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(if (uiState.esEdicion) R.string.titulo_editar else R.string.titulo_nuevo))
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        if (uiState.cargando) {
            EstadoCargando(Modifier.padding(innerPadding))
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val form = uiState.formulario
            CampoTexto(Campo.NOMBRE, R.string.etiqueta_nombre, form.nombre, uiState, onCampoChange,
                KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next))
            CampoTexto(Campo.APELLIDO, R.string.etiqueta_apellido, form.apellido, uiState, onCampoChange,
                KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next))
            CampoTexto(Campo.EMAIL, R.string.etiqueta_email, form.email, uiState, onCampoChange,
                KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next))
            CampoTexto(Campo.TELEFONO, R.string.etiqueta_telefono, form.telefono, uiState, onCampoChange,
                KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next))
            CampoTexto(Campo.DIRECCION, R.string.etiqueta_direccion, form.direccion, uiState, onCampoChange,
                KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next))
            CampoTexto(Campo.CIUDAD, R.string.etiqueta_ciudad, form.ciudad, uiState, onCampoChange,
                KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Done))

            Button(
                onClick = onGuardar,
                enabled = !uiState.guardando,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .testTag("boton_guardar")
            ) {
                if (uiState.guardando) {
                    CircularProgressIndicator(modifier = Modifier.padding(4.dp))
                } else {
                    Text(stringResource(R.string.guardar))
                }
            }
        }
    }
}

@Composable
private fun CampoTexto(
    campo: Campo,
    etiqueta: Int,
    valor: String,
    uiState: FormularioContactoUiState,
    onCampoChange: (Campo, String) -> Unit,
    keyboardOptions: KeyboardOptions
) {
    val error = uiState.errores[campo]
    OutlinedTextField(
        value = valor,
        onValueChange = { onCampoChange(campo, it) },
        label = { Text(stringResource(etiqueta)) },
        isError = error != null,
        supportingText = error?.let { { Text(textoDe(it)) } },
        singleLine = true,
        keyboardOptions = keyboardOptions,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("campo_${campo.name.lowercase()}")
    )
}

@Composable
private fun textoDe(error: ErrorCampo): String = when (error) {
    ErrorCampo.Obligatorio -> stringResource(R.string.campo_obligatorio)
    is ErrorCampo.Longitud -> stringResource(R.string.campo_longitud, error.minimo, error.maximo)
    is ErrorCampo.LargoMaximo -> stringResource(R.string.campo_largo_maximo, error.maximo)
    ErrorCampo.EmailInvalido -> stringResource(R.string.campo_email_invalido)
    is ErrorCampo.Servidor -> error.mensaje
}
