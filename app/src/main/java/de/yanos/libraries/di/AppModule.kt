package de.yanos.libraries.di

import android.content.Context
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.yanos.core.utils.GoogleClientId
import de.yanos.core.utils.SignInRequest
import de.yanos.core.utils.SignUpRequest
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

    @Provides
    @SignInRequest
    fun provideSignInRequest(@GoogleClientId clientId: String): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(clientId)
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
    }
    @Provides
    @SignUpRequest
    fun provideSignUpRequest(@GoogleClientId clientId: String): BeginSignInRequest {
        return BeginSignInRequest.Builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.Builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(clientId)
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()
    }
}