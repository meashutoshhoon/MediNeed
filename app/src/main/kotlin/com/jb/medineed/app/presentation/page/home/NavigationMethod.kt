package com.jb.medineed.app.presentation.page.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuOpen
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.presentation.common.Route

// ─── Data Model ─────────────────────────────────────────────────────────────

data class BottomNavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector = selectedIcon,
    val route: String,
)

// Define your nav items here — swap Screen.* with your actual routes
val bottomNavItems = listOf(
    BottomNavItem(
        label = "Stock",
        selectedIcon = Icons.Filled.Inventory2,
        unselectedIcon = Icons.Outlined.Inventory2,
        route = Route.STOCK_LIST,
    ),
    BottomNavItem(
        label = "Low Stock",
        selectedIcon = Icons.AutoMirrored.Filled.TrendingDown,
        unselectedIcon = Icons.AutoMirrored.Outlined.TrendingDown,
        route = Route.LOW_STOCK,
    ),
    BottomNavItem(
        label = "Order",
        selectedIcon = Icons.Filled.ShoppingCart,
        unselectedIcon = Icons.Outlined.ShoppingCart,
        route = Route.OUT_OF_STOCK,
    ),
    BottomNavItem(
        label = "Reports",
        selectedIcon = Icons.Filled.BarChart,
        unselectedIcon = Icons.Outlined.BarChart,
        route = Route.REPORTS,
    ),
)

// ─── Main Adaptive Navigation ────────────────────────────────────────────────

/**
 * Adaptive navigation that renders:
 *  - Compact  → Bottom Navigation Bar
 *  - Medium   → Navigation Rail (collapsible via gesture/button)
 *  - Expanded → Navigation Rail (always visible, collapsible via gesture/button)
 *
 * This composable owns its own Scaffold/layout; no parent layout is needed.
 */
@Composable
fun AdaptiveNavigation(
    windowWidth: WindowWidthSizeClass,
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (PaddingValues) -> Unit,
) {
    when (windowWidth) {
        WindowWidthSizeClass.Compact -> {
            CompactLayout(
                modifier = modifier,
                currentRoute = currentRoute,
                onNavigateToRoute = onNavigateToRoute,
                content = content,
            )
        }

        WindowWidthSizeClass.Medium, WindowWidthSizeClass.Expanded -> {
            RailLayout(
                modifier = modifier,
                currentRoute = currentRoute,
                onNavigateToRoute = onNavigateToRoute,
                content = content,
            )
        }
    }
}

// ─── Compact: Bottom Navigation ──────────────────────────────────────────────

@Composable
private fun CompactLayout(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    val showBottomBar = bottomNavItems.any { it.route == currentRoute }

    Scaffold(
        modifier = modifier,
        bottomBar = {
            AnimatedVisibility(visible = showBottomBar) {
                AppBottomNavigationBar(
                    currentRoute = currentRoute,
                    onNavigateToRoute = onNavigateToRoute,
                )
            }
        },
    ) { innerPadding ->
        content(innerPadding)
    }
}

@Composable
private fun AppBottomNavigationBar(
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
) {
    NavigationBar(
        tonalElevation = 3.dp,
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    onNavigateToRoute(item.route)
                },
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label,
                    )
                },
                label = { Text(item.label) },
                alwaysShowLabel = false,
            )
        }
    }
}

// ─── Medium / Expanded: Navigation Rail ──────────────────────────────────────

@Composable
private fun RailLayout(
    modifier: Modifier = Modifier,
    currentRoute: String?,
    onNavigateToRoute: (String) -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    // Rail can be expanded (shows labels) or collapsed (icons only)
    var railExpanded by remember { mutableStateOf(false) }
    val railWidth by animateDpAsState(
        targetValue = if (railExpanded) 200.dp else 80.dp,
        animationSpec = tween(durationMillis = 250),
        label = "railWidth",
    )

    // Gesture: swipe right on rail to expand, swipe left to collapse
    var dragAccumulator by remember { mutableFloatStateOf(0f) }
    val swipeThreshold = 60f

    Scaffold(modifier = modifier) { innerPadding ->
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // ── Navigation Rail ────────────────────────────────────────────
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                tonalElevation = 2.dp,
                shape = RoundedCornerShape(topEnd = 25.dp, bottomEnd = 25.dp),
                modifier = Modifier
                    .fillMaxHeight()
                    .width(railWidth)
                    // Gesture support: drag horizontally to expand/collapse
                    .pointerInput(Unit) {
                        detectHorizontalDragGestures(
                            onDragEnd = { dragAccumulator = 0f },
                            onDragCancel = { dragAccumulator = 0f },
                            onHorizontalDrag = { _, dragAmount ->
                                dragAccumulator += dragAmount
                                when {
                                    dragAccumulator > swipeThreshold -> {
                                        railExpanded = true
                                        dragAccumulator = 0f
                                    }

                                    dragAccumulator < -swipeThreshold -> {
                                        railExpanded = false
                                        dragAccumulator = 0f
                                    }
                                }
                            },
                        )
                    },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(railWidth),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top,
                ) {
                    // Toggle button
                    IconButton(
                        onClick = { railExpanded = !railExpanded },
                        modifier = Modifier.padding(bottom = 8.dp),
                    ) {
                        Icon(
                            imageVector = if (railExpanded) Icons.AutoMirrored.Filled.MenuOpen else Icons.Filled.Menu,
                            contentDescription = if (railExpanded) "Collapse rail" else "Expand rail",
                        )
                    }

                    Spacer(Modifier.weight(1f))

                    // Main nav items
                    Column(
                        modifier = Modifier.selectableGroup(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        bottomNavItems.forEach { item ->
                            RailNavItem(
                                item = item,
                                selected = currentRoute == item.route,
                                expanded = railExpanded,
                                onClick = { onNavigateToRoute(item.route) },
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    // Settings pinned to bottom
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                    )
                    RailNavItem(
                        item = BottomNavItem(
                            label = "Settings",
                            selectedIcon = Icons.Filled.Settings,
                            unselectedIcon = Icons.Outlined.Settings,
                            route = Route.SETTINGS_PAGE,
                        ),
                        selected = currentRoute == Route.SETTINGS_PAGE,
                        expanded = railExpanded,
                        onClick = { onNavigateToRoute(Route.SETTINGS_PAGE) },
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }

            // ── Main Content ───────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1f),
            ) {
                content(PaddingValues(0.dp))
            }
        }
    }
}

@Composable
private fun RailNavItem(
    item: BottomNavItem,
    selected: Boolean,
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val bgColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
    else androidx.compose.ui.graphics.Color.Transparent
    val contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer
    else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxWidth()
            .height(56.dp)
            .clip(MaterialTheme.shapes.large)
            .background(bgColor)
            .selectable(selected = selected, onClick = onClick),
        contentAlignment = Alignment.CenterStart,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = if (expanded) Arrangement.Start else Arrangement.Center,
        ) {
            CompositionLocalProvider(LocalContentColor provides contentColor) {
                Icon(
                    imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                    contentDescription = item.label,
                )
                AnimatedVisibility(
                    visible = expanded,
                    enter = fadeIn(tween(150)),
                    exit = fadeOut(tween(100)),
                ) {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(start = 16.dp),
                        maxLines = 1,
                    )
                }
            }
        }
    }
}