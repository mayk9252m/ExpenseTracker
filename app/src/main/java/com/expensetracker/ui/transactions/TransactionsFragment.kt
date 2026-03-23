package com.expensetracker.ui.transactions

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import com.expensetracker.R
import com.expensetracker.databinding.FragmentTransactionsBinding
import com.expensetracker.util.CsvExporter

class TransactionsFragment : Fragment() {

    private var _binding: FragmentTransactionsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TransactionsViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        setupFilterChips()
        observeData()

        binding.fabAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }
    }

    private fun setupMenu() {
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_transactions, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_export -> {
                        exportToCsv()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            // TODO: Open edit/detail dialog
        }
        binding.rvTransactions.adapter = adapter
    }

    private fun setupFilterChips() {
        binding.chipAll.isChecked = true

        binding.chipGroupFilter.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipIncome) -> viewModel.setFilter("INCOME")
                checkedIds.contains(R.id.chipExpense) -> viewModel.setFilter("EXPENSE")
                else -> viewModel.setFilter("ALL")
            }
        }
    }

    private fun observeData() {
        viewModel.transactions.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)

            // ✅ Show/hide empty state message
            if (list.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvTransactions.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvTransactions.visibility = View.VISIBLE
            }
        }
    }

    private fun exportToCsv() {
        viewModel.getAllTransactionsForExport { transactions ->
            val intent = CsvExporter.exportTransactions(requireContext(), transactions)
            if (intent != null) {
                startActivity(Intent.createChooser(intent, "Export Expenses"))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
