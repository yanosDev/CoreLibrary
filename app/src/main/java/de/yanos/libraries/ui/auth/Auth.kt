@file:OptIn(ExperimentalMaterial3Api::class)

package de.yanos.libraries.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import de.yanos.core.R
import de.yanos.data.model.user.User
import de.yanos.libraries.ui.auth.login.LoginScreen
import de.yanos.libraries.ui.auth.registration.RegisterScreen
import kotlinx.coroutines.launch

@Composable
fun AuthView(
    modifier: Modifier = Modifier,
    vm: AuthViewModel = hiltViewModel(),
    onUserStateChange: (AuthUIState) -> Unit
) {
    val scope = rememberCoroutineScope()
    onUserStateChange(vm.userState)
    val authExecutor = { authAction: AuthAction ->
        scope.launch {
            when (authAction) {
                is GoogleSignIn -> {

                }

                is EmailSignIn -> vm.signInUser(authAction.email, authAction.password)
                is SignOut -> {
                    vm.signOutUser()
                }

                is PasswordReset -> vm.requestPasswordReset(authAction.email)
            }
        }
        Unit
    }
    when (vm.userState) {
        AuthUIState.Register -> RegisterScreen(
            modifier = modifier,
            user = vm.user,
            onUserChanged = { vm.user = it },
            continueAfterRegistration = {
                vm.checkUserAuthState()
            },
            startLogin = { vm.userState = AuthUIState.Login }
        )

        AuthUIState.Login -> LoginScreen(
            modifier = modifier,
            user = vm.user,
            onUserChanged = { vm.user = it },
            continueAfterLogin = { vm.checkUserAuthState() },
            startRegistration = { vm.userState = AuthUIState.Register }
        )

        else -> SignOffScreen(modifier = modifier, authExecutor = authExecutor)
    }
}


@Composable
private fun SignInScreen(modifier: Modifier = Modifier, authExecutor: (AuthAction) -> Unit) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(32.dp),
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var username by remember { mutableStateOf(TextFieldValue()) }
        var password by remember { mutableStateOf(TextFieldValue()) }
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.welcome),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = stringResource(id = R.string.signIn_to_continue),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TextField(
                label = { Text(text = stringResource(id = R.string.name)) },
                value = username,
                onValueChange = { username = it })
            Spacer(modifier = Modifier.height(20.dp))
            TextField(
                label = { Text(text = stringResource(id = R.string.password)) },
                value = password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password = it }
            )
            Column(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .align(Alignment.End)
            ) {
                if (username.text.isNotBlank())
                    ClickableText(
                        text = AnnotatedString(stringResource(id = R.string.forgot_password)),
                        onClick = { authExecutor(PasswordReset(email = username.text)) },
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurface),
                    )
                else Text(
                    modifier = modifier.alpha(0.6f),
                    text = stringResource(id = R.string.forgot_password),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(
                    enabled = username.text.isNotEmpty() && password.text.isNotEmpty(),
                    onClick = { authExecutor(EmailSignIn(username.text, password.text)) },
                    modifier = Modifier.height(50.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Login,
                        contentDescription = ""
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(id = R.string.signIn),
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.signIn_alternative),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { authExecutor(GoogleSignIn) },
                modifier = Modifier.height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Login,
                    contentDescription = ""
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(id = R.string.btn_login_google),
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }
    }
}

@Composable
private fun SignOffScreen(modifier: Modifier = Modifier, authExecutor: (AuthAction) -> Unit) {
    Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly, horizontalAlignment = Alignment.CenterHorizontally) {
        Button(
            onClick = { authExecutor(SignOut) },
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

private sealed interface AuthAction
private object GoogleSignIn : AuthAction
private object SignOut : AuthAction
private class PasswordReset(val email: String) : AuthAction
private class EmailSignIn(val email: String, val password: String) : AuthAction