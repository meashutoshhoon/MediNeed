package com.jb.medineed.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jb.medineed.app.data.local.dao.MedicineDao
import com.jb.medineed.app.data.local.dao.StockTransactionDao
import com.jb.medineed.app.data.local.entity.MedicineEntity
import com.jb.medineed.app.data.local.entity.StockTransactionEntity

@Database(
    entities = [MedicineEntity::class, StockTransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MediStockDatabase : RoomDatabase() {

    abstract fun medicineDao(): MedicineDao
    abstract fun stockTransactionDao(): StockTransactionDao

    companion object {
        const val DATABASE_NAME = "medistock_db"
    }
}