package com.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.expensetracker.data.dao.BudgetDao
import com.expensetracker.data.model.Budget

class BudgetRepository(private val budgetDao: BudgetDao) {

    suspend fun insert(budget: Budget): Long = budgetDao.insert(budget)

    suspend fun update(budget: Budget) = budgetDao.update(budget)

    suspend fun delete(budget: Budget) = budgetDao.delete(budget)

    fun getBudgetsForMonth(month: Int, year: Int): LiveData<List<Budget>> =
        budgetDao.getBudgetsForMonth(month, year)

    fun getOverallBudget(month: Int, year: Int): LiveData<Budget?> =
        budgetDao.getOverallBudget(month, year)

    suspend fun getOverallBudgetSync(month: Int, year: Int): Budget? =
        budgetDao.getOverallBudgetSync(month, year)

    suspend fun getBudgetForCategory(month: Int, year: Int, category: String): Budget? =
        budgetDao.getBudgetForCategory(month, year, category)
}
