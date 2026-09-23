package com.ejemplo.milistadetareas

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class TareasViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    private var ultimaEliminada: Tarea? = null

    fun agregarTarea(texto: String) {
        if (texto.isBlank()) return
        _uiState.update { estado ->
            estado.copy(tareas = estado.tareas + Tarea(texto = texto.trim()))
        }
    }

    fun cambiarCompletada(id: String, completada: Boolean) {
        _uiState.update { estado ->
            estado.copy(
                tareas = estado.tareas.map { tarea ->
                    if (tarea.id == id) tarea.copy(completada = completada) else tarea
                }
            )
        }
    }

    fun eliminarTarea(id: String) {
        ultimaEliminada = _uiState.value.tareas.firstOrNull { it.id == id }
        _uiState.update { estado ->
            estado.copy(
                tareas = estado.tareas.filter { it.id != id },
                mensaje = Mensaje.TareaEliminada
            )
        }
    }

    fun deshacerEliminacion() {
        val tarea = ultimaEliminada ?: return
        ultimaEliminada = null
        _uiState.update { estado -> estado.copy(tareas = estado.tareas + tarea) }
    }

    fun mensajeMostrado() {
        _uiState.update { estado -> estado.copy(mensaje = null) }
    }
}
