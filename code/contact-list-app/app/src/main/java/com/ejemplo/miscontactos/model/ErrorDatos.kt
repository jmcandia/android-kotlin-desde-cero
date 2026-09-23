package com.ejemplo.miscontactos.model

/**
 * Los errores que la capa de datos puede entregar hacia arriba.
 * El ViewModel y la interfaz trabajan con estos casos, no con excepciones de Retrofit.
 */
sealed class ErrorDatos(mensaje: String) : Exception(mensaje) {
    class SinConexion : ErrorDatos("Sin conexión con el servidor")
    class NoEncontrado : ErrorDatos("Recurso no encontrado")
    class Conflicto(mensaje: String) : ErrorDatos(mensaje)
    class Validacion(val errores: List<String>) : ErrorDatos("Datos no válidos")
    class Desconocido(mensaje: String) : ErrorDatos(mensaje)
}

fun Throwable.comoErrorDatos(): ErrorDatos =
    this as? ErrorDatos ?: ErrorDatos.Desconocido(message ?: "Error desconocido")
