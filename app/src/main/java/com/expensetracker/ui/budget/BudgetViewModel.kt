package com.expensetracker.ui.budget

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.db.AppDatabase
import com.expensetracker.data.model.Budget
import com.expensetracker.data.model.ExpenseCategory
import com.expensetracker.data.repository.BudgetRepository
import com.expensetracker.util.DateUtils
import kotlinx.coroutines.launch

class BudgetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BudgetRepository
    private val currentMonth = DateUtils.getCurrentMonthInt()
    private val currentYear = DateUtils.getCurrentYearInt()

    val overallBudget: LiveData<Budget?>
    val categoryBudgets: LiveData<List<Budget>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = BudgetRepository(db.budgetDao())
        overallBudget = repository.getOverallBudget(currentMonth, currentYear)
        categoryBudgets = repository.getBudgetsForMonth(currentMonth, currentYear)
    }

    fun saveOverallBudget(limit: Double) {
        viewModelScope.launch {
            val existing = repository.getOverallBudgetSync(currentMonth, currentYear)
            if (existing != null) {
                repository.update(existing.copy(monthlyLimit = limit))
            } else {
                repository.insert(
                    Budget(
                        category = "OVERALL",
                        monthlyLimit = limit,
                        month = currentMonth,
                        year = currentYear
                    )
                )
            }
        }
    }

    fun saveCategoryBudget(category: String, limit: Double) {
        viewModelScope.launch {
            val existing = repository.getBudgetForCategory(currentMonth, currentYear, category)
            if (existing != null) {
                repository.update(existing.copy(monthlyLimit = limit))
            } else {
                repository.insert(
                    Budget(
                        category = category,
                        monthlyLimit = limit,
                        month = currentMonth,
                        year = currentYear
                    )
                )
            }
        }
    }
}
