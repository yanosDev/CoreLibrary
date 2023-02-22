package de.yanos.firestorewrapper.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.android.gms.auth.api.identity.SignInClient
import de.yanos.corelibrary.ui.theme.SonayPreviews
import de.yanos.crashlog.util.Clog
import de.yanos.firestorewrapper.domain.AuthRepositoryBuilder
import de.yanos.firestorewrapper.domain.AuthResult
import kotlinx.coroutines.launch

@Composable
fun AuthView(modifier: Modifier = Modifier, clientId: String, oneTapClient: SignInClient, onUserStateChange: (AuthResult) -> Unit) {
    val scope = rememberCoroutineScope()
    val authViewModel = AuthViewModel(clientId, AuthRepositoryBuilder.Builder().build())
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.authUserByCredentials(oneTapClient.getSignInCredentialFromIntent(result.data))
        }
    }
    AuthScreen(
        modifier = modifier,
        isUserLoggedIn = authViewModel.userIsLoggedIn,
        signIn = {
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
        },
        signOff = {
            scope.launch {
                authViewModel.signOutUser()
            }
        }
    )
}

@SonayPreviews
@Preview
@Composable
private fun AuthScreen(modifier: Modifier = Modifier, isUserLoggedIn: Boolean = true, signIn: () -> Unit = {}, signOff: () -> Unit = {}) {
    if (!isUserLoggedIn)
        SignInScreen(modifier = modifier, signInClick = signIn)
    else SignOffScreen(modifier = modifier, signOffClick = signOff)
}

@Composable
private fun SignInScreen(modifier: Modifier = Modifier, signInClick: () -> Unit = {}) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = signInClick,
            modifier = Modifier.padding(16.dp),
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

@Composable
private fun SignOffScreen(modifier: Modifier = Modifier, signOffClick: () -> Unit = {}) {
    Column(modifier = modifier, verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = signOffClick,
            modifier = Modifier.padding(16.dp),
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
            Text(text = "Sign Off with Google", modifier = Modifier.padding(6.dp))
        }
    }
}