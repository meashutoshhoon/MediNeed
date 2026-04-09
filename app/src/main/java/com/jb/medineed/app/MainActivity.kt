package com.jb.medineed.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.jb.medineed.app.ui.common.LocalDarkTheme
import com.jb.medineed.app.ui.common.SettingsProvider
import com.jb.medineed.app.ui.navigation.MediStockNavHost
import com.jb.medineed.app.ui.navigation.Screen
import com.jb.medineed.app.ui.page.AppEntry
import com.jb.medineed.app.ui.theme.MediTheme
import com.jb.medineed.app.util.PreferenceUtil
import com.jb.medineed.app.util.setLanguage
import kotlinx.coroutines.runBlocking
import org.koin.compose.KoinContext

data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT < 33) {
            runBlocking { setLanguage(PreferenceUtil.getLocaleFromPreference()) }
        }
        enableEdgeToEdge()
        setContent {
            KoinContext {
                val windowSizeClass = calculateWindowSizeClass(this)
                SettingsProvider(windowWidthSizeClass = windowSizeClass.widthSizeClass) {
                    MediTheme(
                        darkTheme = LocalDarkTheme.current.isDarkTheme(),
                        isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                    ) {
                        MediStockApp()
                    }
                }
            }
        }
    }
}

@Composable
fun MediStockApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem("Stock", Icons.Default.Inventory2, Screen.StockList.route),
        BottomNavItem("Low Stock", Icons.Default.TrendingDown, Screen.LowStock.route),
        BottomNavItem("Order", Icons.Default.ShoppingCart, Screen.OutOfStock.route),
        BottomNavItem("Reports", Icons.Default.BarChart, Screen.Reports.route),
    )

    val showBottomBar = currentRoute in bottomNavItems.map { it.route }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontWeight = FontWeight.Medium) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Spacer(modifier = Modifier.padding(innerPadding))
        MediStockNavHost(navController = navController)
    }
}