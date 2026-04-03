package com.cosmic_struck.expensetracker.data.repository

import com.cosmic_struck.expensetracker.data.local.dao.GoalDao
import com.cosmic_struck.expensetracker.data.local.dao.TransactionDao
import com.cosmic_struck.expensetracker.data.local.entity.GoalEntity
import com.cosmic_struck.expensetracker.data.local.entity.TransactionEntity
import com.cosmic_struck.expensetracker.data.local.entity.TransactionTypeEntity
import com.cosmic_struck.expensetracker.domain.model.CategoryExpense
import com.cosmic_struck.expensetracker.domain.model.FinanceInsight
import com.cosmic_struck.expensetracker.domain.model.FinanceTransaction
import com.cosmic_struck.expensetracker.domain.model.FinancialGoal
import com.cosmic_struck.expensetracker.domain.model.TransactionType
import com.cosmic_struck.expensetracker.domain.model.WeeklyTrend
import com.cosmic_struck.expensetracker.domain.repository.FinanceRepository
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class OfflineFirstFinanceRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val goalDao: GoalDao
) : FinanceRepository {

    override fun observeTransactions(): Flow<List<FinanceTransaction>> {
        return transactionDao.observeAllTransactions().map { entities ->
            entities.map(TransactionEntity::toDomain)
        }
    }

    override fun observeGoals(): Flow<List<FinancialGoal>> {
        return goalDao.observeGoals().map { entities ->
            entities.map(GoalEntity::toDomain)
        }
    }

    override fun observeTotalIncome(): Flow<Double> = transactionDao.observeTotalIncome()

    override fun observeTotalExpense(): Flow<Double> = transactionDao.observeTotalExpense()

    override fun observeBalance(): Flow<Double> {
        return combine(
            observeTotalIncome(),
            observeTotalExpense()
        ) { income, expense ->
            income - expense
        }
    }

    override fun observeCategoryExpenseBreakdown(): Flow<List<CategoryExpense>> {
        return transactionDao.observeCategoryExpenseBreakdown().map { rows ->
            rows.map { CategoryExpense(category = it.category, totalAmount = it.totalAmount) }
        }
    }

    override fun observeInsights(): Flow<FinanceInsight> {
        return combine(
            observeCategoryExpenseBreakdown(),
            observeTransactions()
        ) { categories, transactions ->
            FinanceInsight(
                topSpendingCategory = categories.maxByOrNull(CategoryExpense::totalAmount),
                weeklyTrends = transactions.toWeeklyTrends()
            )
        }
    }

    override suspend fun saveTransaction(transaction: FinanceTransaction) {
        transactionDao.insertTransaction(transaction.toEntity())
        refreshGoalProgress()
    }

    override suspend fun updateTransaction(transaction: FinanceTransaction) {
        transactionDao.updateTransaction(transaction.toEntity())
        refreshGoalProgress()
    }

    override suspend fun deleteTransaction(transactionId: String) {
        transactionDao.getTransactionById(transactionId)?.let { entity ->
            transactionDao.deleteTransaction(entity)
            refreshGoalProgress()
        }
    }

    override suspend fun saveGoal(goal: FinancialGoal) {
        goalDao.insertGoal(goal.toEntity())
        refreshGoalProgress()
    }

    override suspend fun updateGoal(goal: FinancialGoal) {
        goalDao.updateGoal(goal.toEntity())
        refreshGoalProgress()
    }

    override suspend fun deleteGoal(goalId: String) {
        goalDao.getGoalById(goalId)?.let { goal ->
            goalDao.deleteGoal(goal)
        }
    }

    override suspend fun refreshGoalProgress() {
        val goals = observeGoals().first()
        val currentBalance = observeBalance().first().coerceAtLeast(0.0)

        goals.forEach { goal ->
            goalDao.updateGoal(
                goal.copy(currentAmount = currentBalance).toEntity()
            )
        }
    }

}

private fun TransactionEntity.toDomain(): FinanceTransaction {
    return FinanceTransaction(
        id = id,
        amount = amount,
        type = type.toDomain(),
        category = category,
        date = date,
        note = note
    )
}

private fun GoalEntity.toDomain(): FinancialGoal {
    return FinancialGoal(
        id = id,
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        deadline = deadline
    )
}

private fun FinanceTransaction.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = id,
        amount = amount,
        type = type.toEntity(),
        category = category,
        date = date,
        note = note
    )
}

private fun FinancialGoal.toEntity(): GoalEntity {
    return GoalEntity(
        id = id,
        title = title,
        targetAmount = targetAmount,
        currentAmount = currentAmount,
        deadline = deadline
    )
}

private fun TransactionTypeEntity.toDomain(): TransactionType {
    return when (this) {
        TransactionTypeEntity.INCOME -> TransactionType.Income
        TransactionTypeEntity.EXPENSE -> TransactionType.Expense
    }
}

private fun TransactionType.toEntity(): TransactionTypeEntity {
    return when (this) {
        TransactionType.Income -> TransactionTypeEntity.INCOME
        TransactionType.Expense -> TransactionTypeEntity.EXPENSE
    }
}

private fun List<FinanceTransaction>.toWeeklyTrends(): List<WeeklyTrend> {
    if (isEmpty()) return emptyList()

    val zoneId = ZoneId.systemDefault()
    val cutoff = Instant.now()
        .minus(42, ChronoUnit.DAYS)
        .toEpochMilli()

    return asSequence()
        .filter { it.type == TransactionType.Expense && it.date >= cutoff }
        .groupBy { transaction ->
            Instant.ofEpochMilli(transaction.date)
                .atZone(zoneId)
                .toLocalDate()
                .with(java.time.DayOfWeek.MONDAY)
                .atStartOfDay(zoneId)
                .toInstant()
                .toEpochMilli()
        }
        .map { (weekStart, weeklyTransactions) ->
            WeeklyTrend(
                weekStart = weekStart,
                totalExpense = weeklyTransactions.sumOf(FinanceTransaction::amount)
            )
        }
        .sortedBy(WeeklyTrend::weekStart)
        .toList()
}
