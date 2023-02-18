package de.yanos.core

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.navigation.compose.rememberNavController
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.core.utils.*
import de.yanos.corelibrary.R

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            AppTheme(activity = this) { config ->
                val navController = rememberNavController()
                DynamicNavigationScreen(config = config, destinations = TEST_DESTINATIONS, navController = navController) {
                    
                }
            }
        }
    }
}

object TestRoutes {
    const val ONE = "Inbox"
    const val TWO = "Notes"
    const val THREE = "Conversations"
    const val FOUR = "Settings"
}

val TEST_DESTINATIONS = listOf(
    NavigationDestination.TopDestination(
        route = TestRoutes.ONE,
        selectedIcon = Icons.Default.Inbox,
        unselectedIcon = Icons.Default.Inbox,
        iconTextId = R.string.tab_inbox
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.TWO,
        selectedIcon = Icons.Default.Notes,
        unselectedIcon = Icons.Default.Notes,
        iconTextId = R.string.tab_note
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.THREE,
        selectedIcon = Icons.Default.PhoneInTalk,
        unselectedIcon = Icons.Default.PhoneInTalk,
        iconTextId = R.string.tab_conversation
    ),
    NavigationDestination.TopDestination(
        route = TestRoutes.FOUR,
        selectedIcon = Icons.Default.Settings,
        unselectedIcon = Icons.Default.Settings,
        iconTextId = R.string.tab_settings
    ),
)