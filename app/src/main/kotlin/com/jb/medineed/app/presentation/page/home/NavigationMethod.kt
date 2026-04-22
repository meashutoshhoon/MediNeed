package com.jb.medineed.app.presentation.page.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.outlined.TrendingDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfoV2
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteType
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import com.jb.medineed.app.presentation.common.Route

data class NavItem(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val route: String
)

private val navItems = listOf(
    NavItem("Stock", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, Route.STOCK_LIST),
    NavItem(
        "Low Stock",
        Icons.AutoMirrored.Filled.TrendingDown,
        Icons.AutoMirrored.Outlined.TrendingDown,
        Route.LOW_STOCK
    ),
    NavItem("Order", Icons.Filled.ShoppingCart, Icons.Outlined.ShoppingCart, Route.OUT_OF_STOCK),
    NavItem("Reports", Icons.Filled.BarChart, Icons.Outlined.BarChart, Route.REPORTS),
    NavItem("Settings", Icons.Filled.Settings, Icons.Outlined.Settings, Route.SETTINGS_PAGE),
)

/**
 * Routes where navigation (bar/rail) is allowed
 */
private val navigationRoutes = setOf(
    Route.STOCK_LIST, Route.LOW_STOCK, Route.OUT_OF_STOCK, Route.REPORTS, Route.SETTINGS_PAGE
)

@Composable
fun AdaptiveNavigation(
    currentRoute: String?, onNavigateToRoute: (String) -> Unit, content: @Composable () -> Unit
) {
    val adaptiveInfo = currentWindowAdaptiveInfoV2()
    val defaultNavType = NavigationSuiteScaffoldDefaults.calculateFromAdaptiveInfo(adaptiveInfo)
    val isSettings = currentRoute == Route.SETTINGS_PAGE
    val showNavigation = when (defaultNavType) {
        NavigationSuiteType.NavigationBar -> {
            currentRoute in navigationRoutes && !isSettings
        }

        else -> {
            currentRoute in navigationRoutes
        }
    }

    val navType = if (showNavigation) {
        defaultNavType
    } else {
        NavigationSuiteType.None
    }

    val isBottomBar = navType == NavigationSuiteType.NavigationBar

    NavigationSuiteScaffold(
        layoutType = navType, navigationSuiteItems = {

            navItems.forEach { item ->
                if (isBottomBar && item.route == Route.SETTINGS_PAGE) return@forEach

                item(
                    selected = currentRoute == item.route,
                    onClick = { onNavigateToRoute(item.route) },
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == item.route) item.selectedIcon
                            else item.unselectedIcon, contentDescription = item.label
                        )
                    },
                    label = { Text(item.label) })
            }
        }) {
        content()
    }
}