package com.ejemplo.miscontactos.ui.componentes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ejemplo.miscontactos.R
import com.ejemplo.miscontactos.model.ErrorDatos

@Composable
fun EstadoCargando(modifier: Modifier = Modifier) {
    val descripcion = stringResource(R.string.cargando)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            modifier = Modifier.semantics { contentDescription = descripcion }
        )
    }
}

@Composable
fun EstadoError(
    error: ErrorDatos,
    onReintentar: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = mensajeDe(error),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(onClick = onReintentar) {
            Text(stringResource(R.string.reintentar))
        }
    }
}

@Composable
fun EstadoVacio(mensaje: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Convierte un error de datos en un texto para el usuario, desde los recursos de la app. */
@Composable
fun mensajeDe(error: ErrorDatos): String = when (error) {
    is ErrorDatos.SinConexion -> stringResource(R.string.error_sin_conexion)
    is ErrorDatos.NoEncontrado -> stringResource(R.string.error_no_encontrado)
    is ErrorDatos.Conflicto -> stringResource(R.string.error_conflicto)
    is ErrorDatos.Validacion -> stringResource(R.string.error_validacion)
    is ErrorDatos.Desconocido -> stringResource(R.string.error_desconocido)
}
