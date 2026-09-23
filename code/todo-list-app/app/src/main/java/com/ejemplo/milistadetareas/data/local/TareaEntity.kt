package com.ejemplo.milistadetareas.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ejemplo.milistadetareas.Tarea

@Entity(tableName = "tareas")
data class TareaEntity(
    @PrimaryKey val id: String,
    val texto: String,
    val completada: Boolean,
    val creadaEn: Long
)

fun TareaEntity.aDominio(): Tarea =
    Tarea(id = id, texto = texto, completada = completada)

fun Tarea.aEntity(creadaEn: Long = System.currentTimeMillis()): TareaEntity =
    TareaEntity(id = id, texto = texto, completada = completada, creadaEn = creadaEn)
