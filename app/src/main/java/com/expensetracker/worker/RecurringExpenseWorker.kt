package com.expensetracker.worker

import android.content.Context
import androidx.work.*
import com.expensetracker.data.db.AppDatabase
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.data.repository.BudgetRepository
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.util.DateUtils
import com.expensetracker.util.NotificationHelper
import java.util.*
import java.util.concurrent.TimeUnit

class RecurringExpenseWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val db = AppDatabase.getDatabase(context)
        val repo = TransactionRepository(db.transactionDao())
        val budgetRepo = BudgetRepository(db.budgetDao())

        val recurringTransactions = repo.getRecurringTransactions()
        val today = Calendar.getInstance()
        val currentDay = today.get(Calendar.DAY_OF_MONTH)

        // ✅ Now using Int instead of String
        val currentMonth = DateUtils.getCurrentMonthInt()
        val currentYear = DateUtils.getCurrentYearInt()

        recurringTransactions.forEach { recurring ->
            if (recurring.recurringDay == currentDay) {
                val newTransaction = Transaction(
                    title = recurring.title,
                    amount = recurring.amount,
                    category = recurring.category,
                    type = recurring.type,
                    date = System.currentTimeMillis(),
                    note = "Auto-added (recurring)",
                    isRecurring = false
                )
                repo.insert(newTransaction)

                NotificationHelper.sendRecurringExpenseNotification(
                    context, recurring.title, recurring.amount
                )

                // Check budget after adding recurring expense
                if (recurring.type == TransactionType.EXPENSE) {
                    checkBudgetAfterExpense(repo, budgetRepo, currentMonth, currentYear)
                }
            }
        }

        return Result.success()
    }

    // ✅ Parameters are now Int instead of String
    private suspend fun checkBudgetAfterExpense(
        repo: TransactionRepository,
        budgetRepo: BudgetRepository,
        month: Int,
        year: Int
    ) {
        val budget = budgetRepo.getOverallBudgetSync(month, year) ?: return

        // ✅ Calling with Int parameters matching updated repository
        val totalSpent = repo.getTotalExpensesForCategory(
            category = "OVERALL",
            month = month,
            year = year
        )

        val percent = ((totalSpent / budget.monthlyLimit) * 100).toInt()
        when {
            percent >= 100 -> NotificationHelper.sendBudgetAlert(
                context, 100, totalSpent, budget.monthlyLimit
            )
            percent >= 75  -> NotificationHelper.sendBudgetAlert(
                context, 75, totalSpent, budget.monthlyLimit
            )
            percent >= 50  -> NotificationHelper.sendBudgetAlert(
                context, 50, totalSpent, budget.monthlyLimit
            )
        }
    }

    companion object {
        const val WORK_NAME = "RecurringExpenseWorker"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(false)
                .build()

            // Calculate delay until next midnight
            val now = Calendar.getInstance()
            val midnight = Calendar.getInstance().apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 5)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val delayMillis = midnight.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<RecurringExpenseWorker>(
                1, TimeUnit.DAYS
            )
                .setInitialDelay(delayMillis, TimeUnit.MILLISECONDS)
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}