package com.ejemplo.miscontactos.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Un contacto tal como lo entrega la API (los campos opcionales pueden no venir). */
@Serializable
data class ContactDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null
)

/** Respuesta paginada de GET /api/contact en formato HAL. */
@Serializable
data class ContactPageDto(
    @SerialName("_embedded") val embedded: ContactEmbeddedDto? = null,
    val page: PageInfoDto
)

@Serializable
data class ContactEmbeddedDto(
    @SerialName("contactResponseList") val contacts: List<ContactDto> = emptyList()
)

@Serializable
data class PageInfoDto(
    val number: Int,
    val size: Int,
    val totalElements: Int,
    val totalPages: Int
)

/** Cuerpo que se envía al crear o actualizar un contacto. */
@Serializable
data class ContactRequestDto(
    val firstName: String,
    val lastName: String,
    val email: String,
    val phone: String? = null,
    val address: String? = null,
    val city: String? = null
)

/** Forma de los errores que devuelve la API (400, 404, 409...). */
@Serializable
data class ApiErrorDto(
    val status: Int? = null,
    val error: String? = null,
    val message: String? = null,
    val errors: List<String> = emptyList()
)
