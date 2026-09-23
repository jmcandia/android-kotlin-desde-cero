package com.ejemplo.miscontactos.data

import com.ejemplo.miscontactos.model.Contacto
import com.ejemplo.miscontactos.model.DatosContacto
import com.ejemplo.miscontactos.model.PaginaContactos
import kotlinx.coroutines.flow.Flow

/**
 * Punto único de acceso a los contactos. Quien lo usa no sabe si los datos
 * vienen de la API (Retrofit) o de la base de datos local (Room).
 */
interface ContactosRepository {

    /** Emite un valor cada vez que se crea, edita o elimina un contacto. */
    val cambios: Flow<Unit>

    /** Ids de los contactos marcados como favoritos (dato solo local). */
    val favoritos: Flow<Set<Int>>

    suspend fun obtenerPagina(pagina: Int, busqueda: String? = null): Result<PaginaContactos>
    suspend fun obtenerContacto(id: Int): Result<Contacto>
    suspend fun crear(datos: DatosContacto): Result<Contacto>
    suspend fun actualizar(id: Int, datos: DatosContacto): Result<Contacto>
    suspend fun eliminar(id: Int): Result<Unit>
    suspend fun alternarFavorito(id: Int)
}
