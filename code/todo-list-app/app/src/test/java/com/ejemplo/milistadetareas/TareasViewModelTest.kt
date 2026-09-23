package com.ejemplo.milistadetareas

import com.ejemplo.milistadetareas.data.TareasRepositoryEnMemoria
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TareasViewModelTest {

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun agregarTarea_laAgregaAlEstado() = runTest {
        val viewModel = TareasViewModel(TareasRepositoryEnMemoria())

        viewModel.agregarTarea("Comprar pan")

        assertEquals(1, viewModel.uiState.value.tareas.size)
        assertEquals("Comprar pan", viewModel.uiState.value.tareas.first().texto)
    }

    @Test
    fun agregarTarea_ignoraTextoEnBlanco() = runTest {
        val viewModel = TareasViewModel(TareasRepositoryEnMemoria())

        viewModel.agregarTarea("   ")

        assertEquals(0, viewModel.uiState.value.tareas.size)
    }

    @Test
    fun eliminarYDeshacer_restauraLaTarea() = runTest {
        val viewModel = TareasViewModel(TareasRepositoryEnMemoria())
        viewModel.agregarTarea("Estudiar")
        val id = viewModel.uiState.value.tareas.first().id

        viewModel.eliminarTarea(id)
        assertEquals(0, viewModel.uiState.value.tareas.size)
        assertEquals(Mensaje.TareaEliminada, viewModel.uiState.value.mensaje)

        viewModel.deshacerEliminacion()
        assertEquals(1, viewModel.uiState.value.tareas.size)
    }
}
