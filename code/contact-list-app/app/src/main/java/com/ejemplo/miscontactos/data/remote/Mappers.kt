package com.ejemplo.miscontactos.data.remote

import com.ejemplo.miscontactos.data.remote.dto.ContactDto
import com.ejemplo.miscontactos.data.remote.dto.ContactPageDto
import com.ejemplo.miscontactos.data.remote.dto.ContactRequestDto
import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.DatosContacto
import com.ejemplo.miscontactos.model.PaginaContactos

fun ContactDto.aDominio(): Contacto = Contacto(
    id = id,
    nombre = firstName,
    apellido = lastName,
    email = email,
    telefono = phone,
    direccion = address,
    ciudad = city
)

fun ContactPageDto.aDominio(): PaginaContactos = PaginaContactos(
    contactos = embedded?.contacts.orEmpty().map { it.aDominio() },
    pagina = page.number,
    totalPaginas = page.totalPages
)

fun DatosContacto.aRequest(): ContactRequestDto = ContactRequestDto(
    firstName = nombre,
    lastName = apellido,
    email = email,
    phone = telefono,
    address = direccion,
    city = ciudad
)
