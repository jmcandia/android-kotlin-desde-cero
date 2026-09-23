package com.ejemplo.milistadetareas.di

import com.ejemplo.milistadetareas.data.TareasRepository
import com.ejemplo.milistadetareas.data.TareasRepositoryEnMemoria
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindTareasRepository(
        impl: TareasRepositoryEnMemoria
    ): TareasRepository
}
