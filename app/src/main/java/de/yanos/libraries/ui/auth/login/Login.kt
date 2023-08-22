package de.yanos.libraries.ui.auth.login

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import de.yanos.data.model.user.User


@Composable
internal fun LoginScreen(
    modifier: Modifier = Modifier,
    user: User = User("", "", "", ""),
    onUserChanged: (User) -> Unit = {},
    continueAfterLogin: () -> Unit = {},
    startRegistration: () -> Unit = {},
) {


}

