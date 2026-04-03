package com.cosmic_struck.expensetracker.ui.viewmodel

import com.cosmic_struck.expensetracker.domain.model.CategoryExpense
import com.cosmic_struck.expensetracker.domain.model.FinanceTransaction
import com.cosmic_struck.expensetracker.domain.model.FinancialGoal
import com.cosmic_struck.expensetracker.domain.model.WeeklyTrend

data class FinanceDashboardUiState(
    val isLoading: Boolean = true,
    val transactions: List<FinanceTransaction> = emptyList(),
    val goals: List<FinancialGoal> = emptyList(),
    val balance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val categoryBreakdown: List<CategoryExpense> = emptyList(),
    val topSpendingCategory: String? = null,
    val weeklyTrends: List<WeeklyTrend> = emptyList()
)
