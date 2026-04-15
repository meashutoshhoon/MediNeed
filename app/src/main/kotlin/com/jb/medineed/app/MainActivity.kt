package com.jb.medineed.app

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.jb.medineed.app.presentation.AppEntry
import com.jb.medineed.app.presentation.common.LocalDarkTheme
import com.jb.medineed.app.presentation.common.SettingsProvider
import com.jb.medineed.app.presentation.theme.MediTheme
import com.jb.medineed.app.util.PreferenceUtil
import com.jb.medineed.app.util.setLanguage
import kotlinx.coroutines.runBlocking
import org.koin.compose.KoinContext

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT < 33) {
            runBlocking {
                setLanguage(PreferenceUtil.getLocaleFromPreference())
            }
        }

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT)
        )

        setContent {
            KoinContext {
                val windowSizeClass = calculateWindowSizeClass(this)

                SettingsProvider(
                    windowWidthSizeClass = windowSizeClass.widthSizeClass
                ) {
                    val themeState = LocalDarkTheme.current

                    MediTheme(
                        darkTheme = themeState.isDarkTheme(),
                        isHighContrastModeEnabled = themeState.isHighContrastModeEnabled
                    ) {
                        AppEntry()
                    }
                }
            }
        }
    }
}