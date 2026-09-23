package com.ejemplo.miscontactos.data.remote

import com.ejemplo.miscontactos.data.remote.dto.ContactDto
import com.ejemplo.miscontactos.data.remote.dto.ContactPageDto
import com.ejemplo.miscontactos.data.remote.dto.ContactRequestDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ContactApi {

    @GET("api/contact")
    suspend fun obtenerContactos(
        @Query("page") pagina: Int,
        @Query("size") tamano: Int,
        @Query("search") busqueda: String? = null,
        @Query("sort") orden: String = "firstName,asc"
    ): ContactPageDto

    @GET("api/contact/{id}")
    suspend fun obtenerContacto(@Path("id") id: Int): ContactDto

    @POST("api/contact")
    suspend fun crearContacto(@Body contacto: ContactRequestDto): ContactDto

    @PUT("api/contact/{id}")
    suspend fun actualizarContacto(
        @Path("id") id: Int,
        @Body contacto: ContactRequestDto
    ): ContactDto

    @DELETE("api/contact/{id}")
    suspend fun eliminarContacto(@Path("id") id: Int)
}
