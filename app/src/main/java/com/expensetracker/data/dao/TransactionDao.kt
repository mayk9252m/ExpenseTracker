package com.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import java.time.Month

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    suspend fun getAllTransactionsSync(): List<Transaction>

    @Query("""
        SELECT * FROM transactions 
        WHERE date >= :startOfMonth And date <= :endOfMonth
        ORDER BY date DESC
    """)
    fun getTransactionsByMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): LiveData<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        AND type = :type
        ORDER BY date DESC
    """)
    fun getTransactionsByMonthAndType(
        startOfMonth: Long,
        endOfMonth: Long,
        type: String
    ): LiveData<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        AND type = 'EXPENSE'
    """)
    fun getTotalExpensesForMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): LiveData<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        AND type = 'INCOME'
    """)
    fun getTotalIncomeForMonth(
        startOfMonth: Long,
        endOfMonth: Long
    ): LiveData<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE date >= :startOfMonth AND date <= :endOfMonth
        AND type = 'EXPENSE'
        AND category = :category
    """)
    suspend fun getTotalExpensesForCategory(
        startOfMonth: Long,
        endOfMonth: Long,
        category: String
    ): Double

    @Query("SELECT * FROM transactions WHERE isRecurring = 1")
    suspend fun getRecurringTransactions(): List<Transaction>

    @Query("""
        SELECT * FROM transactions 
        WHERE type = 'EXPENSE' 
        AND date >= :startOfMonth AND date <= :endOfMonth
        ORDER BY amount DESC 
        LIMIT 5
    """)
    fun getTopExpenses(
        startOfMonth: Long,
        endOfMonth: Long
    ): LiveData<List<Transaction>>

    @Query("""
        SELECT category, SUM(amount) as total FROM transactions 
        WHERE type = 'EXPENSE' 
        AND date >= :startOfMonth AND date <= :endOfMonth
        GROUP BY category
    """)
    fun getExpensesByCategory(
        startOfMonth: Long,
        endOfMonth: Long
    ): LiveData<List<CategorySum>>
}

data class CategorySum(
    val category: String,
    val total: Double
)
