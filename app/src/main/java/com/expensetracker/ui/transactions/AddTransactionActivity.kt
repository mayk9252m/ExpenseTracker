package com.expensetracker.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.expensetracker.R
import com.expensetracker.data.model.ExpenseCategory
import com.expensetracker.data.model.IncomeCategory
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.databinding.ActivityAddTransactionBinding
import com.expensetracker.util.DateUtils
import java.util.*

class AddTransactionActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddTransactionBinding
    private val viewModel: AddTransactionViewModel by viewModels()
    private var selectedDate = System.currentTimeMillis()
    private var editingTransaction: Transaction? = null

    companion object {
        const val EXTRA_TRANSACTION_ID = "extra_transaction_id"
        const val EXTRA_TRANSACTION_TYPE = "extra_transaction_type"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Add Transaction"

        setupTypeToggle()
        setupDatePicker()
        setupSaveButton()

        // Check if editing an existing transaction (for future use)
        val defaultType = intent.getStringExtra(EXTRA_TRANSACTION_TYPE) ?: "EXPENSE"
        if (defaultType == "INCOME") {
            binding.toggleType.check(R.id.btnIncome)
        } else {
            binding.toggleType.check(R.id.btnExpense)
        }
        updateCategoryList()
    }

    private fun setupTypeToggle() {
        binding.toggleType.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) updateCategoryList()
        }
    }

    private fun updateCategoryList() {
        val isExpense = binding.toggleType.checkedButtonId == R.id.btnExpense
        val categories = if (isExpense) {
            ExpenseCategory.values().map { "${it.emoji} ${it.displayName}" }
        } else {
            IncomeCategory.values().map { "${it.emoji} ${it.displayName}" }
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, categories)
        binding.actvCategory.setAdapter(adapter)
        binding.actvCategory.setText("", false)
    }

    private fun setupDatePicker() {
        binding.tvDate.setText(DateUtils.formatDate(selectedDate))
        binding.tvDate.setOnClickListener {
            val cal = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    cal.set(year, month, day)
                    selectedDate = cal.timeInMillis
                    binding.tvDate.setText(DateUtils.formatDate(selectedDate))
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupSaveButton() {
        binding.btnSave.setOnClickListener {
            val title = binding.etTitle.text.toString().trim()
            val amountStr = binding.etAmount.text.toString().trim()
            val categoryDisplay = binding.actvCategory.text.toString().trim()
            val note = binding.etNote.text.toString().trim()
            val isRecurring = binding.switchRecurring.isChecked
            val recurringDay = binding.etRecurringDay.text.toString().toIntOrNull() ?: 1

            // Validate
            if (title.isEmpty()) {
                binding.tilTitle.error = "Please enter a title"; return@setOnClickListener
            }
            if (amountStr.isEmpty()) {
                binding.tilAmount.error = "Please enter an amount"; return@setOnClickListener
            }
            val amount = amountStr.toDoubleOrNull()
            if (amount == null || amount <= 0) {
                binding.tilAmount.error = "Please enter a valid amount"; return@setOnClickListener
            }
            if (categoryDisplay.isEmpty()) {
                binding.tilCategory.error = "Please select a category"; return@setOnClickListener
            }

            val isExpense = binding.toggleType.checkedButtonId == R.id.btnExpense
            val type = if (isExpense) TransactionType.EXPENSE else TransactionType.INCOME

            // Extract category key from display name (remove emoji prefix)
            val categoryKey = extractCategoryKey(categoryDisplay, isExpense)

            val transaction = Transaction(
                id = editingTransaction?.id ?: 0,
                title = title,
                amount = amount,
                category = categoryKey,
                type = type,
                date = selectedDate,
                note = note,
                isRecurring = isRecurring,
                recurringDay = recurringDay.coerceIn(1, 28)
            )

            if (editingTransaction != null) {
                viewModel.updateTransaction(transaction) {
                    Toast.makeText(this, "Transaction updated!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                viewModel.insertTransaction(transaction) {
                    Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }

        // Toggle recurring day field visibility
        binding.switchRecurring.setOnCheckedChangeListener { _, isChecked ->
            binding.tilRecurringDay.visibility = if (isChecked) View.VISIBLE else View.GONE
        }
    }

    private fun extractCategoryKey(displayName: String, isExpense: Boolean): String {
        val name = displayName.substringAfter(" ").trim()
        return if (isExpense) {
            ExpenseCategory.values().find { it.displayName == name }?.name ?: "OTHER"
        } else {
            IncomeCategory.values().find { it.displayName == name }?.name ?: "OTHER"
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}
