package com.cosmic_struck.expensetracker.domain.model

data class FinanceInsight(
    val topSpendingCategory: CategoryExpense?,
    val weeklyTrends: List<WeeklyTrend>
)
