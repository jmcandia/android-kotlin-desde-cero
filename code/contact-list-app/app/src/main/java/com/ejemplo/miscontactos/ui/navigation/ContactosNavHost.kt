package com.ejemplo.miscontactos.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ejemplo.miscontactos.ui.detalle.DetalleContactoScreen
import com.ejemplo.miscontactos.ui.formulario.FormularioContactoScreen
import com.ejemplo.miscontactos.ui.lista.ListaContactosScreen

@Composable
fun ContactosNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ListaContactosRuta) {
        composable<ListaContactosRuta> {
            ListaContactosScreen(
                onVerContacto = { id -> navController.navigate(DetalleContactoRuta(id)) },
                onNuevoContacto = { navController.navigate(FormularioContactoRuta()) }
            )
        }
        composable<DetalleContactoRuta> {
            DetalleContactoScreen(
                onEditar = { id -> navController.navigate(FormularioContactoRuta(id)) },
                onVolver = { navController.popBackStack() }
            )
        }
        composable<FormularioContactoRuta> {
            FormularioContactoScreen(
                onVolver = { navController.popBackStack() }
            )
        }
    }
}
