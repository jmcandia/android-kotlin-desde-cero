package com.ejemplo.miscontactos.ui.componentes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ejemplo.miscontactos.R

/**
 * Desde Android 17 (API 37), una app necesita el permiso ACCESS_LOCAL_NETWORK para
 * conectarse a direcciones de la red local, como la API del curso en 10.0.2.2.
 * Muestra [contenido] solo cuando el permiso está otorgado (o no hace falta).
 */
@Composable
fun PermisoRedLocal(contenido: @Composable () -> Unit) {
    if (Build.VERSION.SDK_INT < 37) {
        contenido()
        return
    }

    val context = LocalContext.current
    var otorgado by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_LOCAL_NETWORK) ==
                PackageManager.PERMISSION_GRANTED
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido -> otorgado = concedido }

    if (otorgado) {
        contenido()
        return
    }

    LaunchedEffect(Unit) {
        launcher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.permiso_red_local_texto),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
            Button(onClick = { launcher.launch(Manifest.permission.ACCESS_LOCAL_NETWORK) }) {
                Text(stringResource(R.string.permiso_red_local_boton))
            }
        }
    }
}
