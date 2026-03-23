package com.expensetracker.ui.budget

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.databinding.ActivityBudgetBinding

class BudgetActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBudgetBinding
    private val viewModel: BudgetViewModel by viewModels()
    private lateinit var adapter: BudgetAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBudgetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Manage Budget"

        setupRecyclerView()
        setupOverallBudget()
        observeData()
    }

    private fun setupRecyclerView() {
        adapter = BudgetAdapter { category, limit ->
            viewModel.saveCategoryBudget(category, limit)
            Toast.makeText(this, "Budget saved for $category", Toast.LENGTH_SHORT).show()
        }
        binding.rvCategoryBudgets.layoutManager = LinearLayoutManager(this)
        binding.rvCategoryBudgets.adapter = adapter
    }

    private fun setupOverallBudget() {
        binding.btnSaveOverall.setOnClickListener {
            val limitStr = binding.etOverallBudget.text.toString()
            val limit = limitStr.toDoubleOrNull()
            if (limit == null || limit <= 0) {
                binding.tilOverallBudget.error = "Enter a valid budget amount"
                return@setOnClickListener
            }
            viewModel.saveOverallBudget(limit)
            Toast.makeText(this, "Overall budget saved!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun observeData() {
        viewModel.overallBudget.observe(this) { budget ->
            if (budget != null) {
                binding.etOverallBudget.setText(budget.monthlyLimit.toString())
            }
        }

        viewModel.categoryBudgets.observe(this) { budgets ->
            adapter.submitList(budgets)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }
}
