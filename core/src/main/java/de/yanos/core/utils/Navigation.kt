package de.yanos.core.utils

import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController


sealed class NavigationDestination(
    open val route: String = "",
    open val argRoutes: String = route,
    open val deeplink: String = "yanos://$route"
) {
    data class TopDestination(
        override val route: String,
        val selectedIcon: ImageVector,
        val unselectedIcon: ImageVector,
        val iconTextId: Int
    ) : NavigationDestination()

    data class SubDestination(
        override val route: String,
        override val argRoutes: String,
    ) : NavigationDestination(route, argRoutes)
}

class NavigationActions(private val navController: NavHostController) {
    fun navigateTo(destination: NavigationDestination) {
        navController.navigate(destination.route.toString()) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
            restoreState = true
        }
    }
}