package com.cosmic_struck.expensetracker.domain.model

data class FinanceTransaction(
    val id: String,
    val amount: Double,
    val type: TransactionType,
    val category: String,
    val date: Long,
    val note: String? = null
)

enum class TransactionType {
    Income,
    Expense
}
