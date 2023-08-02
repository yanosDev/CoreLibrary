package de.yanos.libraries.ui.auth.registration

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AppRegistration
import androidx.compose.material.icons.rounded.Login
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import de.yanos.core.R
import de.yanos.core.ui.theme.SonayPreviews
import de.yanos.core.ui.view.CustomDialog
import de.yanos.core.ui.view.DividerText
import de.yanos.core.ui.view.EmailInput
import de.yanos.core.ui.view.LabelMedium
import de.yanos.core.ui.view.NameInput
import de.yanos.core.ui.view.PasswordInput
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
@SonayPreviews
internal fun RegisterScreen(
    modifier: Modifier = Modifier,
    vm: RegistrationViewModel = hiltViewModel(),
    onUserRegistered: () -> Unit = {},
    navToLogin: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val client = Identity.getSignInClient(LocalContext.current)
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            vm.authUserByCredentials(client.getSignInCredentialFromIntent(result.data))
        }
    }
    if (vm.isRegistrationDone) {
        onUserRegistered()
    }
    if (vm.error != null) {
        val clearError = { vm.error = null }
        CustomDialog(title = R.string.register_error_title, text = R.string.register_error_title, onConfirm = clearError, onDismiss = clearError)
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var first by remember { mutableStateOf(TextFieldValue()) }
        var last by remember { mutableStateOf(TextFieldValue()) }
        var email by remember { mutableStateOf(TextFieldValue()) }
        var password by remember { mutableStateOf(TextFieldValue()) }
        Column(
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
        Column(
            modifier = Modifier.padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            NameInput(
                label = { LabelMedium(R.string.register_first_name) },
                value = first,
                onValueChange = { first = it })
            Spacer(modifier = Modifier.height(8.dp))
            NameInput(
                label = { LabelMedium(R.string.register_last_name) },
                value = last,
                onValueChange = { last = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            EmailInput(
                label = { LabelMedium(R.string.register_email) },
                value = email,
                onValueChange = { email = it }
            )
            Spacer(modifier = Modifier.height(8.dp))
            PasswordInput(
                label = { LabelMedium(text = R.string.register_password) },
                value = password,
                onValueChange = { password = it }
            )
            Spacer(modifier = Modifier.height(32.dp))
            ElevatedButton(
                onClick = {
                    vm.registerUser(
                        email = email.text,
                        password = password.text,
                        firstName = first.text,
                        lastName = last.text
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
        Column(
            modifier = Modifier.padding(16.dp),
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
}