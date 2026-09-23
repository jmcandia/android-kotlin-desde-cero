package com.ejemplo.milistadetareas.data

import com.ejemplo.milistadetareas.Tarea
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Singleton
class TareasRepositoryEnMemoria @Inject constructor() : TareasRepository {

    private val _tareas = MutableStateFlow<List<Tarea>>(emptyList())
    override val tareas: Flow<List<Tarea>> = _tareas.asStateFlow()

    override suspend fun agregar(tarea: Tarea) {
        _tareas.update { lista -> lista + tarea }
    }

    override suspend fun actualizar(tarea: Tarea) {
        _tareas.update { lista -> lista.map { if (it.id == tarea.id) tarea else it } }
    }

    override suspend fun eliminar(id: String) {
        _tareas.update { lista -> lista.filter { it.id != id } }
    }
}
