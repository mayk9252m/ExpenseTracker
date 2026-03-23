package com.expensetracker.ui.dashboard

import android.app.Application
import android.content.Context
import androidx.lifecycle.*
import com.expensetracker.data.dao.CategorySum
import com.expensetracker.data.db.AppDatabase
import com.expensetracker.data.model.Budget
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.BudgetRepository
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.util.DateUtils
import com.expensetracker.util.NotificationHelper
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val transactionRepo: TransactionRepository
    private val budgetRepo: BudgetRepository

    val transactions: LiveData<List<Transaction>>
    val totalExpenses: LiveData<Double>
    val totalIncome: LiveData<Double>
    val expensesByCategory: LiveData<List<CategorySum>>
    val topExpenses: LiveData<List<Transaction>>
    val overallBudget: LiveData<Budget?>
    val netBalance: MediatorLiveData<Double> = MediatorLiveData()

    init {
        val db = AppDatabase.getDatabase(application)
        transactionRepo = TransactionRepository(db.transactionDao())
        budgetRepo = BudgetRepository(db.budgetDao())

        // ✅ No parameters needed — defaults to current month/year
        transactions    = transactionRepo.getTransactionsByMonth()
        totalExpenses   = transactionRepo.getTotalExpensesForMonth()
        totalIncome     = transactionRepo.getTotalIncomeForMonth()
        expensesByCategory = transactionRepo.getExpensesByCategory()
        topExpenses     = transactionRepo.getTopExpenses()
        overallBudget   = budgetRepo.getOverallBudget(
            DateUtils.getCurrentMonthInt(),
            DateUtils.getCurrentYearInt()
        )

        netBalance.addSource(totalIncome) { income ->
            netBalance.value = (income ?: 0.0) - (totalExpenses.value ?: 0.0)
        }
        netBalance.addSource(totalExpenses) { expense ->
            netBalance.value = (totalIncome.value ?: 0.0) - (expense ?: 0.0)
        }
    }

    fun checkBudgetAndNotify(context: Context) {
        viewModelScope.launch {
            val budget = budgetRepo.getOverallBudgetSync(
                DateUtils.getCurrentMonthInt(),
                DateUtils.getCurrentYearInt()
            ) ?: return@launch

            val spent = totalExpenses.value ?: 0.0
            val percent = ((spent / budget.monthlyLimit) * 100).toInt()

            val prefs = context.getSharedPreferences("budget_prefs", Context.MODE_PRIVATE)
            val lastNotified = prefs.getInt("last_notified_percent", 0)

            when {
                percent >= 100 && lastNotified < 100 -> {
                    NotificationHelper.sendBudgetAlert(context, 100, spent, budget.monthlyLimit)
                    prefs.edit().putInt("last_notified_percent", 100).apply()
                }
                percent >= 75 && lastNotified < 75 -> {
                    NotificationHelper.sendBudgetAlert(context, 75, spent, budget.monthlyLimit)
                    prefs.edit().putInt("last_notified_percent", 75).apply()
                }
                percent >= 50 && lastNotified < 50 -> {
                    NotificationHelper.sendBudgetAlert(context, 50, spent, budget.monthlyLimit)
                    prefs.edit().putInt("last_notified_percent", 50).apply()
                }
            }
        }
    }
}