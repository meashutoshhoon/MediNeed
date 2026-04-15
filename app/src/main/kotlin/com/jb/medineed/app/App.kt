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
import com.jb.medineed.app.worker.StockAlertWorker
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

        context = this
        applicationScope = CoroutineScope(SupervisorJob())
        packageInfo = getPkgInfo()

        DynamicColors.applyToActivitiesIfAvailable(this)

        createNotificationChannel()
        StockAlertWorker.schedule(this)
    }

    private fun getPkgInfo(): PackageInfo =
        if (Build.VERSION.SDK_INT >= 33)
            packageManager.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        else
            @Suppress("DEPRECATION")
            packageManager.getPackageInfo(packageName, 0)

    private fun createNotificationChannel() {
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(
            NotificationChannel(
                StockAlertWorker.CHANNEL_ID,
                "Stock Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts for low stock and out of stock medicines"
            }
        )
    }

    companion object {
        lateinit var applicationScope: CoroutineScope
        lateinit var packageInfo: PackageInfo
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }
}