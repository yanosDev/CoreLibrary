package de.yanos.firestorewrapper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import de.yanos.corelibrary.ui.theme.AppTheme
import de.yanos.corelibrary.ui.theme.SonayPreviews
import de.yanos.firestorewrapper.R

class AuthenticationActivity : ComponentActivity() {
    lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            .build()
        setContent {
            AppTheme(this) { config ->
                AuthScreen()
            }
        }
    }
}


@SonayPreviews
@Composable
fun AuthScreen(modifier: Modifier = Modifier) {
    Text(text = "Test")
}