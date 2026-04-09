package com.jb.medineed.app.domain.model

import com.jb.medineed.app.data.local.entity.TransactionType
import java.time.LocalDate

data class Medicine(
    val id: Long = 0,
    val name: String,
    val genericName: String,
    val batchNumber: String,
    val category: String,
    val quantity: Int,
    val lowStockThreshold: Int = 10,
    val manufacturingDate: LocalDate,
    val expiryDate: LocalDate,
    val pricePerUnit: Double = 0.0,
    val supplier: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isOutOfStock: Boolean get() = quantity == 0
    val isLowStock: Boolean get() = quantity in 1..lowStockThreshold
    val daysUntilExpiry: Long get() {
        val today = LocalDate.now()
        return today.until(expiryDate, java.time.temporal.ChronoUnit.DAYS)
    }
    val isExpired: Boolean get() = expiryDate.isBefore(LocalDate.now())
    val stockStatus: StockStatus get() = when {
        isOutOfStock -> StockStatus.OUT_OF_STOCK
        isLowStock -> StockStatus.LOW_STOCK
        else -> StockStatus.IN_STOCK
    }
}

enum class StockStatus { IN_STOCK, LOW_STOCK, OUT_OF_STOCK }

data class StockTransaction(
    val id: Long = 0,
    val medicineId: Long,
    val medicineName: String = "",
    val transactionType: TransactionType,
    val quantityChange: Int,
    val quantityBefore: Int,
    val quantityAfter: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

enum class ExpiryFilter(val label: String, val days: Long) {
    ONE_WEEK("1 Week", 7),
    ONE_MONTH("1 Month", 30),
    SIX_MONTHS("6 Months", 180)
}

enum class SortOrder(val label: String) {
    NAME_ASC("Name A-Z"),
    NAME_DESC("Name Z-A"),
    QUANTITY_ASC("Qty: Low to High"),
    QUANTITY_DESC("Qty: High to Low"),
    EXPIRY_ASC("Expiry: Soonest"),
    EXPIRY_DESC("Expiry: Latest")
}