package com.ejemplo.miscontactos.ui.componentes

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.ejemplo.miscontactos.model.Contacto

/**
 * Muestra las iniciales del contacto y, encima, una foto de ejemplo cargada con Coil.
 * Si la foto no se puede descargar (por ejemplo, sin conexión), quedan visibles las iniciales.
 */
@Composable
fun AvatarContacto(
    contacto: Contacto,
    modifier: Modifier = Modifier,
    tamano: Dp = 48.dp,
    mostrarFoto: Boolean = true
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(tamano)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Text(
            text = contacto.iniciales,
            style = if (tamano >= 80.dp) {
                MaterialTheme.typography.headlineMedium
            } else {
                MaterialTheme.typography.titleMedium
            },
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
        if (mostrarFoto) {
            AsyncImage(
                model = "https://i.pravatar.cc/300?u=contacto-${contacto.id}",
                contentDescription = null, // decorativa: el nombre ya se lee junto al avatar
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(tamano)
            )
        }
    }
}
