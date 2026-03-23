package com.expensetracker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    val category: String,
    val type: TransactionType,        // INCOME or EXPENSE
    val date: Long = System.currentTimeMillis(),
    val note: String = "",
    val isRecurring: Boolean = false,
    val recurringDay: Int = 1         // Day of month for recurring (1–28)
)

enum class TransactionType {
    INCOME, EXPENSE
}

enum class ExpenseCategory(val displayName: String, val emoji: String) {
    FOOD("Food & Dining", "🍔"),
    TRANSPORT("Transport", "🚗"),
    SHOPPING("Shopping", "🛍️"),
    BILLS("Bills & Utilities", "💡"),
    ENTERTAINMENT("Entertainment", "🎬"),
    HEALTH("Health", "🏥"),
    EDUCATION("Education", "📚"),
    OTHER("Other", "📦")
}

enum class IncomeCategory(val displayName: String, val emoji: String) {
    SALARY("Salary", "💼"),
    FREELANCE("Freelance", "💻"),
    INVESTMENT("Investment", "📈"),
    GIFT("Gift", "🎁"),
    OTHER("Other Income", "💰")
}
