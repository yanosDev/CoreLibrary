package de.yanos.core

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.ui.view.DynamicNavigationScreen
import de.yanos.core.utils.NavigationDestination
import de.yanos.corelibrary.R

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("Hi", "Sonay")
        setContent {
            AppTheme(activity = this) { config ->
                val navController = rememberNavController()
                DynamicNavigationScreen(config = config, destinations = TEST_DESTINATIONS, navController = navController) { modifier ->
                    //NavHost Here
                    TestNavHost(
                        modifier = modifier,
                        startRoute = TEST_DESTINATIONS.first().route,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
private fun TestNavHost(
    modifier: Modifier = Modifier,
    startRoute: String,
    navController: NavHostController
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startRoute,
    ) {
        composable(TestRoutes.ONE) {
            Text(text = TestRoutes.ONE)
        }
        composable(TestRoutes.TWO) {
            Text(text = TestRoutes.TWO)
        }
        composable(TestRoutes.THREE) {
            Text(text = TestRoutes.THREE)
        }
        composable(TestRoutes.FOUR) {
            Text(text = TestRoutes.FOUR)
        }
    }
}

object TestRoutes {
    internal const val ONE = "Inbox"
    internal const val TWO = "Notes"
    internal const val THREE = "Conversations"
    internal const val FOUR = "Settings"
}

private val TEST_DESTINATIONS = listOf(
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
