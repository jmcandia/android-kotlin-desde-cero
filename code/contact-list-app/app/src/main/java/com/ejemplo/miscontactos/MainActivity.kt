package com.ejemplo.miscontactos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.ejemplo.miscontactos.ui.componentes.PermisoRedLocal
import com.ejemplo.miscontactos.ui.navigation.ContactosNavHost
import com.ejemplo.miscontactos.ui.theme.MisContactosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MisContactosTheme {
                PermisoRedLocal {
                    ContactosNavHost()
                }
            }
        }
    }
}
