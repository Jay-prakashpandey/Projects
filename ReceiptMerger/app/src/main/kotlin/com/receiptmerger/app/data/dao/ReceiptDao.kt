package com.receiptmerger.app.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.receiptmerger.app.data.ReceiptEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Insert
    suspend fun insertReceipt(receipt: ReceiptEntity)

    @Insert
    suspend fun insertReceipts(receipts: List<ReceiptEntity>)

    @Update
    suspend fun updateReceipt(receipt: ReceiptEntity)

    @Delete
    suspend fun deleteReceipt(receipt: ReceiptEntity)

    @Query("SELECT * FROM receipts WHERE projectId = :projectId")
    fun getProjectReceipts(projectId: String): Flow<List<ReceiptEntity>>

    @Query("SELECT * FROM receipts WHERE id = :id")
    suspend fun getReceiptById(id: Int): ReceiptEntity?

    @Query("DELETE FROM receipts WHERE projectId = :projectId")
    suspend fun deleteProjectReceipts(projectId: String)
}
