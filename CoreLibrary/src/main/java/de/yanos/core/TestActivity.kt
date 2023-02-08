package de.yanos.core

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.utils.ScreenConfig

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            AppTheme(activity = this) { config ->
                ContentTest(Modifier, config)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun ContentTest(
    modifier: Modifier = Modifier,
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf())
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(32.dp),
            text = "Test Environment",
            style = MaterialTheme.typography.titleMedium
        )
    }
}