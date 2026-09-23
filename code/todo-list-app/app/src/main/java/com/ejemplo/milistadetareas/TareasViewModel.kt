package com.ejemplo.milistadetareas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.milistadetareas.data.TareasRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TareasViewModel @Inject constructor(
    private val repository: TareasRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TareasUiState())
    val uiState: StateFlow<TareasUiState> = _uiState.asStateFlow()

    private var ultimaEliminada: Tarea? = null

    init {
        viewModelScope.launch {
            repository.tareas.collect { tareas ->
                _uiState.update { estado -> estado.copy(tareas = tareas) }
            }
        }
    }

    fun agregarTarea(texto: String) {
        if (texto.isBlank()) return
        viewModelScope.launch {
            repository.agregar(Tarea(texto = texto.trim()))
        }
    }

    fun cambiarCompletada(id: String, completada: Boolean) {
        val tarea = _uiState.value.tareas.firstOrNull { it.id == id } ?: return
        viewModelScope.launch {
            repository.actualizar(tarea.copy(completada = completada))
        }
    }

    fun eliminarTarea(id: String) {
        ultimaEliminada = _uiState.value.tareas.firstOrNull { it.id == id }
        viewModelScope.launch {
            repository.eliminar(id)
            _uiState.update { estado -> estado.copy(mensaje = Mensaje.TareaEliminada) }
        }
    }

    fun deshacerEliminacion() {
        val tarea = ultimaEliminada ?: return
        ultimaEliminada = null
        viewModelScope.launch {
            repository.agregar(tarea)
        }
    }

    fun mensajeMostrado() {
        _uiState.update { estado -> estado.copy(mensaje = null) }
    }
}
