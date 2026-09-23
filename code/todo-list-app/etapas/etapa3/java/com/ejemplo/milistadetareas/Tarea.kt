package com.ejemplo.milistadetareas

import java.util.UUID

data class Tarea(
    val id: String = UUID.randomUUID().toString(),
    val texto: String,
    val completada: Boolean = false
)
