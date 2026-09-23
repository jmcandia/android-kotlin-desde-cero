package com.ejemplo.milistadetareas.data

import com.ejemplo.milistadetareas.Tarea
import com.ejemplo.milistadetareas.data.local.TareaDao
import com.ejemplo.milistadetareas.data.local.aDominio
import com.ejemplo.milistadetareas.data.local.aEntity
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TareasRepositoryRoom @Inject constructor(
    private val dao: TareaDao
) : TareasRepository {

    override val tareas: Flow<List<Tarea>> =
        dao.observarTodas().map { entidades -> entidades.map { it.aDominio() } }

    override suspend fun agregar(tarea: Tarea) {
        dao.insertar(tarea.aEntity())
    }

    override suspend fun actualizar(tarea: Tarea) {
        dao.actualizar(id = tarea.id, texto = tarea.texto, completada = tarea.completada)
    }

    override suspend fun eliminar(id: String) {
        dao.eliminar(id)
    }
}
