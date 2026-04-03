package com.cosmic_struck.expensetracker.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey
    val id: String,
    val amount: Double,
    val type: TransactionTypeEntity,
    val category: String,
    val date: Long,
    val note: String? = null
)

enum class TransactionTypeEntity {
    INCOME,
    EXPENSE
}
