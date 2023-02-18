package de.yanos.core.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MenuOpen
import androidx.compose.material3.*
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.offset
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import de.yanos.core.TEST_DESTINATIONS
import de.yanos.core.utils.*
import de.yanos.corelibrary.R
import kotlinx.coroutines.launch

@Composable
fun DynamicNavigationScreen(
    modifier: Modifier = Modifier,
    config: ScreenConfig,
    destinations: List<NavigationDestination.TopDestination>,
    navController: NavHostController,
    content: @Composable () -> Unit,
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: destinations.first().route
    val navigationAction = remember(navController) { NavigationActions(navController) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (config.navigationType == NavigationType.DRAWER) {
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentNavigationDrawerContent(
                    config = config,
                    route = currentRoute,
                    destinations = destinations,
                    navigateToTopLevelDestination = navigationAction::navigateTo,
                )
            },
            content = {
                DynamicContent(
                    config = config,
                    route = currentRoute,
                    navigateToTopLevelDestination = navigationAction::navigateTo,
                    content = content
                )
            }
        )
    } else {
        val drawerClickListener = {
            scope.launch {
                drawerState.close()
            }
            Unit
        }
        ModalNavigationDrawer(
            drawerContent = {
                ModalNavigationDrawerContent(
                    config = config,
                    route = currentRoute,
                    destinations = destinations,
                    navigateToTopLevelDestination = navigationAction::navigateTo,
                    onDrawerClicked = drawerClickListener
                )
            },
            drawerState = drawerState,
            content = {
                DynamicContent(
                    config = config,
                    route = currentRoute,
                    onDrawerClicked = drawerClickListener,
                    navigateToTopLevelDestination = navigationAction::navigateTo,
                    content = content
                )
            }
        )
    }
}

@Composable
fun DynamicContent(
    modifier: Modifier = Modifier,
    config: ScreenConfig,
    route: String,
    onDrawerClicked: () -> Unit = {},
    navigateToTopLevelDestination: (NavigationDestination.TopDestination) -> Unit,
    content: @Composable () -> Unit
) {
    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = config.navigationType == NavigationType.RAIL) {
            DynamicRail(
                selectedDestination = route,
                config = config,
                onDrawerClicked = onDrawerClicked,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.inverseOnSurface)
        ) {
            content()
            AnimatedVisibility(visible = config.navigationType == NavigationType.BOTTOM) {
                DynamicBottomBar(
                    selectedDestination = route,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}

@Composable
fun DynamicBottomBar(selectedDestination: String, navigateToTopLevelDestination: (NavigationDestination.TopDestination) -> Unit) {

}

@Composable
fun DynamicRail(selectedDestination: String, config: ScreenConfig, onDrawerClicked: () -> Unit) {

}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun PermanentNavigationDrawerContent(
    modifier: Modifier = Modifier,
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf()),
    route: String = TEST_DESTINATIONS.first().route,
    destinations: List<NavigationDestination.TopDestination>,
    navigateToTopLevelDestination: (NavigationDestination) -> Unit,
) {
    PermanentDrawerSheet(modifier = modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp)) {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modifier = Modifier
                            .padding(16.dp),
                        text = stringResource(id = R.string.app_name).uppercase(),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    ExFab(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                    )
                }
                DrawerItems(route = route, destinations = destinations, navigateToTopLevelDestination = navigateToTopLevelDestination)
            },
            measurePolicy = { m, c -> measurePolicy(config = config, measurableList = m, constraints = c) }
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun ModalNavigationDrawerContent(
    modifier: Modifier = Modifier,
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf()),
    route: String,
    destinations: List<NavigationDestination.TopDestination>,
    navigateToTopLevelDestination: (NavigationDestination) -> Unit,
    onDrawerClicked: () -> Unit
) {
    ModalDrawerSheet {
        Layout(
            modifier = modifier
                .background(MaterialTheme.colorScheme.inverseOnSurface)
                .padding(16.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.app_name).uppercase(),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = onDrawerClicked) {
                            Icon(
                                imageVector = Icons.Default.MenuOpen,
                                contentDescription = stringResource(id = R.string.d_nav_drawer)
                            )
                        }
                    }
                    ExFab(
                        onClick = {},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 40.dp),
                    )
                }
                DrawerItems(route = route, destinations = destinations, navigateToTopLevelDestination = navigateToTopLevelDestination)
            },
            measurePolicy = { m, c -> measurePolicy(config = config, measurableList = m, constraints = c) }
        )
    }
}

@Composable
private fun DrawerItems(
    modifier: Modifier = Modifier,
    route: String,
    destinations: List<NavigationDestination.TopDestination>,
    navigateToTopLevelDestination: (NavigationDestination) -> Unit
) {
    Column(
        modifier = modifier
            .layoutId(LayoutType.CONTENT)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        destinations.forEach { destination ->
            NavigationDrawerItem(
                selected = route == destination.route,
                label = { Text(text = stringResource(id = destination.iconTextId), modifier = Modifier.padding(horizontal = 16.dp)) },
                icon = { Icon(imageVector = destination.selectedIcon, contentDescription = stringResource(id = destination.iconTextId)) },
                colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent),
                onClick = { navigateToTopLevelDestination(destination) }
            )
        }
    }
}

private fun MeasureScope.measurePolicy(config: ScreenConfig, measurableList: List<Measurable>, constraints: Constraints): MeasureResult {
    lateinit var headerMeasurable: Measurable
    lateinit var contentMeasurable: Measurable
    measurableList.forEach {
        when (it.layoutId) {
            LayoutType.HEADER -> headerMeasurable = it
            LayoutType.CONTENT -> contentMeasurable = it
            else -> error("Unknown layoutId encountered!")
        }
    }

    val headerPlaceable = headerMeasurable.measure(constraints)
    val contentPlaceable = contentMeasurable.measure(constraints.offset(vertical = -headerPlaceable.height))
    return layout(constraints.maxWidth, constraints.maxHeight) {
        headerPlaceable.placeRelative(0, 0)
        val nonContentVerticalSpace = constraints.maxHeight - contentPlaceable.height
        val contentPlaceableY = when (config.contentPosition) {
            ContentPosition.TOP -> 0
            ContentPosition.CENTER -> nonContentVerticalSpace / 2
        }.coerceAtLeast(headerPlaceable.height)
        contentPlaceable.placeRelative(0, contentPlaceableY)
    }
}

enum class LayoutType {
    HEADER, CONTENT
}