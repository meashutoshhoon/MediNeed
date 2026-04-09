package com.jb.medineed.app

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.google.android.material.color.DynamicColors
import com.jb.medineed.app.di.appModules
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModules)
        }

        context = applicationContext
        packageInfo =
            packageManager.run {
                if (Build.VERSION.SDK_INT >= 33)
                    getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
                else getPackageInfo(packageName, 0)
            }
        applicationScope = CoroutineScope(SupervisorJob())
        DynamicColors.applyToActivitiesIfAvailable(this)

        createNotificationChannel()
        StockAlertWorker.schedule(this)
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            StockAlertWorker.CHANNEL_ID,
            "Stock Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Alerts for low stock and out of stock medicines"
        }
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    companion object {
        lateinit var applicationScope: CoroutineScope
        lateinit var packageInfo: PackageInfo
        @SuppressLint("StaticFieldLeak") lateinit var context: Context
    }
}