package de.yanos.data.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.yanos.data.service.auth.AuthLocalSource
import de.yanos.data.service.auth.AuthLocalSourceImpl
import de.yanos.data.service.auth.AuthRemoteSource
import de.yanos.data.service.auth.AuthRemoteSourceImpl
import de.yanos.data.service.auth.AuthRepository
import de.yanos.data.service.auth.AuthRepositoryImpl


@Module
@InstallIn(SingletonComponent::class)
internal abstract class InterfaceModule {
    @Binds
    abstract fun provideAuthLocal(source: AuthLocalSourceImpl): AuthLocalSource

    @Binds
    abstract fun provideAuthRemote(source: AuthRemoteSourceImpl): AuthRemoteSource

    @Binds
    abstract fun provideAuthRepo(repo: AuthRepositoryImpl): AuthRepository
}