package com.ejemplo.miscontactos.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ejemplo.miscontactos.data.ContactosRepository
import com.ejemplo.miscontactos.model.comoErrorDatos
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ListaContactosViewModel @Inject constructor(
    private val repository: ContactosRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ListaContactosUiState())
    val uiState: StateFlow<ListaContactosUiState> = _uiState.asStateFlow()

    private var paginaActual = 0
    private var cargaJob: Job? = null

    init {
        viewModelScope.launch {
            repository.favoritos.collect { ids ->
                _uiState.update { it.copy(favoritos = ids) }
            }
        }
        viewModelScope.launch {
            repository.cambios.collect { recargar() }
        }
        recargar()
    }

    /** Vuelve a pedir la primera página (al iniciar, al reintentar o al deslizar para actualizar). */
    fun recargar() {
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch { cargarPagina(0) }
    }

    fun cargarMas() {
        val estado = _uiState.value
        if (estado.cargando || estado.cargandoMas || !estado.hayMas) return
        cargaJob = viewModelScope.launch { cargarPagina(paginaActual + 1) }
    }

    /** Cada tecla reinicia la espera: solo se busca cuando el usuario deja de escribir. */
    fun cambiarBusqueda(texto: String) {
        _uiState.update { it.copy(busqueda = texto) }
        cargaJob?.cancel()
        cargaJob = viewModelScope.launch {
            delay(ESPERA_BUSQUEDA_MS)
            cargarPagina(0)
        }
    }

    fun alternarSoloFavoritos() {
        _uiState.update { it.copy(soloFavoritos = !it.soloFavoritos) }
    }

    fun alternarFavorito(id: Int) {
        viewModelScope.launch { repository.alternarFavorito(id) }
    }

    private suspend fun cargarPagina(pagina: Int) {
        _uiState.update {
            if (pagina == 0) it.copy(cargando = true, error = null)
            else it.copy(cargandoMas = true, error = null)
        }

        val busqueda = _uiState.value.busqueda.trim().ifBlank { null }
        repository.obtenerPagina(pagina, busqueda)
            .onSuccess { resultado ->
                paginaActual = resultado.pagina
                _uiState.update { estado ->
                    estado.copy(
                        contactos = if (pagina == 0) {
                            resultado.contactos
                        } else {
                            estado.contactos + resultado.contactos
                        },
                        hayMas = resultado.hayMas,
                        desdeCache = resultado.desdeCache,
                        cargando = false,
                        cargandoMas = false
                    )
                }
            }
            .onFailure { error ->
                _uiState.update {
                    it.copy(cargando = false, cargandoMas = false, error = error.comoErrorDatos())
                }
            }
    }

    private companion object {
        const val ESPERA_BUSQUEDA_MS = 400L
    }
}
