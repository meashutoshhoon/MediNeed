package com.jb.medineed.app.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.navigation.compose.NavHost
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.jb.medineed.app.R
import com.jb.medineed.app.presentation.common.LocalWindowWidthState
import com.jb.medineed.app.presentation.common.Route
import com.jb.medineed.app.presentation.common.animatedComposable
import com.jb.medineed.app.presentation.page.settings.SettingsPage
import com.jb.medineed.app.presentation.page.settings.appearance.AppearancePreferences
import com.jb.medineed.app.presentation.page.settings.appearance.DarkThemePreferences
import com.jb.medineed.app.presentation.page.settings.appearance.LanguagePage

private val TopDestinations =
    listOf(Route.HOME, Route.SETTINGS_PAGE)

@Composable
fun AppEntry(animatedComposable: (String, Any?) -> Unit) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val view = LocalView.current
    val windowWidth = LocalWindowWidthState.current

    val appName = stringResource(R.string.app_name)
    val scope = rememberCoroutineScope()

    val onNavigateBack: () -> Unit = {
        with(navController) {
            if (currentBackStackEntry?.lifecycle?.currentState == Lifecycle.State.RESUMED) {
                popBackStack()
            }
        }
    }

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    var currentTopDestination by rememberSaveable { mutableStateOf(currentRoute) }

    LaunchedEffect(currentRoute) {
        if (currentRoute in TopDestinations) {
            currentTopDestination = currentRoute
        }
    }
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
//        NavigationDrawer(
//            windowWidth = windowWidth,
//            drawerState = drawerState,
//            currentRoute = currentRoute,
//            currentTopDestination = currentTopDestination,
//            showQuickSettings = true,
//            gesturesEnabled = currentRoute == Route.HOME,
//            onDismissRequest = { drawerState.close() },
//            onNavigateToRoute = {
//                if (currentRoute != it) {
//                    navController.navigate(it) {
//                        launchSingleTop = true
//                        popUpTo(route = Route.HOME)
//                    }
//                }
//            },
//        ) {
            NavHost(
                modifier = Modifier.align(Alignment.Center),
                navController = navController,
                startDestination = Route.HOME,
            ) {
                animatedComposable(Route.HOME) {
//                    HomeScreen()
                }

                settingsGraph(
                    onNavigateBack = onNavigateBack,
                    onNavigateTo = { route ->
                        navController.navigate(route = route) { launchSingleTop = true }
                    },
                )
//            }
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