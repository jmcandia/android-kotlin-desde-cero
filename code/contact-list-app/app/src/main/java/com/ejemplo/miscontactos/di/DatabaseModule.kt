package com.ejemplo.miscontactos.di

import android.content.Context
import androidx.room.Room
import com.ejemplo.miscontactos.data.local.ContactoDao
import com.ejemplo.miscontactos.data.local.ContactosDatabase
import com.ejemplo.miscontactos.data.local.FavoritoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ContactosDatabase =
        Room.databaseBuilder(context, ContactosDatabase::class.java, "contactos.db").build()

    @Provides
    fun provideContactoDao(database: ContactosDatabase): ContactoDao = database.contactoDao()

    @Provides
    fun provideFavoritoDao(database: ContactosDatabase): FavoritoDao = database.favoritoDao()
}
