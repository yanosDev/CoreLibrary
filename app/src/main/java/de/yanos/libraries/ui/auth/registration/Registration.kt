package de.yanos.libraries.ui.auth.registration

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material.icons.rounded.WifiTetheringError
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.google.android.gms.auth.api.identity.Identity
import de.yanos.core.R
import de.yanos.core.ui.theme.SonayPreviews
import de.yanos.core.ui.view.CustomDialog
import de.yanos.core.ui.view.DividerText
import de.yanos.core.ui.view.EmailInput
import de.yanos.core.ui.view.LabelMedium
import de.yanos.core.ui.view.LabelSmall
import de.yanos.core.ui.view.NameInput
import de.yanos.core.ui.view.PasswordInput
import de.yanos.data.model.user.User
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
@SonayPreviews
internal fun RegisterScreen(
    modifier: Modifier = Modifier,
    vm: RegistrationViewModel = hiltViewModel(),
    user: User = User("", "", "", ""),
    onUserChanged: (User) -> Unit = {},
    continueAfterRegistration: () -> Unit = {},
    startLogin: () -> Unit = {},
) {
    when (vm.state) {
        RegistrationState.RegistrationSuccessfull -> continueAfterRegistration()
        is RegistrationState.RegistrationFailed -> {
            val clearError = { vm.state = RegistrationState.Idle }
            CustomDialog(
                icon = Icons.Rounded.WifiTetheringError,
                title = stringResource(id = R.string.register_error_server_title),
                text = "${stringResource(id = R.string.register_error_server_text)}: \n ${(vm.state as RegistrationState.RegistrationFailed).e}",
                onConfirm = clearError,
                onDismiss = clearError,
                showCancel = false
            )
        }

        RegistrationState.DataCheckFailed -> {
            val callback = { vm.state = RegistrationState.Idle }
            CustomDialog(
                title = stringResource(id = R.string.register_error_invalid_title),
                text = stringResource(id = R.string.register_error_invalid_text),
                onConfirm = callback,
                onDismiss = callback,
                showCancel = false
            )
        }

        else -> {}
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Column(
            modifier = modifier
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 32.dp)
            ) {
                RegistrationHeader(modifier = Modifier.wrapContentWidth())
                RegistrationBackground(modifier = Modifier.height(72.dp))
            }
            AnimatedVisibility(
                visible = vm.state == RegistrationState.RegistrationInProgress,
                enter = fadeIn(),
            ) {
                RegistrationProgressing()
            }
            AnimatedVisibility(
                visible = vm.state != RegistrationState.RegistrationInProgress,
                enter = fadeIn(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    RegistrationPassword(vm = vm, user = user)
                    RegistrationSocial(vm = vm)
                }
            }
        }
        RegistrationToLogin(startLogin = startLogin)
    }
}


@Composable
private fun RegistrationToLogin(modifier: Modifier = Modifier, startLogin: () -> Unit = {}) {
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
        LabelSmall(text = R.string.register_to_login_desc)
        TextButton(onClick = startLogin) {
            LabelMedium(text = R.string.register_sign_in)
        }
    }
}

@Composable
private fun RegistrationPassword(
    modifier: Modifier = Modifier,
    vm: RegistrationViewModel,
    user: User,
    onUserChanged: (User) -> Unit = {},
) {
    var firstName by remember { mutableStateOf(TextFieldValue(user.firstName)) }
    var lastName by remember { mutableStateOf(TextFieldValue(user.firstName)) }
    var id by remember { mutableStateOf(TextFieldValue(user.firstName)) }
    var password by remember { mutableStateOf(TextFieldValue(user.firstName)) }
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        NameInput(
            label = { LabelMedium(R.string.register_first_name) },
            value = firstName,
            onValueChange = {
                firstName = it
                onUserChanged(user.copy(firstName = it.text))
            })
        Spacer(modifier = Modifier.height(8.dp))
        NameInput(
            label = { LabelMedium(R.string.register_last_name) },
            value = lastName,
            onValueChange = {
                lastName = it
                onUserChanged(user.copy(lastName = it.text))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        EmailInput(
            label = { LabelMedium(R.string.register_email) },
            value = id,
            onValueChange = {
                id = it
                onUserChanged(user.copy(id = it.text))
            }
        )
        Spacer(modifier = Modifier.height(8.dp))
        PasswordInput(
            label = { LabelMedium(text = R.string.register_password) },
            value = password,
            onValueChange = {
                password = it
                onUserChanged(user.copy(password = it.text))
            }
        )
        Spacer(modifier = Modifier.height(32.dp))
        ElevatedButton(
            onClick = {
                vm.startRegisteringUser(
                    email = id.text,
                    password = password.text,
                    firstName = firstName.text,
                    lastName = lastName.text
                )
            },
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.AppRegistration,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.register_new_user),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun RegistrationProgressing(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_registering))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = modifier.height(512.dp),
        composition = composition,
        progress = { progress },
    )
}

@Composable
private fun RegistrationHeader(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = stringResource(id = R.string.register_intro),
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = stringResource(id = R.string.register_account),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun RegistrationSocial(
    modifier: Modifier = Modifier,
    vm: RegistrationViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val client = Identity.getSignInClient(LocalContext.current)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            vm.authUserByCredentials(client.getSignInCredentialFromIntent(result.data))
        }
    }
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DividerText(text = R.string.register_or)
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                scope.launch {
                    client.beginSignIn(vm.signIn)
                        .addOnSuccessListener { result ->
                            launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                        }
                        .addOnFailureListener {
                            client.beginSignIn(vm.signUp)
                                .addOnSuccessListener { result ->
                                    launcher.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
                                }
                                .addOnFailureListener { Timber.e("Registration Failed") }
                        }
                }
            },
            modifier = Modifier.height(50.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Login,
                contentDescription = ""
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = stringResource(id = R.string.register_google),
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun RegistrationBackground(modifier: Modifier = Modifier) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.lottie_android_icon))
    val progress by animateLottieCompositionAsState(composition)
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress },
    )
}