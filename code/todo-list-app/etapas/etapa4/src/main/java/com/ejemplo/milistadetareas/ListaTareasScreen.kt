package com.ejemplo.milistadetareas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ejemplo.milistadetareas.ui.theme.MiListaDeTareasTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaTareasScreen(
    uiState: TareasUiState,
    onAgregar: (String) -> Unit,
    onCambiarCompletada: (String, Boolean) -> Unit,
    onEliminar: (String) -> Unit,
    onDeshacer: () -> Unit,
    onMensajeMostrado: () -> Unit,
    onVerDetalle: (String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val textoTareaEliminada = stringResource(R.string.mensaje_tarea_eliminada)
    val textoDeshacer = stringResource(R.string.accion_deshacer)

    LaunchedEffect(uiState.mensaje) {
        if (uiState.mensaje == Mensaje.TareaEliminada) {
            val resultado = snackbarHostState.showSnackbar(
                message = textoTareaEliminada,
                actionLabel = textoDeshacer
            )
            if (resultado == SnackbarResult.ActionPerformed) {
                onDeshacer()
            }
            onMensajeMostrado()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.titulo_pantalla)) })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            FormularioNuevaTarea(onAgregar = onAgregar)

            Text(
                text = stringResource(
                    R.string.resumen_pendientes,
                    uiState.pendientes,
                    uiState.tareas.size
                ),
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            if (uiState.tareas.isEmpty()) {
                Text(
                    text = stringResource(R.string.mensaje_lista_vacia),
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                LazyColumn(modifier = Modifier.padding(horizontal = 16.dp)) {
                    items(uiState.tareas, key = { it.id }) { tarea ->
                        TareaItem(
                            tarea = tarea,
                            onCambiarCompletada = onCambiarCompletada,
                            onEliminar = onEliminar,
                            onVerDetalle = onVerDetalle
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FormularioNuevaTarea(
    onAgregar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var texto by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text(stringResource(R.string.etiqueta_nueva_tarea)) },
            modifier = Modifier.weight(1f)
        )
        Button(
            onClick = {
                onAgregar(texto)
                texto = ""
            },
            enabled = texto.isNotBlank(),
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(stringResource(R.string.boton_agregar))
        }
    }
}

@Composable
fun TareaItem(
    tarea: Tarea,
    onCambiarCompletada: (String, Boolean) -> Unit,
    onEliminar: (String) -> Unit,
    onVerDetalle: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onVerDetalle(tarea.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = tarea.completada,
                onCheckedChange = { marcada -> onCambiarCompletada(tarea.id, marcada) }
            )
            Text(
                text = tarea.texto,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodyLarge
            )
            IconButton(onClick = { onEliminar(tarea.id) }) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.descripcion_eliminar)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ListaTareasScreenPreview() {
    MiListaDeTareasTheme {
        ListaTareasScreen(
            uiState = TareasUiState(
                tareas = listOf(
                    Tarea(texto = "Comprar pan"),
                    Tarea(texto = "Estudiar Compose", completada = true)
                )
            ),
            onAgregar = {},
            onCambiarCompletada = { _, _ -> },
            onEliminar = {},
            onDeshacer = {},
            onMensajeMostrado = {},
            onVerDetalle = {}
        )
    }
}
