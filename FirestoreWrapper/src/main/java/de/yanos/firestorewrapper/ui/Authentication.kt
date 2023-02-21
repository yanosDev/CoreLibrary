package de.yanos.firestorewrapper.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import de.yanos.corelibrary.ui.theme.AppTheme
import de.yanos.corelibrary.ui.theme.SonayPreviews


class AuthenticationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme(this) { config ->
                AuthScreen()
            }
        }
    }
}

@SonayPreviews
@Composable
fun AuthScreen() {
    Text(text = "Test")
}