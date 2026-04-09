package com.jb.medineed.app.data.local.dao

import androidx.room.*
import com.jb.medineed.app.data.local.entity.StockTransactionEntity
import com.jb.medineed.app.data.local.entity.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface StockTransactionDao {

    @Insert
    suspend fun insertTransaction(transaction: StockTransactionEntity): Long

    @Query("SELECT * FROM stock_transactions WHERE medicineId = :medicineId ORDER BY timestamp DESC")
    fun getTransactionsForMedicine(medicineId: Long): Flow<List<StockTransactionEntity>>

    @Query("SELECT * FROM stock_transactions ORDER BY timestamp DESC")
    fun getAllTransactions(): Flow<List<StockTransactionEntity>>

    @Query("""
        SELECT * FROM stock_transactions 
        WHERE timestamp BETWEEN :fromMillis AND :toMillis 
        ORDER BY timestamp DESC
    """)
    fun getTransactionsBetween(fromMillis: Long, toMillis: Long): Flow<List<StockTransactionEntity>>

    @Query("""
        SELECT * FROM stock_transactions 
        WHERE transactionType = :type
        AND timestamp BETWEEN :fromMillis AND :toMillis 
        ORDER BY timestamp DESC
    """)
    suspend fun getTransactionsByTypeAndPeriod(
        type: TransactionType,
        fromMillis: Long,
        toMillis: Long
    ): List<StockTransactionEntity>

    @Query("SELECT SUM(ABS(quantityChange)) FROM stock_transactions WHERE transactionType = 'SALE' AND timestamp BETWEEN :fromMillis AND :toMillis")
    suspend fun getTotalSoldBetween(fromMillis: Long, toMillis: Long): Int?

    @Query("SELECT COUNT(DISTINCT medicineId) FROM stock_transactions WHERE transactionType = 'SALE' AND timestamp BETWEEN :fromMillis AND :toMillis")
    suspend fun getUniqueMedicinesSoldBetween(fromMillis: Long, toMillis: Long): Int?
}