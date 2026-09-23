package com.ejemplo.miscontactos.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ejemplo.miscontactos.model.Contacto

/** Copia local de un contacto, usada como caché para trabajar sin conexión. */
@Entity(tableName = "contactos")
data class ContactoEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val apellido: String,
    val email: String,
    val telefono: String?,
    val direccion: String?,
    val ciudad: String?,
    val orden: Int
)

/** Un contacto marcado como favorito. Es un dato que solo existe en el dispositivo. */
@Entity(tableName = "favoritos")
data class FavoritoEntity(
    @PrimaryKey val contactoId: Int
)

fun ContactoEntity.aDominio(): Contacto = Contacto(
    id = id,
    nombre = nombre,
    apellido = apellido,
    email = email,
    telefono = telefono,
    direccion = direccion,
    ciudad = ciudad
)

fun Contacto.aEntity(orden: Int): ContactoEntity = ContactoEntity(
    id = id,
    nombre = nombre,
    apellido = apellido,
    email = email,
    telefono = telefono,
    direccion = direccion,
    ciudad = ciudad,
    orden = orden
)
