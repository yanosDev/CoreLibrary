package de.yanos.core

import android.os.Bundle
import android.os.PersistableBundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import de.yanos.core.ui.theme.AppTheme
import de.yanos.core.utils.NavigationActions
import de.yanos.core.utils.NavigationDestination
import de.yanos.core.utils.NavigationType
import de.yanos.core.utils.ScreenConfig
import de.yanos.corelibrary.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TestActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContent {
            AppTheme(activity = this) { config ->
                ContentTest(config = config)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun ContentTest(
    modifier: Modifier = Modifier,
    extras: Bundle? = Bundle(),
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf())
) {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val selectedDestination = navBackStackEntry?.destination?.route ?: TestRoutes.ONE
    val navigationAction = remember(navController) { NavigationActions(navController) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)

    if (config.navigationType == NavigationType.DRAWER) {
        PermanentNavigationDrawer(drawerContent = {
            PermanentNavigationDrawerContent(
                config = config,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationAction::navigateTo,
            )
        }) {

        }
    } else {
        ModalNavigationDrawer(drawerContent = {
            ModalNavigationDrawerContent(
                config = config,
                selectedDestination = selectedDestination,
                navigateToTopLevelDestination = navigationAction::navigateTo,
                onDrawerClicked = {
                    scope.launch {
                        drawerState.close()
                    }
                }
            )
        }) {

        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun ModalNavigationDrawerContent(
    selectedDestination: String,
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf()),
    navigateToTopLevelDestination: (NavigationDestination) -> Unit,
    onDrawerClicked: () -> Unit = {}
) {
    TODO("Not yet implemented")
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun PermanentNavigationDrawerContent(
    selectedDestination: String,
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf()),
    navigateToTopLevelDestination: (NavigationDestination) -> Unit,
) {

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