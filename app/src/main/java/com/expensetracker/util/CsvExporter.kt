package com.expensetracker.util

import android.content.Context
import android.content.Intent
import android.os.Environment
import androidx.core.content.FileProvider
import com.expensetracker.data.model.Transaction
import com.expensetracker.data.model.TransactionType
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {

    fun exportTransactions(context: Context, transactions: List<Transaction>): Intent? {
        return try {
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            val fileName = "expenses_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}.csv"

            val exportDir = File(context.getExternalFilesDir(null), "exports")
            exportDir.mkdirs()

            val file = File(exportDir, fileName)
            val writer = FileWriter(file)

            // CSV Header
            writer.append("Date,Title,Category,Type,Amount,Note\n")

            // CSV Rows
            transactions.forEach { t ->
                val date = dateFormat.format(Date(t.date))
                val type = if (t.type == TransactionType.INCOME) "Income" else "Expense"
                val amount = String.format("%.2f", t.amount)
                // Escape commas in strings
                val title = "\"${t.title.replace("\"", "\"\"")}\""
                val note = "\"${t.note.replace("\"", "\"\"")}\""
                writer.append("$date,$title,${t.category},$type,$amount,$note\n")
            }

            writer.flush()
            writer.close()

            // Create share intent
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Expense History Export")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
