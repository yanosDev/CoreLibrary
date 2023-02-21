package de.yanos.firestorewrapper.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import de.yanos.corelibrary.ui.theme.AppTheme
import de.yanos.corelibrary.ui.theme.SonayPreviews
import de.yanos.crashlog.util.Clog
import kotlinx.coroutines.launch

class AuthenticationActivity : ComponentActivity() {
    private lateinit var oneTapClient: SignInClient
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        oneTapClient = Identity.getSignInClient(this)
        setContent {
            AppTheme(this) { config ->
                val scope = rememberCoroutineScope()
                val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
                    if (result.resultCode == Activity.RESULT_OK) {
                        authViewModel.authUserByCredentials(oneTapClient.getSignInCredentialFromIntent(result.data))
                    }
                }
                AuthScreen(onClick = {
                    scope.launch {
                        oneTapClient.beginSignIn(authViewModel.signInRequest)
                            .addOnSuccessListener { result ->
                                launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                            }
                            .addOnFailureListener {
                                oneTapClient.beginSignIn(authViewModel.signUpRequest)
                                    .addOnSuccessListener { result ->
                                        launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                                    }
                                    .addOnFailureListener { Clog.e("Registration Failed") }
                            }
                    }
                })
            }
        }
    }

    private fun handleSignInResult(activityResult: ActivityResult) {

    }
}


@SonayPreviews
@Composable
fun AuthScreen(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(modifier = modifier.fillMaxSize()) {
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            )
        ) {
            Image(
                painter = painterResource(id = com.google.android.gms.base.R.drawable.common_full_open_on_phone),
                contentDescription = ""
            )
            Text(text = "Sign in with Google", modifier = Modifier.padding(6.dp))
        }
    }
}