package de.yanos.libraries.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.yanos.libraries.util.prefs.AppSettings
import de.yanos.libraries.util.prefs.AppSettingsImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class InterfaceModule {
    @Binds
    abstract fun provideAppSettings(appSettings: AppSettingsImpl): AppSettings
}