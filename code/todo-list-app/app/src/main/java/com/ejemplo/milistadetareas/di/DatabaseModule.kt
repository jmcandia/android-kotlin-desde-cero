package com.ejemplo.milistadetareas.di

import android.content.Context
import androidx.room.Room
import com.ejemplo.milistadetareas.data.local.TareaDao
import com.ejemplo.milistadetareas.data.local.TareasDatabase
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
    fun provideDatabase(@ApplicationContext context: Context): TareasDatabase =
        Room.databaseBuilder(context, TareasDatabase::class.java, "tareas.db").build()

    @Provides
    fun provideTareaDao(database: TareasDatabase): TareaDao = database.tareaDao()
}
