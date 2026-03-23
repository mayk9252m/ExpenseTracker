package com.expensetracker.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.expensetracker.R
import com.expensetracker.ui.MainActivity

object NotificationHelper {

    const val CHANNEL_ID_BUDGET = "budget_alert_channel"
    const val CHANNEL_ID_RECURRING = "recurring_expense_channel"

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val budgetChannel = NotificationChannel(
                CHANNEL_ID_BUDGET,
                "Budget Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when you approach your monthly budget limit"
            }

            val recurringChannel = NotificationChannel(
                CHANNEL_ID_RECURRING,
                "Recurring Expenses",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications when recurring expenses are added"
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(budgetChannel)
            manager.createNotificationChannel(recurringChannel)
        }
    }

    fun sendBudgetAlert(context: Context, percentUsed: Int, spent: Double, limit: Double) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val title = when {
            percentUsed >= 100 -> "⚠️ Budget Exceeded!"
            percentUsed >= 75 -> "🔴 75% Budget Used"
            else -> "🟡 50% Budget Used"
        }

        val message = "You've spent ₹${String.format("%.2f", spent)} of your ₹${String.format("%.2f", limit)} budget ($percentUsed%)"

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_BUDGET)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(percentUsed, notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    fun sendRecurringExpenseNotification(context: Context, title: String, amount: Double) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID_RECURRING)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Recurring Expense Added")
            .setContentText("$title — ₹${String.format("%.2f", amount)}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(title.hashCode(), notification)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
}
