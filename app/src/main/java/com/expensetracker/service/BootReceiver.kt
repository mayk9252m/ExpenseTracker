package com.expensetracker.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.expensetracker.worker.RecurringExpenseWorker

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            RecurringExpenseWorker.schedule(context)
        }
    }
}
