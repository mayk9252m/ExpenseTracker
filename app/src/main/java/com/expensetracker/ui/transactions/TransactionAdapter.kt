package com.expensetracker.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.R
import com.expensetracker.data.model.ExpenseCategory
import com.expensetracker.data.model.IncomeCategory
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.databinding.ItemTransactionBinding
import com.expensetracker.util.DateUtils

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: Transaction) {
            binding.tvTitle.text = transaction.title
            binding.tvDate.text = DateUtils.formatDate(transaction.date)
            binding.tvCategory.text = getCategoryDisplay(transaction)

            val isExpense = transaction.type == TransactionType.EXPENSE
            val sign = if (isExpense) "- ₹" else "+ ₹"
            binding.tvAmount.text = "$sign${String.format("%.2f", transaction.amount)}"
            binding.tvAmount.setTextColor(
                ContextCompat.getColor(
                    binding.root.context,
                    if (isExpense) R.color.expense_red else R.color.income_green
                )
            )

            if (transaction.isRecurring) {
                binding.ivRecurring.visibility = android.view.View.VISIBLE
            } else {
                binding.ivRecurring.visibility = android.view.View.GONE
            }

            binding.root.setOnClickListener { onItemClick(transaction) }
        }

        private fun getCategoryDisplay(transaction: Transaction): String {
            return if (transaction.type == TransactionType.EXPENSE) {
                val cat = ExpenseCategory.values().find { it.name == transaction.category }
                cat?.let { "${it.emoji} ${it.displayName}" } ?: transaction.category
            } else {
                val cat = IncomeCategory.values().find { it.name == transaction.category }
                cat?.let { "${it.emoji} ${it.displayName}" } ?: transaction.category
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Transaction>() {
        override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction) =
            oldItem == newItem
    }
}
