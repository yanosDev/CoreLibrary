@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.firestorewrapper.ui

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.identity.SignInClient
import de.yanos.corelibrary.ui.theme.SonayPreviews
import de.yanos.crashlog.util.Clog
import de.yanos.firestorewrapper.R
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
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
        val username = remember { mutableStateOf(TextFieldValue()) }
        val password = remember { mutableStateOf(TextFieldValue()) }

        Text(
            text = stringResource(id = R.string.login),
            style = MaterialTheme.typography.headlineLarge.copy(
                shadow = Shadow(
                    color = MaterialTheme.colorScheme.onBackground,
                    offset = Offset(5f, 5f),
                    blurRadius = 15f
                ),
                fontStyle = FontStyle.Italic
            ),
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Username") },
            value = username.value,
            onValueChange = { username.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        TextField(
            label = { Text(text = "Password") },
            value = password.value,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            onValueChange = { password.value = it })

        Spacer(modifier = Modifier.height(20.dp))
        Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
            Button(
                onClick = { },
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(text = "Login")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        ClickableText(
            text = AnnotatedString("Forgot password?"),
            onClick = { },
            style = MaterialTheme.typography.bodyMedium
        )
    }
    Button(
        onClick = signInClick,
        modifier = Modifier.padding(16.dp),
        shape = RoundedCornerShape(6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    ) {
        Image(
            imageVector = Icons.Rounded.Login,
            contentDescription = "",
            colorFilter = ColorFilter.tint(color = MaterialTheme.colorScheme.primary)
        )
        Text(text = stringResource(id = R.string.btn_login_google), modifier = Modifier.padding(6.dp))
    }
}

@Composable
private fun SignOffScreen(modifier: Modifier = Modifier, signOffClick: () -> Unit = {}) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = signOffClick,
            modifier = Modifier.padding(16.dp),
            shape = RoundedCornerShape(6.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        ) {
            Icon(
                imageVector = Icons.Rounded.Logout,
                contentDescription = "",
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(text = stringResource(id = R.string.btn_logout), modifier = Modifier.padding(6.dp))
        }
    }
}