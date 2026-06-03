package com.receiptmerger.app.data

data class Receipt(
    val id: String,
    val filePath: String,
    val fileName: String,
    val fileSize: Long,
    val mimeType: String,
)

data class MergeProject(
    val id: String,
    val name: String,
    val template: String,
    val receipts: List<Receipt>,
    val createdAt: Long,
    val modifiedAt: Long,
)
