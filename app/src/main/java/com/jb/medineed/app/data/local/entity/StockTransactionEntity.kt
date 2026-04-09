package com.jb.medineed.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TransactionType { SALE, RESTOCK, ADJUSTMENT, INITIAL }

@Entity(
    tableName = "stock_transactions",
    foreignKeys = [
        ForeignKey(
            entity = MedicineEntity::class,
            parentColumns = ["id"],
            childColumns = ["medicineId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("medicineId")]
)
data class StockTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val medicineId: Long,
    val transactionType: TransactionType,
    val quantityChange: Int,          // Negative for sales, positive for restock
    val quantityBefore: Int,
    val quantityAfter: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)