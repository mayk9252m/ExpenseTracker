package com.expensetracker.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.db.AppDatabase
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.TransactionRepository
import com.expensetracker.util.DateUtils
import kotlinx.coroutines.launch

class TransactionsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository
    private val _filter = MutableLiveData("ALL")

    val transactions: LiveData<List<Transaction>>

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepository(db.transactionDao())

        // ✅ Use repository directly — no broken extension function
        transactions = _filter.switchMap { filter ->
            val month = DateUtils.getCurrentMonth()
            val year = DateUtils.getCurrentYear()
            when (filter) {
                "INCOME"  -> repository.getTransactionsByMonthAndType(month, year, "INCOME")
                "EXPENSE" -> repository.getTransactionsByMonthAndType(month, year, "EXPENSE")
                else      -> repository.getTransactionsByMonth(month, year)
            }
        }
    }

    fun setFilter(filter: String) {
        _filter.value = filter
    }

    fun getAllTransactionsForExport(onResult: (List<Transaction>) -> Unit) {
        viewModelScope.launch {
            val list = repository.getAllTransactionsSync()
            onResult(list)
        }
    }
}