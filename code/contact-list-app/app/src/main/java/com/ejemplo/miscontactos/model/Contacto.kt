package com.ejemplo.miscontactos.model

data class Contacto(
    val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val ciudad: String? = null
) {
    val nombreCompleto: String
        get() = "$nombre $apellido"

    val iniciales: String
        get() = "${nombre.firstOrNull() ?: ""}${apellido.firstOrNull() ?: ""}".uppercase()
}

/** Datos que el usuario escribe para crear o editar un contacto (todavía sin id). */
data class DatosContacto(
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String? = null,
    val direccion: String? = null,
    val ciudad: String? = null
)

/** Una página de resultados, tal como la necesita la app. */
data class PaginaContactos(
    val contactos: List<Contacto>,
    val pagina: Int,
    val totalPaginas: Int,
    val desdeCache: Boolean = false
) {
    val hayMas: Boolean
        get() = pagina + 1 < totalPaginas
}
