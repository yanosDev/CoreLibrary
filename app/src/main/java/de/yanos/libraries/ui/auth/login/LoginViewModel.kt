package de.yanos.libraries.ui.auth.login

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.yanos.libraries.util.prefs.AppSettings
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val appSettings: AppSettings
) : ViewModel() {
}