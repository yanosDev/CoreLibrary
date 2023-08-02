package de.yanos.libraries.di

import android.content.Context
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.yanos.core.utils.GoogleClientId
import de.yanos.libraries.R
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    fun provideCrashlytics(): FirebaseCrashlytics {
        return FirebaseCrashlytics.getInstance()
    }

    @Provides
    @GoogleClientId
    fun provideGoogleClientId(@ApplicationContext context: Context): String {
        return context.getString(R.string.default_web_client_id)
    }
}