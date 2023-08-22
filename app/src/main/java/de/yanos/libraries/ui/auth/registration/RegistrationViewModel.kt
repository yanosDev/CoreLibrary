package de.yanos.libraries.ui.auth.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.SignInRequest
import de.yanos.core.utils.SignUpRequest
import de.yanos.core.utils.isEmail
import de.yanos.core.utils.isName
import de.yanos.core.utils.isPassword
import de.yanos.data.model.user.User
import de.yanos.data.service.auth.AuthRepository
import de.yanos.data.util.LoadState
import de.yanos.libraries.util.prefs.AppSettings
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val appSettings: AppSettings,
    private val repo: AuthRepository,
    @SignInRequest val signIn: BeginSignInRequest,
    @SignUpRequest val signUp: BeginSignInRequest,
) : ViewModel() {
    var state by mutableStateOf<RegistrationState>(RegistrationState.Idle)

    fun startRegisteringUser(email: String, password: String, lastName: String, firstName: String) {
        viewModelScope.launch {
            if (email.isEmail()
                && password.isPassword()
                && firstName.isName()
                && lastName.isName()
            ) {
                state = RegistrationState.RegistrationInProgress
                val ts = System.currentTimeMillis()
                val result = repo.register(email, password, lastName, firstName)
                val diff = System.currentTimeMillis() - ts
                if (diff < 3000)
                    delay(diff)
                setResult(result)
            } else {
                state = RegistrationState.DataCheckFailed
            }
        }
    }

    fun authUserByCredentials(googleCredential: SignInCredential) {
        viewModelScope.launch {
            googleCredential.googleIdToken?.let { token ->
                val result = repo.signInGoogle(googleCredential.id, token)
                setResult(result)
            }
        }
    }

    private fun setResult(result: LoadState<User>) {
        state = (result as? LoadState.Data)?.data?.let { user ->
            appSettings.userId = user.id

            RegistrationState.RegistrationSuccessfull
        } ?: RegistrationState.RegistrationFailed((result as? LoadState.Failure)?.e)
    }
}

sealed interface RegistrationState {
    object Idle : RegistrationState
    object RegistrationInProgress : RegistrationState
    object RegistrationSuccessfull : RegistrationState
    class RegistrationFailed(val e: Exception?) : RegistrationState
    object DataCheckFailed : RegistrationState

}