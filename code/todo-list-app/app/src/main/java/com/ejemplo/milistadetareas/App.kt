package com.ejemplo.milistadetareas

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun App(viewModel: TareasViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = "lista"
    ) {
        composable("lista") {
            ListaTareasScreen(
                uiState = uiState,
                onAgregar = viewModel::agregarTarea,
                onCambiarCompletada = viewModel::cambiarCompletada,
                onEliminar = viewModel::eliminarTarea,
                onDeshacer = viewModel::deshacerEliminacion,
                onMensajeMostrado = viewModel::mensajeMostrado,
                onVerDetalle = { id -> navController.navigate("detalle/$id") }
            )
        }
        composable(
            route = "detalle/{tareaId}",
            arguments = listOf(navArgument("tareaId") { type = NavType.StringType })
        ) { backStackEntry ->
            val tareaId = backStackEntry.arguments?.getString("tareaId")
            val tarea = uiState.tareas.firstOrNull { it.id == tareaId }

            if (tarea != null) {
                DetalleTareaScreen(
                    tarea = tarea,
                    onVolver = { navController.popBackStack() }
                )
            } else {
                Text(stringResource(R.string.tarea_no_encontrada))
            }
        }
    }
}
