package com.cosmic_struck.expensetracker.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cosmic_struck.expensetracker.data.local.dao.GoalDao
import com.cosmic_struck.expensetracker.data.local.dao.TransactionDao
import com.cosmic_struck.expensetracker.data.local.entity.GoalEntity
import com.cosmic_struck.expensetracker.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(FinanceTypeConverters::class)
abstract class ExpenseTrackerDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun goalDao(): GoalDao
}
