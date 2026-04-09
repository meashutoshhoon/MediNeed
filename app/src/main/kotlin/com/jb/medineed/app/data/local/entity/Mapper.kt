package com.jb.medineed.app.data.local.entity

import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.StockTransaction
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

fun MedicineEntity.toDomain(): Medicine = Medicine(
    id = id,
    name = name,
    genericName = genericName,
    batchNumber = batchNumber,
    category = category,
    quantity = quantity,
    lowStockThreshold = lowStockThreshold,
    manufacturingDate = Instant.ofEpochMilli(manufacturingDate)
        .atZone(ZoneId.systemDefault()).toLocalDate(),
    expiryDate = Instant.ofEpochMilli(expiryDate)
        .atZone(ZoneId.systemDefault()).toLocalDate(),
    pricePerUnit = pricePerUnit,
    supplier = supplier,
    createdAt = createdAt,
    updatedAt = updatedAt
)

fun Medicine.toEntity(): MedicineEntity = MedicineEntity(
    id = id,
    name = name,
    genericName = genericName,
    batchNumber = batchNumber,
    category = category,
    quantity = quantity,
    lowStockThreshold = lowStockThreshold,
    manufacturingDate = manufacturingDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    expiryDate = expiryDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli(),
    pricePerUnit = pricePerUnit,
    supplier = supplier,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis()
)

fun StockTransactionEntity.toDomain(medicineName: String = ""): StockTransaction = StockTransaction(
    id = id,
    medicineId = medicineId,
    medicineName = medicineName,
    transactionType = transactionType,
    quantityChange = quantityChange,
    quantityBefore = quantityBefore,
    quantityAfter = quantityAfter,
    note = note,
    timestamp = timestamp
)

fun StockTransaction.toEntity(): StockTransactionEntity = StockTransactionEntity(
    id = id,
    medicineId = medicineId,
    transactionType = transactionType,
    quantityChange = quantityChange,
    quantityBefore = quantityBefore,
    quantityAfter = quantityAfter,
    note = note,
    timestamp = timestamp
)