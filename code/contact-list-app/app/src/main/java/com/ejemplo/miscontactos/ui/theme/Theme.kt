package com.ejemplo.miscontactos.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = Azul,
    onPrimary = Color.White,
    secondary = Turquesa,
    onSecondary = Color.White,
    error = Error
)

private val DarkColorScheme = darkColorScheme(
    primary = AzulClaro,
    onPrimary = Color(0xFF003062),
    secondary = TurquesaClaro,
    onSecondary = Color(0xFF003739),
    error = ErrorClaro
)

@Composable
fun MisContactosTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // En false, la app usa siempre su propia paleta en lugar de los colores del fondo de pantalla.
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
