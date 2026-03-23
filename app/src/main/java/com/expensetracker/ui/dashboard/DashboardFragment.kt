package com.expensetracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.expensetracker.R
import com.expensetracker.databinding.FragmentDashboardBinding
import com.expensetracker.ui.transactions.TransactionAdapter
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recentAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fix 1
        (activity as AppCompatActivity).supportActionBar?.apply {
            title = "MK Wallet"
            show()
        }

        setupRecentTransactions()
        setupPieChart()
        observeData()
        setupClickListeners()
    }

    private fun setupRecentTransactions() {
        recentAdapter = TransactionAdapter { transaction ->
            // Navigate to edit
        }
        // fix 2
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = recentAdapter
    }

    private fun setupPieChart() {
        binding.pieChart.apply {
            description.isEnabled = false
            isDrawHoleEnabled = true
            holeRadius = 58f
            setHoleColor(Color.TRANSPARENT)
            transparentCircleRadius = 61f
            legend.isEnabled = false
            setDrawEntryLabels(false)
        }
    }

    private fun observeData() {
        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = "₹${String.format("%.2f", income ?: 0.0)}"
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { expense ->
            binding.tvTotalExpense.text = "₹${String.format("%.2f", expense ?: 0.0)}"
            viewModel.checkBudgetAndNotify(requireContext())
        }

        viewModel.netBalance.observe(viewLifecycleOwner) { balance ->
            val b = balance ?: 0.0
            binding.tvNetBalance.text = "₹${String.format("%.2f", Math.abs(b))}"
            binding.tvNetBalance.setTextColor(
                if (b >= 0) requireContext().getColor(R.color.income_green)
                else requireContext().getColor(R.color.expense_red)
            )
        }

        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            recentAdapter.submitList(list.take(5))
            binding.tvNoTransactions.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.overallBudget.observe(viewLifecycleOwner) { budget ->
            if (budget != null) {
                binding.budgetProgressLayout.visibility = View.VISIBLE
                val spent = viewModel.totalExpenses.value ?: 0.0
                val percent = ((spent / budget.monthlyLimit) * 100).toInt().coerceAtMost(100)
                binding.progressBudget.progress = percent
                binding.tvBudgetStatus.text = "₹${String.format("%.2f", spent)} / ₹${String.format("%.2f", budget.monthlyLimit)} ($percent%)"
            } else {
                binding.budgetProgressLayout.visibility = View.GONE
            }
        }

        viewModel.expensesByCategory.observe(viewLifecycleOwner) { categorySums ->
            if (categorySums.isNullOrEmpty()) {
                binding.pieChart.visibility = View.GONE
                return@observe
            }
            binding.pieChart.visibility = View.VISIBLE

            val entries = categorySums.map { PieEntry(it.total.toFloat(), it.category) }
            val colors = listOf(
                Color.parseColor("#FF6B6B"), Color.parseColor("#4ECDC4"),
                Color.parseColor("#45B7D1"), Color.parseColor("#96CEB4"),
                Color.parseColor("#FFEAA7"), Color.parseColor("#DDA0DD"),
                Color.parseColor("#98D8C8"), Color.parseColor("#F7DC6F")
            )

            val dataSet = PieDataSet(entries, "Expenses").apply {
                this.colors = colors
                sliceSpace = 3f
                selectionShift = 5f
            }

            binding.pieChart.data = PieData(dataSet)
            binding.pieChart.invalidate()
        }
    }

    private fun setupClickListeners() {
        binding.fabAddTransaction.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_addTransaction)
        }

        binding.tvSeeAllTransactions.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_transactions)
        }

        binding.cardBudget.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_budget)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
