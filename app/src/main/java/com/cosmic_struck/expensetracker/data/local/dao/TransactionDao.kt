package com.cosmic_struck.expensetracker.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.cosmic_struck.expensetracker.data.local.entity.TransactionEntity
import com.cosmic_struck.expensetracker.data.local.model.CategoryExpenseTotalEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun observeAllTransactions(): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE id = :transactionId LIMIT 1")
    suspend fun getTransactionById(transactionId: String): TransactionEntity?

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0)
        FROM transactions
        WHERE type = 'INCOME'
        """
    )
    fun observeTotalIncome(): Flow<Double>

    @Query(
        """
        SELECT COALESCE(SUM(amount), 0.0)
        FROM transactions
        WHERE type = 'EXPENSE'
        """
    )
    fun observeTotalExpense(): Flow<Double>

    @Query(
        """
        SELECT category, COALESCE(SUM(amount), 0.0) AS totalAmount
        FROM transactions
        WHERE type = 'EXPENSE'
        GROUP BY category
        ORDER BY totalAmount DESC
        """
    )
    fun observeCategoryExpenseBreakdown(): Flow<List<CategoryExpenseTotalEntity>>
}
