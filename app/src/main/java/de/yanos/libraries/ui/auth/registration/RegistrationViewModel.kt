package de.yanos.libraries.ui.auth.registration

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.core.utils.GoogleClientId
import de.yanos.core.utils.SignInRequest
import de.yanos.core.utils.SignUpRequest
import de.yanos.data.service.auth.AuthRepository
import de.yanos.data.util.LoadState
import de.yanos.libraries.ui.auth.AuthUIState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistrationViewModel @Inject constructor(
    private val repo: AuthRepository,
    @SignInRequest val signIn: BeginSignInRequest,
    @SignUpRequest val signUp: BeginSignInRequest,
) : ViewModel() {
    var isRegistrationDone: Boolean by mutableStateOf(false)
        private set
    var error: String? by mutableStateOf(null)

    fun registerUser(email: String, password: String, lastName: String, firstName: String) {
        viewModelScope.launch {
            val result = repo.register(email, password, lastName, firstName)
            setResult(result)
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

    private fun <T> setResult(result: LoadState<T>) {
        isRegistrationDone = (result as? LoadState.Data)?.data != null
        (result as? LoadState.Failure)?.e?.let { error = it.message }
    }
}