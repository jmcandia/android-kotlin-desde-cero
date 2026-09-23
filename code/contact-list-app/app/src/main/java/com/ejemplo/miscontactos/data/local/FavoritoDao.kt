package com.ejemplo.miscontactos.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritoDao {

    @Query("SELECT contactoId FROM favoritos")
    fun observarIds(): Flow<List<Int>>

    @Query("SELECT EXISTS(SELECT 1 FROM favoritos WHERE contactoId = :id)")
    suspend fun esFavorito(id: Int): Boolean

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertar(favorito: FavoritoEntity)

    @Query("DELETE FROM favoritos WHERE contactoId = :id")
    suspend fun borrar(id: Int)
}
