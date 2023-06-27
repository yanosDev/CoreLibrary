package de.yanos.libraries.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import de.yanos.domain.repository.AuthenticationRepository
import de.yanos.firestorewrapper.domain.AuthResult
import kotlinx.coroutines.launch

internal class AuthViewModel(
    clientId: String,
    private val authRepository: AuthenticationRepository
) : ViewModel() {

    internal val signInRequest: BeginSignInRequest
    internal val signUpRequest: BeginSignInRequest
    var userIsLoggedIn: Boolean by mutableStateOf(false)
    var userState: AuthResult by mutableStateOf(AuthResult.LoggedOut)
    private var id: String? = null
    private var pwd: String? = null

    init {
        viewModelScope.launch {
            id?.let {
                userIsLoggedIn = authRepository.getToken(it, pwd)
            }
        }
        signUpRequest = BeginSignInRequest.Builder()
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
        signInRequest = BeginSignInRequest.Builder()
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

    fun authUserByCredentials(googleCredential: SignInCredential) {
        viewModelScope.launch {
            googleCredential.googleIdToken?.let { token ->
                val result = authRepository.signInGoogle(googleCredential.id, token)
                setStateFromResult(if (result) AuthResult.SignIn(googleCredential.id) else AuthResult.Failure(""))
            }
        }
    }

    fun registerUser(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.registerPasswordUser(email, password)
            setStateFromResult(if (result) AuthResult.SignIn(email) else AuthResult.Failure(""))
        }
    }

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            val result = authRepository.signInPasswordUser(email, password)
            setStateFromResult(if (result) AuthResult.SignIn(email) else AuthResult.Failure(""))
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            authRepository.signOut()
            userState = AuthResult.LoggedOut
            userIsLoggedIn = false
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            val result = authRepository.requestPasswordChangeEmail(email)
            setStateFromResult(if (result) AuthResult.PasswordResetSent else AuthResult.Failure(""))
        }
    }

    private fun setStateFromResult(result: AuthResult) {
        userIsLoggedIn = result is AuthResult.SignIn
        userState = result
    }
}
