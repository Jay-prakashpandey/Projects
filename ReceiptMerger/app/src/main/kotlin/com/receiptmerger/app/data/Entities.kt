package com.receiptmerger.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "receipts")
data class ReceiptEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val projectId: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "merge_projects")
data class MergeProjectEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val template: String,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis(),
    val outputPath: String = "",
    val status: String = "draft", // draft, processing, completed, failed
)
