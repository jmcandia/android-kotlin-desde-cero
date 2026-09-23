package com.ejemplo.miscontactos.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ContactoEntity::class, FavoritoEntity::class],
    version = 1
)
abstract class ContactosDatabase : RoomDatabase() {
    abstract fun contactoDao(): ContactoDao
    abstract fun favoritoDao(): FavoritoDao
}
