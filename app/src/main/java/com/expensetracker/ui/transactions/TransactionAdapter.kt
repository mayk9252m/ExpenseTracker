package com.expensetracker.ui.transactions

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.R
import com.expensetracker.data.model.ExpenseCategory
import com.expensetracker.data.model.IncomeCategory
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import com.expensetracker.util.DateUtils

class TransactionAdapter(
    private val onItemClick: (Transaction) -> Unit
) : ListAdapter<Transaction, TransactionAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // ✅ Inflate manually without ViewBinding to rule out binding issues
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {

        // ✅ Find views manually — no ViewBinding
        private val tvTitle       = itemView.findViewById<TextView>(R.id.tvTitle)
        private val tvCategory    = itemView.findViewById<TextView>(R.id.tvCategory)
        private val tvDate        = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvAmount      = itemView.findViewById<TextView>(R.id.tvAmount)
        private val viewIndicator = itemView.findViewById<View>(R.id.viewIndicator)
        private val tvRecurring   = itemView.findViewById<TextView>(R.id.tvRecurringBadge)

        fun bind(transaction: Transaction) {
            val isExpense = transaction.type == TransactionType.EXPENSE

            tvTitle.text = transaction.title
            tvTitle.setTextColor(Color.parseColor("#111111"))

            tvCategory.text = getCategoryDisplay(transaction)
            tvCategory.setTextColor(Color.parseColor("#777777"))

            tvDate.text = DateUtils.formatDate(transaction.date)
            tvDate.setTextColor(Color.parseColor("#AAAAAA"))

            val sign = if (isExpense) "- ₹" else "+ ₹"
            tvAmount.text = "$sign${String.format("%.2f", transaction.amount)}"
            tvAmount.setTextColor(
                if (isExpense) Color.parseColor("#E53935")
                else Color.parseColor("#43A047")
            )

            viewIndicator.setBackgroundColor(
                if (isExpense) Color.parseColor("#E53935")
                else Color.parseColor("#43A047")
            )

            tvRecurring.visibility =
                if (transaction.isRecurring) View.VISIBLE else View.GONE

            itemView.setBackgroundColor(Color.WHITE)
            itemView.setOnClickListener { onItemClick(transaction) }
        }

        private fun getCategoryDisplay(transaction: Transaction): String {
            return if (transaction.type == TransactionType.EXPENSE) {
                ExpenseCategory.values()
                    .find { it.name == transaction.category }
                    ?.let { "${it.emoji} ${it.displayName}" }
                    ?: transaction.category
            } else {
                IncomeCategory.values()
                    .find { it.name == transaction.category }
                    ?.let { "${it.emoji} ${it.displayName}" }
                    ?: transaction.category
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