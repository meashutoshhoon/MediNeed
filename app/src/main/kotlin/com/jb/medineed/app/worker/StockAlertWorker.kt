package com.jb.medineed.app.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.jb.medineed.app.MainActivity
import com.jb.medineed.app.data.repository.MedicineRepository
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.concurrent.TimeUnit
import kotlin.collections.filter

class StockAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: MedicineRepository by inject()

    override suspend fun doWork(): Result {
        return try {
            val medicines = repository.getMedicinesNeedingAlert()
            val outOfStock = medicines.filter { it.isOutOfStock }
            val lowStock = medicines.filter { it.isLowStock }

            if (outOfStock.isNotEmpty()) {
                showNotification(
                    id = NOTIF_ID_OUT_OF_STOCK,
                    title = "⚠️ Out of Stock Alert",
                    body = "${outOfStock.size} medicine(s) are completely out of stock: ${outOfStock.take(3).joinToString { it.name }}${if (outOfStock.size > 3) " and more…" else ""}",
                    context = applicationContext
                )
            }

            if (lowStock.isNotEmpty()) {
                showNotification(
                    id = NOTIF_ID_LOW_STOCK,
                    title = "📉 Low Stock Warning",
                    body = "${lowStock.size} medicine(s) are running low: ${lowStock.take(3).joinToString { "${it.name} (${it.quantity} left)" }}${if (lowStock.size > 3) " and more…" else ""}",
                    context = applicationContext
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun showNotification(id: Int, title: String, body: String, context: Context) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, id, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(id, notification)
    }

    companion object {
        const val CHANNEL_ID = "medistock_stock_alerts"
        const val WORK_NAME = "StockAlertWork"
        private const val NOTIF_ID_OUT_OF_STOCK = 1001
        private const val NOTIF_ID_LOW_STOCK = 1002

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<StockAlertWorker>(
                repeatInterval = 6,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiresBatteryNotLow(false)
                        .build()
                )
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            StockAlertWorker.schedule(context)
        }
    }
}