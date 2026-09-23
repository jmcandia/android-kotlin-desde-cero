package com.ejemplo.miscontactos.ui.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ejemplo.miscontactos.data.ContactosRepository
import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.ErrorDatos
import com.ejemplo.miscontactos.model.comoErrorDatos
import com.ejemplo.miscontactos.ui.navigation.DetalleContactoRuta
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface DetalleContactoUiState {
    data object Cargando : DetalleContactoUiState
    data class Error(val error: ErrorDatos) : DetalleContactoUiState
    data class Contenido(
        val contacto: Contacto,
        val esFavorito: Boolean = false,
        val eliminando: Boolean = false,
        val eliminado: Boolean = false,
        val errorAccion: ErrorDatos? = null
    ) : DetalleContactoUiState
}

@HiltViewModel
class DetalleContactoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ContactosRepository
) : ViewModel() {

    private val id: Int = savedStateHandle.toRoute<DetalleContactoRuta>().id

    private val _uiState = MutableStateFlow<DetalleContactoUiState>(DetalleContactoUiState.Cargando)
    val uiState: StateFlow<DetalleContactoUiState> = _uiState.asStateFlow()

    private var favoritos: Set<Int> = emptySet()

    init {
        viewModelScope.launch {
            repository.favoritos.collect { ids ->
                favoritos = ids
                actualizarContenido { it.copy(esFavorito = id in ids) }
            }
        }
        viewModelScope.launch {
            repository.cambios.collect { cargar() }
        }
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            if (_uiState.value !is DetalleContactoUiState.Contenido) {
                _uiState.value = DetalleContactoUiState.Cargando
            }
            repository.obtenerContacto(id)
                .onSuccess { contacto ->
                    _uiState.value = DetalleContactoUiState.Contenido(
                        contacto = contacto,
                        esFavorito = id in favoritos
                    )
                }
                .onFailure { error ->
                    _uiState.value = DetalleContactoUiState.Error(error.comoErrorDatos())
                }
        }
    }

    fun alternarFavorito() {
        viewModelScope.launch { repository.alternarFavorito(id) }
    }

    fun eliminar() {
        actualizarContenido { it.copy(eliminando = true, errorAccion = null) }
        viewModelScope.launch {
            repository.eliminar(id)
                .onSuccess { actualizarContenido { it.copy(eliminando = false, eliminado = true) } }
                .onFailure { error ->
                    actualizarContenido { it.copy(eliminando = false, errorAccion = error.comoErrorDatos()) }
                }
        }
    }

    fun errorAccionMostrado() {
        actualizarContenido { it.copy(errorAccion = null) }
    }

    /** Aplica un cambio solo si la pantalla ya está mostrando un contacto. */
    private fun actualizarContenido(cambio: (DetalleContactoUiState.Contenido) -> DetalleContactoUiState.Contenido) {
        _uiState.update { estado ->
            if (estado is DetalleContactoUiState.Contenido) cambio(estado) else estado
        }
    }
}
