package com.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.expensetracker.data.model.Budget

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget)

    @Delete
    suspend fun delete(budget: Budget)

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year")
    fun getBudgetsForMonth(month: Int, year: Int): LiveData<List<Budget>>

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category = :category LIMIT 1")
    suspend fun getBudgetForCategory(month: Int, year: Int, category: String): Budget?

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category = 'OVERALL' LIMIT 1")
    fun getOverallBudget(month: Int, year: Int): LiveData<Budget?>

    @Query("SELECT * FROM budgets WHERE month = :month AND year = :year AND category = 'OVERALL' LIMIT 1")
    suspend fun getOverallBudgetSync(month: Int, year: Int): Budget?
}
