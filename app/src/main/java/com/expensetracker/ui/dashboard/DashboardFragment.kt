package com.expensetracker.ui.dashboard

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.expensetracker.R
import com.expensetracker.databinding.FragmentDashboardBinding
import com.expensetracker.ui.transactions.TransactionAdapter
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var recentAdapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecentTransactions()
        setupBarChart()
        observeData()
        setupClickListeners()
    }

    private fun setupRecentTransactions() {
        recentAdapter = TransactionAdapter { }
        binding.rvRecentTransactions.adapter = recentAdapter
        binding.rvRecentTransactions.layoutManager =
            androidx.recyclerview.widget.LinearLayoutManager(requireContext())
    }

    private fun setupBarChart() {
        binding.barChart.apply {
            description.isEnabled = false
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            isDoubleTapToZoomEnabled = false
            setPinchZoom(false)
            setScaleEnabled(false)
            legend.isEnabled = false
            setExtraOffsets(10f, 20f, 10f, 10f)

            // X Axis
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                setDrawGridLines(false)
                granularity = 1f
                textSize = 13f
                textColor = Color.parseColor("#1A1A2E")
                valueFormatter = IndexAxisValueFormatter(arrayOf("Income", "Expenses"))
                setDrawAxisLine(false)
            }

            // Left Y Axis
            axisLeft.apply {
                setDrawGridLines(true)
                gridColor = Color.parseColor("#F0F0F0")
                textColor = Color.parseColor("#6B7280")
                textSize = 11f
                setDrawAxisLine(false)
                axisMinimum = 0f
            }

            // Right Y Axis — hide it
            axisRight.isEnabled = false
        }
    }

    private fun updateBarChart(income: Double, expense: Double) {
        val incomeEntry  = BarEntry(0f, income.toFloat())
        val expenseEntry = BarEntry(1f, expense.toFloat())

        val dataSet = BarDataSet(listOf(incomeEntry, expenseEntry), "").apply {
            // ✅ Income = green, Expense = red
            colors = listOf(
                Color.parseColor("#43A047"),
                Color.parseColor("#E53935")
            )
            valueTextSize = 12f
            valueTextColor = Color.parseColor("#1A1A2E")
            setDrawValues(true)
            valueFormatter = object : com.github.mikephil.charting.formatter.ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return "₹${String.format("%.0f", value)}"
                }
            }
        }

        val barData = BarData(dataSet).apply {
            barWidth = 0.45f
        }

        binding.barChart.apply {
            data = barData
            animateY(600)
            invalidate()
        }
    }

    private fun observeData() {
        viewModel.totalIncome.observe(viewLifecycleOwner) { income ->
            binding.tvTotalIncome.text = "₹${String.format("%.2f", income ?: 0.0)}"
            val expense = viewModel.totalExpenses.value ?: 0.0
            updateBarChart(income ?: 0.0, expense)
        }

        viewModel.totalExpenses.observe(viewLifecycleOwner) { expense ->
            binding.tvTotalExpense.text = "₹${String.format("%.2f", expense ?: 0.0)}"
            val income = viewModel.totalIncome.value ?: 0.0
            updateBarChart(income, expense ?: 0.0)
            viewModel.checkBudgetAndNotify(requireContext())
        }

        viewModel.netBalance.observe(viewLifecycleOwner) { balance ->
            val b = balance ?: 0.0
            binding.tvNetBalance.text = "₹${String.format("%.2f", Math.abs(b))}"
            binding.tvNetBalance.setTextColor(
                if (b >= 0) Color.parseColor("#43A047")
                else Color.parseColor("#E53935")
            )
        }

        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            recentAdapter.submitList(list.take(5))
            binding.tvNoTransactions.visibility =
                if (list.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.overallBudget.observe(viewLifecycleOwner) { budget ->
            if (budget != null) {
                binding.budgetProgressLayout.visibility = View.VISIBLE
                val spent = viewModel.totalExpenses.value ?: 0.0
                val percent = ((spent / budget.monthlyLimit) * 100).toInt().coerceAtMost(100)
                binding.progressBudget.progress = percent

                // ✅ Change progress bar color based on percent
                val progressColor = when {
                    percent >= 75 -> Color.parseColor("#E53935")
                    percent >= 50 -> Color.parseColor("#FF9800")
                    else          -> Color.parseColor("#43A047")
                }
                binding.progressBudget.progressDrawable
                    .setColorFilter(progressColor, android.graphics.PorterDuff.Mode.SRC_IN)

                binding.tvBudgetStatus.text =
                    "₹${String.format("%.2f", spent)} / ₹${String.format("%.2f", budget.monthlyLimit)} ($percent%)"
            } else {
                binding.budgetProgressLayout.visibility = View.GONE
            }
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