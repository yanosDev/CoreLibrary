package de.yanos.firestorewrapper.ui

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import de.yanos.corelibrary.ui.theme.AppTheme
import de.yanos.corelibrary.ui.theme.SonayPreviews
import de.yanos.crashlog.util.Clog
import de.yanos.firestorewrapper.R
import de.yanos.firestorewrapper.domain.AuthRepositoryBuilder
import kotlinx.coroutines.launch

@Composable
fun AuthView(modifier: Modifier = Modifier, clientId: String, oneTapClient: SignInClient) {
    val scope = rememberCoroutineScope()
    val authViewModel = AuthViewModel(clientId, AuthRepositoryBuilder.Builder().build())
    val registerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.authUserByCredentials(oneTapClient.getSignInCredentialFromIntent(result.data))
        }
    }
    val loginLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.authUserByCredentials(oneTapClient.getSignInCredentialFromIntent(result.data))
        }
    }
    AuthScreen(modifier = modifier.fillMaxSize(), isUserLoggedIn = authViewModel.userIsLoggedIn, onClick = {
        scope.launch {
            oneTapClient.beginSignIn(authViewModel.signInRequest)
                .addOnSuccessListener { result ->
                    loginLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                }
                .addOnFailureListener {
                    oneTapClient.beginSignIn(authViewModel.signUpRequest)
                        .addOnSuccessListener { result ->
                            registerLauncher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                        }
                        .addOnFailureListener { Clog.e("Registration Failed") }
                }
        }
    })
}

@SonayPreviews
@Preview
@Composable
private fun AuthScreen(modifier: Modifier = Modifier, isUserLoggedIn: Boolean = true, onClick: () -> Unit = {}) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
    ) {
        if (!isUserLoggedIn)
            Button(
                onClick = onClick,
                modifier = Modifier
                    .align(Alignment.Bottom)
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