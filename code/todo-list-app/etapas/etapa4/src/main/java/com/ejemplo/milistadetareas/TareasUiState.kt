package com.ejemplo.milistadetareas

data class TareasUiState(
    val tareas: List<Tarea> = emptyList(),
    val mensaje: Mensaje? = null
) {
    val pendientes: Int
        get() = tareas.count { !it.completada }
}

sealed interface Mensaje {
    data object TareaEliminada : Mensaje
}
