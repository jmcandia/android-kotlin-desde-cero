package com.ejemplo.miscontactos.di

import com.ejemplo.miscontactos.data.ContactosRepository
import com.ejemplo.miscontactos.data.ContactosRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    @Binds
    abstract fun bindContactosRepository(impl: ContactosRepositoryImpl): ContactosRepository
}
