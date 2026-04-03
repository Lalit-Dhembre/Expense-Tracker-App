package com.cosmic_struck.expensetracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cosmic_struck.expensetracker.domain.model.FinanceTransaction
import com.cosmic_struck.expensetracker.domain.model.FinancialGoal
import com.cosmic_struck.expensetracker.domain.repository.FinanceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class FinanceDashboardViewModel @Inject constructor(
    private val financeRepository: FinanceRepository
) : ViewModel() {

    init {
        viewModelScope.launch {
            financeRepository.refreshGoalProgress()
        }
    }

    private val summaryFlow = combine(
        financeRepository.observeTransactions(),
        financeRepository.observeGoals(),
        financeRepository.observeTotalIncome(),
        financeRepository.observeTotalExpense(),
        financeRepository.observeBalance()
    ) { transactions, goals, income, expense, balance ->
        InterimDashboardState(
            transactions = transactions,
            goals = goals,
            totalIncome = income,
            totalExpense = expense,
            balance = balance
        )
    }

    val uiState: StateFlow<FinanceDashboardUiState> = combine(
        summaryFlow,
        financeRepository.observeCategoryExpenseBreakdown(),
        financeRepository.observeInsights()
    ) { summary, categories, insights ->
        FinanceDashboardUiState(
            isLoading = false,
            transactions = summary.transactions,
            goals = summary.goals,
            balance = summary.balance,
            totalIncome = summary.totalIncome,
            totalExpense = summary.totalExpense,
            categoryBreakdown = categories,
            topSpendingCategory = insights.topSpendingCategory?.category,
            weeklyTrends = insights.weeklyTrends
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = FinanceDashboardUiState()
    )

    fun saveTransaction(transaction: FinanceTransaction) {
        viewModelScope.launch {
            financeRepository.saveTransaction(transaction)
        }
    }

    fun updateTransaction(transaction: FinanceTransaction) {
        viewModelScope.launch {
            financeRepository.updateTransaction(transaction)
        }
    }

    fun deleteTransaction(transactionId: String) {
        viewModelScope.launch {
            financeRepository.deleteTransaction(transactionId)
        }
    }

    fun saveGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            financeRepository.saveGoal(goal)
        }
    }

    fun updateGoal(goal: FinancialGoal) {
        viewModelScope.launch {
            financeRepository.updateGoal(goal)
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            financeRepository.deleteGoal(goalId)
        }
    }

    fun refreshGoalProgress() {
        viewModelScope.launch {
            financeRepository.refreshGoalProgress()
        }
    }
}

private data class InterimDashboardState(
    val transactions: List<FinanceTransaction>,
    val goals: List<FinancialGoal>,
    val totalIncome: Double,
    val totalExpense: Double,
    val balance: Double
)
