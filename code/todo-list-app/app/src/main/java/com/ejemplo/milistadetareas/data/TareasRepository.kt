package com.ejemplo.milistadetareas.data

import com.ejemplo.milistadetareas.Tarea
import kotlinx.coroutines.flow.Flow

interface TareasRepository {
    val tareas: Flow<List<Tarea>>
    suspend fun agregar(tarea: Tarea)
    suspend fun actualizar(tarea: Tarea)
    suspend fun eliminar(id: String)
}
