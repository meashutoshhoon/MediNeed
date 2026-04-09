package com.jb.medineed.app.data.repository

import com.jb.medineed.app.data.local.dao.MedicineDao
import com.jb.medineed.app.data.local.dao.StockTransactionDao
import com.jb.medineed.app.data.local.entity.StockTransactionEntity
import com.jb.medineed.app.data.local.entity.TransactionType
import com.jb.medineed.app.data.local.entity.toDomain
import com.jb.medineed.app.data.local.entity.toEntity
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.StockTransaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.ZoneId

class MedicineRepository(
    private val medicineDao: MedicineDao,
    private val transactionDao: StockTransactionDao
) {

    // ── Medicine CRUD ──────────────────────────────────────────────────────────

    suspend fun addMedicine(medicine: Medicine): Long {
        val id = medicineDao.insertMedicine(medicine.toEntity())
        transactionDao.insertTransaction(
            StockTransactionEntity(
                medicineId = id,
                transactionType = TransactionType.INITIAL,
                quantityChange = medicine.quantity,
                quantityBefore = 0,
                quantityAfter = medicine.quantity,
                note = "Initial stock entry"
            )
        )
        return id
    }

    suspend fun updateMedicine(medicine: Medicine) =
        medicineDao.updateMedicine(medicine.toEntity())

    suspend fun deleteMedicine(medicine: Medicine) =
        medicineDao.deleteMedicine(medicine.toEntity())

    fun getAllMedicines(): Flow<List<Medicine>> =
        medicineDao.getAllMedicines().map { list -> list.map { it.toDomain() } }

    fun getMedicineById(id: Long): Flow<Medicine?> =
        medicineDao.getMedicineById(id).map { it?.toDomain() }

    fun getOutOfStockMedicines(): Flow<List<Medicine>> =
        medicineDao.getOutOfStockMedicines().map { list -> list.map { it.toDomain() } }

    fun getLowStockMedicines(): Flow<List<Medicine>> =
        medicineDao.getLowStockMedicines().map { list -> list.map { it.toDomain() } }

    fun getMedicinesExpiringWithin(days: Long): Flow<List<Medicine>> {
        val now = System.currentTimeMillis()
        val future = LocalDate.now().plusDays(days)
            .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        return medicineDao.getMedicinesExpiringBetween(now, future)
            .map { list -> list.map { it.toDomain() } }
    }

    fun searchMedicines(query: String): Flow<List<Medicine>> =
        medicineDao.searchMedicines(query).map { list -> list.map { it.toDomain() } }

    fun getAllCategories(): Flow<List<String>> = medicineDao.getAllCategories()

    // ── Stats ──────────────────────────────────────────────────────────────────

    fun getTotalCount(): Flow<Int> = medicineDao.getTotalMedicineCount()
    fun getOutOfStockCount(): Flow<Int> = medicineDao.getOutOfStockCount()
    fun getLowStockCount(): Flow<Int> = medicineDao.getLowStockCount()
    fun getTotalInventoryValue(): Flow<Double?> = medicineDao.getTotalInventoryValue()

    // ── Stock Updates ──────────────────────────────────────────────────────────

    suspend fun updateStock(
        medicineId: Long,
        newQuantity: Int,
        type: TransactionType,
        note: String = ""
    ) {
        val medicine = medicineDao.getMedicineByIdOnce(medicineId) ?: return
        val quantityBefore = medicine.quantity
        val change = newQuantity - quantityBefore

        medicineDao.updateMedicine(medicine.copy(quantity = newQuantity, updatedAt = System.currentTimeMillis()))
        transactionDao.insertTransaction(
            StockTransactionEntity(
                medicineId = medicineId,
                transactionType = type,
                quantityChange = change,
                quantityBefore = quantityBefore,
                quantityAfter = newQuantity,
                note = note
            )
        )
    }

    // ── Transactions ───────────────────────────────────────────────────────────

    fun getAllTransactions(): Flow<List<StockTransaction>> =
        transactionDao.getAllTransactions().map { list -> list.map { it.toDomain() } }

    fun getTransactionsBetween(fromMillis: Long, toMillis: Long): Flow<List<StockTransaction>> =
        transactionDao.getTransactionsBetween(fromMillis, toMillis)
            .map { list -> list.map { it.toDomain() } }

    suspend fun getTransactionsByTypeAndPeriod(
        type: TransactionType,
        from: Long,
        to: Long
    ): List<StockTransaction> =
        transactionDao.getTransactionsByTypeAndPeriod(type, from, to).map { it.toDomain() }

    suspend fun getTotalSoldBetween(from: Long, to: Long): Int =
        transactionDao.getTotalSoldBetween(from, to) ?: 0

    suspend fun getMedicinesNeedingAlert(): List<Medicine> =
        medicineDao.getMedicinesNeedingAlert().map { it.toDomain() }
}