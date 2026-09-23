package com.ejemplo.miscontactos.ui.navigation

import kotlinx.serialization.Serializable

/** Rutas de la app. Al ser tipos (y no textos), el compilador verifica sus argumentos. */
@Serializable
data object ListaContactosRuta

@Serializable
data class DetalleContactoRuta(val id: Int)

/** Sin id: crear un contacto nuevo. Con id: editar uno existente. */
@Serializable
data class FormularioContactoRuta(val id: Int? = null)
