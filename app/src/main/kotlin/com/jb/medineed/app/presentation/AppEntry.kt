package com.jb.medineed.app.presentation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.jb.medineed.app.presentation.common.LocalWindowWidthState
import com.jb.medineed.app.presentation.common.Route
import com.jb.medineed.app.presentation.common.animatedComposable
import com.jb.medineed.app.presentation.common.arg
import com.jb.medineed.app.presentation.common.id
import com.jb.medineed.app.presentation.page.entry.MedicineEntryScreen
import com.jb.medineed.app.presentation.page.home.AdaptiveNavigation
import com.jb.medineed.app.presentation.page.lowstock.LowStockScreen
import com.jb.medineed.app.presentation.page.outofstock.OutOfStockScreen
import com.jb.medineed.app.presentation.page.reports.ReportsScreen
import com.jb.medineed.app.presentation.page.settings.SettingsPage
import com.jb.medineed.app.presentation.page.settings.appearance.AppearancePreferences
import com.jb.medineed.app.presentation.page.settings.appearance.DarkThemePreferences
import com.jb.medineed.app.presentation.page.settings.appearance.LanguagePage
import com.jb.medineed.app.presentation.page.stock.StockListScreen
import com.jb.medineed.app.presentation.page.update.StockUpdateScreen

@Composable
fun AppEntry() {
    val navController = rememberNavController()
    LocalContext.current
    LocalView.current
    val windowWidth = LocalWindowWidthState.current
    rememberCoroutineScope()

    val onNavigateBack: () -> Unit = {
        with(navController) {
            if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                popBackStack()
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    AdaptiveNavigation(
        windowWidth = windowWidth,
        currentRoute = currentRoute,
        onNavigateToRoute = { route ->
            navController.navigate(route) {
                popUpTo(navController.graph.startDestinationId) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    ) { paddingValues ->
        NavHost(
            modifier = Modifier.padding(paddingValues),
            navController = navController,
            startDestination = Route.STOCK_LIST,
        ) {
            animatedComposable(Route.STOCK_LIST) {
                StockListScreen(onAddMedicine = {
                    navController.navigate(Route.MEDICINE_ENTRY)
                }, onSettings = {
                    navController.navigate(Route.SETTINGS_PAGE)
                }, onMedicineClick = { id ->
                    navController.navigate(Route.STOCK_UPDATE id id)
                }, onEditClick = { id ->
                    navController.navigate(Route.EDIT_MEDICINE id id)
                })
            }

            animatedComposable(Route.MEDICINE_ENTRY) {
                MedicineEntryScreen(
                    onNavigateBack = { navController.popBackStack() })
            }

            animatedComposable(
                Route.EDIT_MEDICINE arg Route.MEDICINE_ID
            ) { backStack ->

                val id = backStack.arguments?.getString(Route.MEDICINE_ID)?.toLong()
                    ?: return@animatedComposable

                MedicineEntryScreen(
                    medicineId = id, onNavigateBack = { navController.popBackStack() })
            }

            animatedComposable(Route.LOW_STOCK) {
                LowStockScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onMedicineClick = { id ->
                        navController.navigate(Route.STOCK_UPDATE id id)
                    })
            }

            animatedComposable(Route.OUT_OF_STOCK) {
                OutOfStockScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onMedicineClick = { id ->
                        navController.navigate(Route.STOCK_UPDATE id id)
                    })
            }

            animatedComposable(Route.REPORTS) {
                ReportsScreen(
                    onNavigateBack = { navController.popBackStack() })
            }

            animatedComposable(
                Route.STOCK_UPDATE arg Route.MEDICINE_ID
            ) { backStack ->

                val id = backStack.arguments?.getString(Route.MEDICINE_ID)?.toLong()
                    ?: return@animatedComposable

                StockUpdateScreen(
                    medicineId = id, onNavigateBack = { navController.popBackStack() })
            }

            settingsGraph(
                onNavigateBack = onNavigateBack,
                onNavigateTo = { route ->
                    navController.navigate(route = route) { launchSingleTop = true }
                },
            )
        }
    }
}

fun NavGraphBuilder.settingsGraph(
    onNavigateBack: () -> Unit,
    onNavigateTo: (route: String) -> Unit,
) {
    navigation(startDestination = Route.SETTINGS_PAGE, route = Route.SETTINGS) {
        animatedComposable(Route.SETTINGS_PAGE) {
            SettingsPage(onNavigateBack = onNavigateBack, onNavigateTo = onNavigateTo)
        }
//        animatedComposable(Route.ABOUT) {
//            AboutPage(
//                onNavigateBack = onNavigateBack,
//                onNavigateToCreditsPage = { onNavigateTo(Route.CREDITS) },
//            )
//        }
//        animatedComposable(Route.CREDITS) { CreditsPage(onNavigateBack) }
        animatedComposable(Route.APPEARANCE) {
            AppearancePreferences(onNavigateBack = onNavigateBack, onNavigateTo = onNavigateTo)
        }
        animatedComposable(Route.LANGUAGES) { LanguagePage { onNavigateBack() } }
        animatedComposable(Route.DARK_THEME) { DarkThemePreferences { onNavigateBack() } }
    }
}