package com.cosmic_struck.expensetracker.domain.model

data class FinanceSummary(
    val transactions: List<FinanceTransaction> = emptyList(),
    val goals: List<FinancialGoal> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,
    val categoryExpenses: List<CategoryExpense> = emptyList(),
    val insights: FinanceInsight = FinanceInsight(
        topSpendingCategory = null,
        weeklyTrends = emptyList()
    )
)
