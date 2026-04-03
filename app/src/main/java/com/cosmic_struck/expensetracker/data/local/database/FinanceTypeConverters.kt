package com.cosmic_struck.expensetracker.data.local.database

import androidx.room.TypeConverter
import com.cosmic_struck.expensetracker.data.local.entity.TransactionTypeEntity

class FinanceTypeConverters {

    @TypeConverter
    fun fromTransactionType(value: TransactionTypeEntity): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionTypeEntity = TransactionTypeEntity.valueOf(value)
}
