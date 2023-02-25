package de.yanos.libraries.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import de.yanos.firestorewrapper.domain.AuthRepository
import de.yanos.firestorewrapper.domain.AuthResult
import kotlinx.coroutines.launch

internal class AuthViewModel(
    clientId: String,
    private val authRepository: AuthRepository
) : ViewModel() {

    internal val signInRequest: BeginSignInRequest
    internal val signUpRequest: BeginSignInRequest
    var userIsLoggedIn: Boolean by mutableStateOf(false)
    var userState: AuthResult by mutableStateOf(AuthResult.LoggedOut)

    init {
        viewModelScope.launch {
            userIsLoggedIn = authRepository.isLoggedIn()
        }
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
            val result = authRepository.loginWithCredential(googleCredential.googleIdToken)
            setStateFromResult(result)
        }
    }

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            val result = if (authRepository.userIsRegistered(email))
                authRepository.loginPasswordUser(email, password)
            else
                authRepository.createPasswordUser(email, password)
            setStateFromResult(result)
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            authRepository.logOutUser()
            userState = AuthResult.LoggedOut
            userIsLoggedIn = false
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            val result = authRepository.sendPasswordResetEmail(email)
            setStateFromResult(result)
        }
    }

    private fun setStateFromResult(result: AuthResult) {
        userIsLoggedIn = result is AuthResult.SignIn
        userState = result
    }
}
