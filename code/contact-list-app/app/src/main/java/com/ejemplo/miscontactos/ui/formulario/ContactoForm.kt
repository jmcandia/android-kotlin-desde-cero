package com.ejemplo.miscontactos.ui.formulario

import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.DatosContacto

/** Lo que el usuario está escribiendo: todo es texto, tal como sale de los campos. */
data class ContactoForm(
    val nombre: String = "",
    val apellido: String = "",
    val email: String = "",
    val telefono: String = "",
    val direccion: String = "",
    val ciudad: String = ""
)

enum class Campo(val nombreApi: String) {
    NOMBRE("firstName"),
    APELLIDO("lastName"),
    EMAIL("email"),
    TELEFONO("phone"),
    DIRECCION("address"),
    CIUDAD("city")
}

sealed interface ErrorCampo {
    data object Obligatorio : ErrorCampo
    data class Longitud(val minimo: Int, val maximo: Int) : ErrorCampo
    data class LargoMaximo(val maximo: Int) : ErrorCampo
    data object EmailInvalido : ErrorCampo
    /** Mensaje que envió el servidor para este campo. */
    data class Servidor(val mensaje: String) : ErrorCampo
}

private val patronEmail = Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")

/**
 * Aplica las mismas reglas que la API, para avisar al usuario antes de enviar.
 * Es una función pura: recibe un formulario y devuelve sus errores, sin tocar nada más.
 */
fun validarContacto(form: ContactoForm): Map<Campo, ErrorCampo> = buildMap {
    fun obligatorioConLongitud(campo: Campo, valor: String, minimo: Int, maximo: Int) {
        when {
            valor.isBlank() -> put(campo, ErrorCampo.Obligatorio)
            valor.trim().length !in minimo..maximo -> put(campo, ErrorCampo.Longitud(minimo, maximo))
        }
    }

    obligatorioConLongitud(Campo.NOMBRE, form.nombre, 3, 150)
    obligatorioConLongitud(Campo.APELLIDO, form.apellido, 3, 150)

    when {
        form.email.isBlank() -> put(Campo.EMAIL, ErrorCampo.Obligatorio)
        !patronEmail.matches(form.email.trim()) -> put(Campo.EMAIL, ErrorCampo.EmailInvalido)
        form.email.trim().length > 255 -> put(Campo.EMAIL, ErrorCampo.LargoMaximo(255))
    }

    if (form.telefono.trim().length > 20) put(Campo.TELEFONO, ErrorCampo.LargoMaximo(20))
    if (form.direccion.trim().length > 255) put(Campo.DIRECCION, ErrorCampo.LargoMaximo(255))
    if (form.ciudad.trim().length > 100) put(Campo.CIUDAD, ErrorCampo.LargoMaximo(100))
}

/**
 * Convierte los errores de la API ("email: debe ser una dirección...") en errores por campo.
 * Si un campo trae varios mensajes, se conserva el primero.
 */
fun erroresDelServidor(errores: List<String>): Map<Campo, ErrorCampo> = buildMap {
    for (texto in errores) {
        val nombreApi = texto.substringBefore(":").trim()
        val mensaje = texto.substringAfter(":").trim()
        val campo = Campo.entries.firstOrNull { it.nombreApi == nombreApi } ?: continue
        if (campo !in this) put(campo, ErrorCampo.Servidor(mensaje))
    }
}

fun ContactoForm.aDatos(): DatosContacto = DatosContacto(
    nombre = nombre.trim(),
    apellido = apellido.trim(),
    email = email.trim(),
    telefono = telefono.trim().ifBlank { null },
    direccion = direccion.trim().ifBlank { null },
    ciudad = ciudad.trim().ifBlank { null }
)

fun Contacto.aFormulario(): ContactoForm = ContactoForm(
    nombre = nombre,
    apellido = apellido,
    email = email,
    telefono = telefono.orEmpty(),
    direccion = direccion.orEmpty(),
    ciudad = ciudad.orEmpty()
)
