package de.yanos.firestorewrapper.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.GoogleAuthProvider
import de.yanos.firestorewrapper.domain.AuthRepository
import kotlinx.coroutines.launch

internal class AuthViewModel(
    clientId: String,
    val authRepository: AuthRepository
) : ViewModel() {

    internal val signInRequest: BeginSignInRequest
    internal val signUpRequest: BeginSignInRequest

    init {
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(clientId)
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
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

    fun authUserByCredentials(googleCredential: SignInCredential) {
        viewModelScope.launch {
            val idToken = googleCredential.googleIdToken
            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
            authRepository.loginWithCredential(firebaseCredential)
        }
    }
}