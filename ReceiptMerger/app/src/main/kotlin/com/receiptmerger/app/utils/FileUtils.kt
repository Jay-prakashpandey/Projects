package com.receiptmerger.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object FileUtils {
    fun getDisplayName(context: Context, uri: Uri): String {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                val name = it.getString(nameIndex)
                name
            } ?: uri.lastPathSegment ?: "file"
        } catch (e: Exception) {
            uri.lastPathSegment ?: "file"
        }
    }

    fun getFileSize(context: Context, uri: Uri): Long {
        return try {
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                it.moveToFirst()
                val sizeIndex = it.getColumnIndex(android.provider.OpenableColumns.SIZE)
                it.getLong(sizeIndex)
            } ?: 0L
        } catch (e: Exception) {
            0L
        }
    }

    fun getMimeType(context: Context, uri: Uri): String {
        return context.contentResolver.getType(uri) ?: "application/octet-stream"
    }

    fun copyToCache(context: Context, uri: Uri, fileName: String): File? {
        return try {
            val cacheFile = File(context.cacheDir, fileName)
            context.contentResolver.openInputStream(uri)?.use { input ->
                cacheFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            cacheFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun deleteFile(file: File): Boolean {
        return try {
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun formatFileSize(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.2f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
