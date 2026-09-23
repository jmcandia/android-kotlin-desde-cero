package com.ejemplo.miscontactos.ui.formulario

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.ejemplo.miscontactos.data.ContactosRepository
import com.ejemplo.miscontactos.model.ErrorDatos
import com.ejemplo.miscontactos.model.comoErrorDatos
import com.ejemplo.miscontactos.ui.navigation.FormularioContactoRuta
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FormularioContactoUiState(
    val formulario: ContactoForm = ContactoForm(),
    val errores: Map<Campo, ErrorCampo> = emptyMap(),
    val esEdicion: Boolean = false,
    val cargando: Boolean = false,
    val guardando: Boolean = false,
    val guardado: Boolean = false,
    val errorGeneral: ErrorDatos? = null
)

@HiltViewModel
class FormularioContactoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: ContactosRepository
) : ViewModel() {

    private val idEdicion: Int? = savedStateHandle.toRoute<FormularioContactoRuta>().id

    private val _uiState = MutableStateFlow(FormularioContactoUiState(esEdicion = idEdicion != null))
    val uiState: StateFlow<FormularioContactoUiState> = _uiState.asStateFlow()

    init {
        if (idEdicion != null) cargarContacto(idEdicion)
    }

    private fun cargarContacto(id: Int) {
        _uiState.update { it.copy(cargando = true) }
        viewModelScope.launch {
            repository.obtenerContacto(id)
                .onSuccess { contacto ->
                    _uiState.update { it.copy(formulario = contacto.aFormulario(), cargando = false) }
                }
                .onFailure { error ->
                    _uiState.update { it.copy(cargando = false, errorGeneral = error.comoErrorDatos()) }
                }
        }
    }

    /** Cada cambio en un campo borra el error de ese campo: el usuario ya lo está corrigiendo. */
    fun cambiarCampo(campo: Campo, valor: String) {
        _uiState.update { estado ->
            val formulario = when (campo) {
                Campo.NOMBRE -> estado.formulario.copy(nombre = valor)
                Campo.APELLIDO -> estado.formulario.copy(apellido = valor)
                Campo.EMAIL -> estado.formulario.copy(email = valor)
                Campo.TELEFONO -> estado.formulario.copy(telefono = valor)
                Campo.DIRECCION -> estado.formulario.copy(direccion = valor)
                Campo.CIUDAD -> estado.formulario.copy(ciudad = valor)
            }
            estado.copy(formulario = formulario, errores = estado.errores - campo)
        }
    }

    fun guardar() {
        val estado = _uiState.value
        if (estado.guardando) return

        val errores = validarContacto(estado.formulario)
        if (errores.isNotEmpty()) {
            _uiState.update { it.copy(errores = errores) }
            return
        }

        _uiState.update { it.copy(guardando = true, errorGeneral = null) }
        viewModelScope.launch {
            val datos = estado.formulario.aDatos()
            val resultado = if (idEdicion == null) {
                repository.crear(datos)
            } else {
                repository.actualizar(idEdicion, datos)
            }

            resultado
                .onSuccess { _uiState.update { it.copy(guardando = false, guardado = true) } }
                .onFailure { throwable ->
                    val error = throwable.comoErrorDatos()
                    _uiState.update {
                        when (error) {
                            is ErrorDatos.Validacion -> it.copy(
                                guardando = false,
                                errores = erroresDelServidor(error.errores)
                            )
                            is ErrorDatos.Conflicto -> it.copy(
                                guardando = false,
                                errores = mapOf(Campo.EMAIL to ErrorCampo.Servidor(error.message.orEmpty())),
                                errorGeneral = error
                            )
                            else -> it.copy(guardando = false, errorGeneral = error)
                        }
                    }
                }
        }
    }

    fun errorGeneralMostrado() {
        _uiState.update { it.copy(errorGeneral = null) }
    }
}
