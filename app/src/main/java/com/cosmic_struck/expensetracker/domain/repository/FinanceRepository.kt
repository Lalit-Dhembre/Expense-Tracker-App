package com.cosmic_struck.expensetracker.domain.repository

import com.cosmic_struck.expensetracker.domain.model.CategoryExpense
import com.cosmic_struck.expensetracker.domain.model.FinanceInsight
import com.cosmic_struck.expensetracker.domain.model.FinanceTransaction
import com.cosmic_struck.expensetracker.domain.model.FinancialGoal
import kotlinx.coroutines.flow.Flow

interface FinanceRepository {
    fun observeTransactions(): Flow<List<FinanceTransaction>>
    fun observeGoals(): Flow<List<FinancialGoal>>
    fun observeTotalIncome(): Flow<Double>
    fun observeTotalExpense(): Flow<Double>
    fun observeBalance(): Flow<Double>
    fun observeCategoryExpenseBreakdown(): Flow<List<CategoryExpense>>
    fun observeInsights(): Flow<FinanceInsight>

    suspend fun saveTransaction(transaction: FinanceTransaction)
    suspend fun updateTransaction(transaction: FinanceTransaction)
    suspend fun deleteTransaction(transactionId: String)

    suspend fun saveGoal(goal: FinancialGoal)
    suspend fun updateGoal(goal: FinancialGoal)
    suspend fun deleteGoal(goalId: String)
    suspend fun refreshGoalProgress()
}
