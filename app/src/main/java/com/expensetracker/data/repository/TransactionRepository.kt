package com.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.expensetracker.data.dao.CategorySum
import com.expensetracker.data.dao.TransactionDao
import com.expensetracker.data.model.Transaction
import com.expensetracker.util.DateUtils

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: Transaction): Long = transactionDao.insert(transaction)

    suspend fun update(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)

    // ✅ Uses timestamp range instead of strftime
    fun getTransactionsByMonth(
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): LiveData<List<Transaction>> =
        transactionDao.getTransactionsByMonth(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year)
        )

    // ✅ Filter by type using timestamp range
    fun getTransactionsByMonthAndType(
        type: String,
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): LiveData<List<Transaction>> =
        transactionDao.getTransactionsByMonthAndType(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year),
            type
        )

    fun getTotalExpensesForMonth(
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): LiveData<Double> =
        transactionDao.getTotalExpensesForMonth(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year)
        )

    fun getTotalIncomeForMonth(
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): LiveData<Double> =
        transactionDao.getTotalIncomeForMonth(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year)
        )

    fun getExpensesByCategory(
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): LiveData<List<CategorySum>> =
        transactionDao.getExpensesByCategory(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year)
        )

    fun getTopExpenses(
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): LiveData<List<Transaction>> =
        transactionDao.getTopExpenses(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year)
        )

    suspend fun getRecurringTransactions(): List<Transaction> =
        transactionDao.getRecurringTransactions()

    suspend fun getAllTransactionsSync(): List<Transaction> =
        transactionDao.getAllTransactionsSync()

    suspend fun getTotalExpensesForCategory(
        category: String,
        month: Int = DateUtils.getCurrentMonthInt(),
        year: Int = DateUtils.getCurrentYearInt()
    ): Double =
        transactionDao.getTotalExpensesForCategory(
            DateUtils.getStartOfMonth(month, year),
            DateUtils.getEndOfMonth(month, year),
            category
        )
}