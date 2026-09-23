package com.ejemplo.miscontactos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction

@Dao
interface ContactoDao {

    @Query("SELECT * FROM contactos ORDER BY orden")
    suspend fun obtenerTodos(): List<ContactoEntity>

    @Query("SELECT * FROM contactos WHERE id = :id")
    suspend fun obtenerPorId(id: Int): ContactoEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(contactos: List<ContactoEntity>)

    @Query("DELETE FROM contactos")
    suspend fun borrarTodos()

    @Query("DELETE FROM contactos WHERE id = :id")
    suspend fun borrar(id: Int)

    /** Reemplaza la caché completa en una sola transacción. */
    @Transaction
    suspend fun reemplazarTodos(contactos: List<ContactoEntity>) {
        borrarTodos()
        insertarTodos(contactos)
    }
}
