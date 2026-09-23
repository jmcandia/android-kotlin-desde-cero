package com.ejemplo.milistadetareas.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TareaDao {

    @Query("SELECT * FROM tareas ORDER BY creadaEn")
    fun observarTodas(): Flow<List<TareaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(tarea: TareaEntity)

    @Query("UPDATE tareas SET texto = :texto, completada = :completada WHERE id = :id")
    suspend fun actualizar(id: String, texto: String, completada: Boolean)

    @Query("DELETE FROM tareas WHERE id = :id")
    suspend fun eliminar(id: String)
}
