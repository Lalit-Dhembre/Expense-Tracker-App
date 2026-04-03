package com.cosmic_struck.expensetracker.domain.model

data class FinancialGoal(
    val id: String,
    val title: String,
    val targetAmount: Double,
    val currentAmount: Double,
    val deadline: Long
) {
    val progress: Float
        get() = if (targetAmount <= 0.0) 0f else (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f)
}
