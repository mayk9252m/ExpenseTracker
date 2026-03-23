package com.expensetracker.data.repository

import androidx.lifecycle.LiveData
import com.expensetracker.data.dao.CategorySum
import com.expensetracker.data.dao.TransactionDao
import com.expensetracker.data.model.Transaction
import java.time.Month

class TransactionRepository(private val transactionDao: TransactionDao) {

    val allTransactions: LiveData<List<Transaction>> = transactionDao.getAllTransactions()

    suspend fun insert(transaction: Transaction): Long = transactionDao.insert(transaction)

    suspend fun update(transaction: Transaction) = transactionDao.update(transaction)

    suspend fun delete(transaction: Transaction) = transactionDao.delete(transaction)

    fun getTransactionsByMonth(month: String, year: String): LiveData<List<Transaction>> =
        transactionDao.getTransactionsByMonth(month, year)

    fun getTransactionsByMonthAndType(
        month: String, year: String, type: String
    ): LiveData<List<Transaction>> =
        transactionDao.getTransactionsByMonthAndType(month, year, type)

    fun getTotalExpensesForMonth(month: String, year: String): LiveData<Double> =
        transactionDao.getTotalExpensesForMonth(month, year)

    fun getTotalIncomeForMonth(month: String, year: String): LiveData<Double> =
        transactionDao.getTotalIncomeForMonth(month, year)

    fun getExpensesByCategory(month: String, year: String): LiveData<List<CategorySum>> =
        transactionDao.getExpensesByCategory(month, year)

    fun getTopExpenses(month: String, year: String): LiveData<List<Transaction>> =
        transactionDao.getTopExpenses(month, year)

    suspend fun getRecurringTransactions(): List<Transaction> =
        transactionDao.getRecurringTransactions()

    suspend fun getAllTransactionsSync(): List<Transaction> =
        transactionDao.getAllTransactionsSync()

    suspend fun getTotalExpensesForCategory(month: String, year: String, category: String): Double =
        transactionDao.getTotalExpensesForCategory(month, year, category)
}
