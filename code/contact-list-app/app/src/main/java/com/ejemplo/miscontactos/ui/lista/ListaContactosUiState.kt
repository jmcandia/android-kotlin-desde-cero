package com.ejemplo.miscontactos.ui.lista

import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.ErrorDatos

data class ListaContactosUiState(
    val contactos: List<Contacto> = emptyList(),
    val busqueda: String = "",
    val favoritos: Set<Int> = emptySet(),
    val soloFavoritos: Boolean = false,
    val cargando: Boolean = false,
    val cargandoMas: Boolean = false,
    val hayMas: Boolean = false,
    val desdeCache: Boolean = false,
    val error: ErrorDatos? = null
) {
    /** Lo que realmente se muestra: todos los contactos cargados o solo los favoritos. */
    val contactosVisibles: List<Contacto>
        get() = if (soloFavoritos) contactos.filter { it.id in favoritos } else contactos
}
