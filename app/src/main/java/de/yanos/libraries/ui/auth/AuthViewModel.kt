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

    var userState: AuthUIState by mutableStateOf(AuthUIState.Register)

    init {
        checkUserAuthState()
    }

    fun checkUserAuthState() {
        viewModelScope.launch {
            val user = repo.loadUser(appSettings.userId)
            if (user != null)
                userState = AuthUIState.LoggedIn(user)

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
    object Profile : AuthUIState
    object Login : AuthUIState
    object Register : AuthUIState
    object Password : AuthUIState
    data class Registered(val user: User) : AuthUIState
    data class LoggedIn(val user: User) : AuthUIState
    data class PasswordRequested(val user: User) : AuthUIState
    data class AuthFailed(val error: Exception) : AuthUIState
    object SignedOut : AuthUIState
}
