package com.expensetracker.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.expensetracker.data.db.AppDatabase
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.launch

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = TransactionRepository(db.transactionDao())
    }

    fun insertTransaction(transaction: Transaction, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.insert(transaction)
            onComplete()
        }
    }

    fun updateTransaction(transaction: Transaction, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.update(transaction)
            onComplete()
        }
    }

    fun deleteTransaction(transaction: Transaction, onComplete: () -> Unit) {
        viewModelScope.launch {
            repository.delete(transaction)
            onComplete()
        }
    }
}
