package com.jb.medineed.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "medicines")
data class MedicineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val genericName: String,
    val batchNumber: String,
    val category: String,
    val quantity: Int,
    val lowStockThreshold: Int = 10,
    val manufacturingDate: Long,   // Stored as epoch millis
    val expiryDate: Long,          // Stored as epoch millis
    val pricePerUnit: Double = 0.0,
    val supplier: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)