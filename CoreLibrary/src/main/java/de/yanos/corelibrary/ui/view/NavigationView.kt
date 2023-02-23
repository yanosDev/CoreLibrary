package de.yanos.corelibrary.ui.view

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
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
import de.yanos.corelibrary.R
import de.yanos.corelibrary.utils.*
import kotlinx.coroutines.launch

@Composable
fun DynamicNavigationScreen(
    modifier: Modifier = Modifier,
    config: ScreenConfig,
    destinations: List<NavigationDestination.TopDestination>,
    navController: NavHostController,
    content: @Composable (Modifier) -> Unit
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: destinations.first().route
    val navigationAction = remember(navController) { NavigationActions(navController) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    if (config.navigationType == NavigationType.DRAWER) {
        PermanentNavigationDrawer(
            modifier = modifier,
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
                    destinations = destinations,
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
            modifier = modifier,
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
                    destinations = destinations,
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
    destinations: List<NavigationDestination.TopDestination>,
    onDrawerClicked: () -> Unit = {},
    navigateToTopLevelDestination: (NavigationDestination.TopDestination) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Row(modifier = modifier.fillMaxSize()) {
        AnimatedVisibility(visible = config.navigationType == NavigationType.RAIL) {
            DynamicRail(
                route = route,
                config = config,
                destinations = destinations,
                navigateToTopLevelDestination = navigateToTopLevelDestination,
                onDrawerClicked = onDrawerClicked,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            content(Modifier.weight(1f))
            AnimatedVisibility(visible = config.navigationType == NavigationType.BOTTOM) {
                DynamicBottomBar(
                    route = route,
                    destinations = destinations,
                    navigateToTopLevelDestination = navigateToTopLevelDestination
                )
            }
        }
    }
}

@Composable
fun DynamicBottomBar(
    modifier: Modifier = Modifier,
    route: String,
    destinations: List<NavigationDestination.TopDestination>,
    navigateToTopLevelDestination: (NavigationDestination.TopDestination) -> Unit
) {
    NavigationBar(modifier = modifier.fillMaxWidth(), containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        destinations.forEach { destination ->
            NavigationBarItem(
                selected = route == destination.route,
                onClick = { navigateToTopLevelDestination(destination) },
                icon = {
                    Icon(
                        imageVector = destination.selectedIcon,
                        contentDescription = stringResource(id = destination.iconTextId)
                    )
                }
            )
        }
    }
}

@Composable
fun DynamicRail(
    modifier: Modifier = Modifier,
    config: ScreenConfig, onDrawerClicked: () -> Unit,
    route: String,
    destinations: List<NavigationDestination.TopDestination>,
    navigateToTopLevelDestination: (NavigationDestination.TopDestination) -> Unit,
) {
    NavigationRail(
        modifier = modifier.fillMaxHeight(),
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Layout(
            modifier = Modifier.widthIn(max = 80.dp),
            content = {
                Column(
                    modifier = Modifier.layoutId(LayoutType.HEADER),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    NavigationRailItem(
                        selected = false,
                        onClick = onDrawerClicked,
                        icon = {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = stringResource(id = R.string.d_nav_drawer)
                            )
                        }
                    )
                    FloatingActionButton(
                        onClick = { /*TODO*/ },
                        modifier = Modifier.padding(top = 8.dp, bottom = 32.dp),
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = stringResource(id = R.string.d_edit),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(Modifier.height(8.dp)) // NavigationRailHeaderPadding
                    Spacer(Modifier.height(4.dp)) // NavigationRailVerticalPadding
                }

                Column(
                    modifier = Modifier.layoutId(LayoutType.CONTENT),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    destinations.forEach { destination ->
                        NavigationRailItem(
                            selected = route == destination.route,
                            onClick = { navigateToTopLevelDestination(destination) },
                            icon = {
                                Icon(
                                    imageVector = destination.selectedIcon,
                                    contentDescription = stringResource(id = destination.iconTextId)
                                )
                            }
                        )
                    }
                }
            },
            measurePolicy = { m, c -> measurePolicy(config = config, measurableList = m, constraints = c) }
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
private fun PermanentNavigationDrawerContent(
    modifier: Modifier = Modifier,
    config: ScreenConfig = ScreenConfig(WindowSizeClass.calculateFromSize(DpSize.Unspecified), listOf()),
    route: String,
    destinations: List<NavigationDestination.TopDestination>,
    navigateToTopLevelDestination: (NavigationDestination) -> Unit,
) {
    PermanentDrawerSheet(modifier = modifier.sizeIn(minWidth = 200.dp, maxWidth = 300.dp)) {
        Layout(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
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
                        text = stringResource(id = R.string.app_name),
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
                .background(MaterialTheme.colorScheme.background)
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
                label = {
                    Text(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        text = stringResource(id = destination.iconTextId),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
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