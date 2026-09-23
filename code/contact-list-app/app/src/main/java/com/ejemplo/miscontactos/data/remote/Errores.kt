package com.ejemplo.miscontactos.data.remote

import com.ejemplo.miscontactos.data.remote.dto.ApiErrorDto
import com.ejemplo.miscontactos.model.ErrorDatos
import java.io.IOException
import kotlinx.serialization.json.Json
import retrofit2.HttpException

/** Traduce cualquier excepción de red a un [ErrorDatos] que el resto de la app entiende. */
fun Throwable.aErrorDatos(json: Json): ErrorDatos = when (this) {
    is ErrorDatos -> this
    is IOException -> ErrorDatos.SinConexion()
    is HttpException -> {
        val cuerpo = response()?.errorBody()?.string()
        val error = cuerpo?.let { runCatching { json.decodeFromString<ApiErrorDto>(it) }.getOrNull() }
        when (code()) {
            400 -> ErrorDatos.Validacion(error?.errors.orEmpty())
            404 -> ErrorDatos.NoEncontrado()
            409 -> ErrorDatos.Conflicto(error?.message ?: "Conflicto")
            else -> ErrorDatos.Desconocido(error?.message ?: "HTTP ${code()}")
        }
    }
    else -> ErrorDatos.Desconocido(message ?: "Error desconocido")
}
