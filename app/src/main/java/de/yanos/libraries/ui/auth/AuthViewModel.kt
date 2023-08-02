package de.yanos.libraries.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.GoogleClientId
import de.yanos.data.model.user.User
import de.yanos.data.service.auth.AuthRepository
import de.yanos.data.util.LoadState
import de.yanos.libraries.util.prefs.AppSettings
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val repo: AuthRepository,
    @GoogleClientId private val clientId: String
) : ViewModel() {

    internal val signInRequest: BeginSignInRequest
    internal val signUpRequest: BeginSignInRequest
    var userState: AuthUIState by mutableStateOf(AuthUIState.SignedOut)

    init {
        viewModelScope.launch {
            val user = repo.loadUser(appSettings.userId)
            if (user != null)
                userState = AuthUIState.LoggedIn(user)

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
                val result = repo.signInGoogle(googleCredential.id, token)
                (result as? LoadState.Data)?.data?.let { user ->
                    userState = AuthUIState.LoggedIn(user)
                } ?: (result as? LoadState.Failure)?.e?.let { userState = AuthUIState.AuthFailed(it) }
            }
        }
    }

    fun registerUser(email: String, password: String, lastName: String, firstName: String) {
        viewModelScope.launch {
            val result = repo.register(email, password, lastName, firstName)
            (result as? LoadState.Data)?.data?.let { user ->
                userState = AuthUIState.Registered(user)
            } ?: (result as? LoadState.Failure)?.e?.let { userState = AuthUIState.AuthFailed(it) }
        }
    }

    fun signInUser(email: String, password: String) {
        viewModelScope.launch {
            val result = repo.signIn(email, password)
            (result as? LoadState.Data)?.data?.let { user ->
                userState = AuthUIState.LoggedIn(user)
            } ?: (result as? LoadState.Failure)?.e?.let { userState = AuthUIState.AuthFailed(it) }
        }
    }

    fun signOutUser() {
        viewModelScope.launch {
            val result = repo.signOut()
            (result as? LoadState.Data)?.data?.let { hasSucceeded ->
                if (hasSucceeded) {
                    userState = AuthUIState.SignedOut
                }
            } ?: run { userState = AuthUIState.AuthFailed(Exception("Couldn't sign out")) }
        }
    }

    fun requestPasswordReset(email: String) {
        viewModelScope.launch {
            val result = repo.resetPassword(email, "newPWD")
            (result as? LoadState.Data)?.data?.let { user ->
                userState = AuthUIState.PasswordRequested(user)
            } ?: (result as? LoadState.Failure)?.e?.let {
                userState = AuthUIState.AuthFailed(it)
            }
        }
    }
}

sealed interface AuthUIState {
    data class Registered(val user: User) : AuthUIState
    data class LoggedIn(val user: User) : AuthUIState
    data class PasswordRequested(val user: User) : AuthUIState
    data class AuthFailed(val error: Exception) : AuthUIState
    object SignedOut : AuthUIState
}
