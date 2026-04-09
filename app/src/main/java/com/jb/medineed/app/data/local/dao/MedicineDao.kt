package com.jb.medineed.app.data.local.dao

import androidx.room.*
import com.jb.medineed.app.data.local.entity.MedicineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MedicineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicine(medicine: MedicineEntity): Long

    @Update
    suspend fun updateMedicine(medicine: MedicineEntity)

    @Delete
    suspend fun deleteMedicine(medicine: MedicineEntity)

    @Query("SELECT * FROM medicines ORDER BY name ASC")
    fun getAllMedicines(): Flow<List<MedicineEntity>>

    @Query("SELECT * FROM medicines WHERE id = :id")
    fun getMedicineById(id: Long): Flow<MedicineEntity?>

    @Query("SELECT * FROM medicines WHERE id = :id")
    suspend fun getMedicineByIdOnce(id: Long): MedicineEntity?

    // Out of stock
    @Query("SELECT * FROM medicines WHERE quantity = 0 ORDER BY name ASC")
    fun getOutOfStockMedicines(): Flow<List<MedicineEntity>>

    // Low stock: quantity > 0 but <= lowStockThreshold
    @Query("SELECT * FROM medicines WHERE quantity > 0 AND quantity <= lowStockThreshold ORDER BY quantity ASC")
    fun getLowStockMedicines(): Flow<List<MedicineEntity>>

    // Expiring within days from now
    @Query("""
        SELECT * FROM medicines 
        WHERE expiryDate BETWEEN :fromMillis AND :toMillis 
        AND quantity > 0
        ORDER BY expiryDate ASC
    """)
    fun getMedicinesExpiringBetween(fromMillis: Long, toMillis: Long): Flow<List<MedicineEntity>>

    // Search
    @Query("""
        SELECT * FROM medicines 
        WHERE name LIKE '%' || :query || '%' 
        OR genericName LIKE '%' || :query || '%'
        OR category LIKE '%' || :query || '%'
        OR batchNumber LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchMedicines(query: String): Flow<List<MedicineEntity>>

    @Query("SELECT DISTINCT category FROM medicines ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Query("SELECT COUNT(*) FROM medicines")
    fun getTotalMedicineCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM medicines WHERE quantity = 0")
    fun getOutOfStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM medicines WHERE quantity > 0 AND quantity <= lowStockThreshold")
    fun getLowStockCount(): Flow<Int>

    @Query("SELECT SUM(quantity * pricePerUnit) FROM medicines")
    fun getTotalInventoryValue(): Flow<Double?>

    @Query("""
        SELECT * FROM medicines 
        WHERE quantity > 0 AND quantity <= lowStockThreshold
        OR quantity = 0
    """)
    suspend fun getMedicinesNeedingAlert(): List<MedicineEntity>
}