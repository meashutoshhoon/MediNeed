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

class StockAlertWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    private val repository: MedicineRepository by inject()

    override suspend fun doWork(): Result = try {
        val medicines = repository.getMedicinesNeedingAlert()

        val (outOfStock, lowStock) = medicines.partition { it.isOutOfStock }

        notifyIfNeeded(
            id = NOTIF_ID_OUT_OF_STOCK,
            items = outOfStock,
            title = "⚠️ Out of Stock Alert"
        ) {
            it.name
        }

        notifyIfNeeded(
            id = NOTIF_ID_LOW_STOCK,
            items = lowStock.filter { it.isLowStock },
            title = "📉 Low Stock Warning"
        ) {
            "${it.name} (${it.quantity} left)"
        }

        Result.success()
    } catch (_: Exception) {
        Result.retry()
    }

    private inline fun <T> notifyIfNeeded(
        id: Int,
        items: List<T>,
        title: String,
        crossinline formatter: (T) -> String
    ) {
        if (items.isEmpty()) return

        val preview = items.take(3).joinToString { formatter(it) }
        val body = buildString {
            append("${items.size} medicine(s): ")
            append(preview)
            if (items.size > 3) append(" and more…")
        }

        showNotification(id, title, body)
    }

    private fun showNotification(id: Int, title: String, body: String) {
        val context = applicationContext

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            id,
            intent,
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

        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
            .notify(id, notification)
    }

    companion object {
        const val CHANNEL_ID = "medistock_stock_alerts"
        private const val WORK_NAME = "StockAlertWork"
        private const val NOTIF_ID_OUT_OF_STOCK = 1001
        private const val NOTIF_ID_LOW_STOCK = 1002

        fun schedule(context: Context) {
            val request = PeriodicWorkRequestBuilder<StockAlertWorker>(6, TimeUnit.HOURS).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            StockAlertWorker.schedule(context)
        }
    }
}