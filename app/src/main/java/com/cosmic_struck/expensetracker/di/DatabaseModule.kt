package com.cosmic_struck.expensetracker.di

import android.content.Context
import androidx.room.Room
import com.cosmic_struck.expensetracker.data.local.dao.GoalDao
import com.cosmic_struck.expensetracker.data.local.dao.TransactionDao
import com.cosmic_struck.expensetracker.data.local.database.ExpenseTrackerDatabase
import com.cosmic_struck.expensetracker.data.repository.OfflineFirstFinanceRepository
import com.cosmic_struck.expensetracker.domain.repository.FinanceRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideExpenseTrackerDatabase(
        @ApplicationContext context: Context
    ): ExpenseTrackerDatabase {
        return Room.databaseBuilder(
            context,
            ExpenseTrackerDatabase::class.java,
            "expense_tracker.db"
        ).fallbackToDestructiveMigration(false).build()
    }

    @Provides
    fun provideTransactionDao(database: ExpenseTrackerDatabase): TransactionDao = database.transactionDao()

    @Provides
    fun provideGoalDao(database: ExpenseTrackerDatabase): GoalDao = database.goalDao()

    @Provides
    @Singleton
    fun provideFinanceRepository(
        transactionDao: TransactionDao,
        goalDao: GoalDao
    ): FinanceRepository {
        return OfflineFirstFinanceRepository(
            transactionDao = transactionDao,
            goalDao = goalDao
        )
    }
}
