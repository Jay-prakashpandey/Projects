package com.receiptmerger.app.utils

import android.content.Context
import android.net.Uri
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import java.io.File

object ImageProcessor {
    suspend fun compressImage(
        context: Context,
        imageUri: Uri,
        outputDir: File = context.cacheDir,
    ): File {
        return try {
            val inputStream = context.contentResolver.openInputStream(imageUri)
            val tempFile = File(outputDir, "temp_image_${System.currentTimeMillis()}.jpg")
            
            inputStream?.use { input ->
                tempFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }

            val compressedFile = Compressor.compress(context, tempFile) {
                default()
                destination(outputDir)
            }
            
            tempFile.delete()
            compressedFile
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }

    fun getImageDimensions(context: Context, uri: Uri): Pair<Int, Int>? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use {
                val options = android.graphics.BitmapFactory.Options().apply {
                    inJustDecodeBounds = true
                }
                android.graphics.BitmapFactory.decodeStream(it, null, options)
                Pair(options.outWidth, options.outHeight)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun rotateImageIfNeeded(file: File, rotation: Int): File {
        // Placeholder for image rotation using EXIF data or manual rotation
        return file
    }
}
