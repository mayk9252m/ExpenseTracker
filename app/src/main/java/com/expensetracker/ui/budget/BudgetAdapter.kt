package com.expensetracker.ui.budget

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.expensetracker.data.model.Budget
import com.expensetracker.data.model.ExpenseCategory
import com.expensetracker.databinding.ItemBudgetCategoryBinding

class BudgetAdapter(
    private val onSave: (category: String, limit: Double) -> Unit
) : ListAdapter<Budget, BudgetAdapter.ViewHolder>(DiffCallback()) {

    // Show all expense categories, even if no budget is set yet
    private val allCategories = ExpenseCategory.values().toList()

    override fun getItemCount(): Int = allCategories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBudgetCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val category = allCategories[position]
        val existingBudget = currentList.find { it.category == category.name }
        holder.bind(category, existingBudget)
    }

    inner class ViewHolder(private val binding: ItemBudgetCategoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: ExpenseCategory, budget: Budget?) {
            binding.tvCategoryName.text = "${category.emoji} ${category.displayName}"
            if (budget != null) {
                binding.etBudgetLimit.setText(budget.monthlyLimit.toString())
            } else {
                binding.etBudgetLimit.setText("")
            }

            binding.btnSaveBudget.setOnClickListener {
                val limitStr = binding.etBudgetLimit.text.toString()
                val limit = limitStr.toDoubleOrNull()
                if (limit != null && limit > 0) {
                    onSave(category.name, limit)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Budget>() {
        override fun areItemsTheSame(oldItem: Budget, newItem: Budget) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Budget, newItem: Budget) = oldItem == newItem
    }
}
