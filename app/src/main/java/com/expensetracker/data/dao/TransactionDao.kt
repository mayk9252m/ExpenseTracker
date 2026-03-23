package com.expensetracker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType

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
        WHERE strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        ORDER BY date DESC
    """)
    fun getTransactionsByMonth(month: String, year: String): LiveData<List<Transaction>>

    @Query("""
        SELECT * FROM transactions 
        WHERE strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        AND type = :type
        ORDER BY date DESC
    """)
    fun getTransactionsByMonthAndType(month: String, year: String, type: String): LiveData<List<Transaction>>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        AND type = 'EXPENSE'
    """)
    fun getTotalExpensesForMonth(month: String, year: String): LiveData<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        AND type = 'INCOME'
    """)
    fun getTotalIncomeForMonth(month: String, year: String): LiveData<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM transactions 
        WHERE strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        AND type = 'EXPENSE'
        AND category = :category
    """)
    suspend fun getTotalExpensesForCategory(month: String, year: String, category: String): Double

    @Query("SELECT * FROM transactions WHERE isRecurring = 1")
    suspend fun getRecurringTransactions(): List<Transaction>

    @Query("""
        SELECT * FROM transactions 
        WHERE type = 'EXPENSE' 
        AND strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        ORDER BY amount DESC 
        LIMIT 5
    """)
    fun getTopExpenses(month: String, year: String): LiveData<List<Transaction>>

    @Query("""
        SELECT category, SUM(amount) as total FROM transactions 
        WHERE type = 'EXPENSE' 
        AND strftime('%m', date/1000, 'unixepoch') = :month 
        AND strftime('%Y', date/1000, 'unixepoch') = :year 
        GROUP BY category
    """)
    fun getExpensesByCategory(month: String, year: String): LiveData<List<CategorySum>>
}

data class CategorySum(
    val category: String,
    val total: Double
)
