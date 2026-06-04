package com.receiptmerger.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.ParcelFileDescriptor
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.default
import id.zelory.compressor.constraint.destination
import java.io.File
import java.io.FileOutputStream

object ImageProcessor {
    private const val PDF_RENDER_SCALE = 2

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

    /**
     * Crop whitespace from image boundaries
     * @param context Android context
     * @param imageFile Input image file
     * @param tolerance Color tolerance for whitespace detection (0-255)
     * @return Cropped image file
     */
    fun cropWhitespace(
        context: Context,
        imageFile: File,
        tolerance: Int = 20,
    ): File {
        return try {
            val originalBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
            val croppedBitmap = cropBitmap(originalBitmap, tolerance)
            
            val outputFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
            saveBitmapToFile(croppedBitmap, outputFile, Bitmap.CompressFormat.JPEG, 92)
            
            originalBitmap.recycle()
            croppedBitmap.recycle()
            outputFile
        } catch (e: Exception) {
            e.printStackTrace()
            imageFile // Return original if cropping fails
        }
    }

    /**
     * Load image from file and return as Bitmap
     */
    fun loadBitmapFromFile(imageFile: File): Bitmap? {
        return try {
            BitmapFactory.decodeFile(imageFile.absolutePath)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Load image from URI and return as Bitmap
     */
    fun loadBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.use { 
                BitmapFactory.decodeStream(it)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Save Bitmap to file
     */
    fun saveBitmapToFile(
        bitmap: Bitmap,
        outputFile: File,
        format: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
        quality: Int = 100,
    ): Boolean {
        return try {
            FileOutputStream(outputFile).use { fos ->
                bitmap.compress(format, quality, fos)
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun renderPdfPageToBitmap(
        context: Context,
        pdfFile: File,
        pageIndex: Int = 0,
        renderScale: Int = 1,
    ): Bitmap? {
        return try {
            val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
            val renderer = PdfRenderer(fileDescriptor)
            if (pageIndex >= renderer.pageCount) {
                renderer.close()
                fileDescriptor.close()
                return null
            }

            val page = renderer.openPage(pageIndex)
            val scale = renderScale.coerceAtLeast(1)
            val bitmap = Bitmap.createBitmap(page.width * scale, page.height * scale, Bitmap.Config.ARGB_8888)
            Canvas(bitmap).drawColor(Color.WHITE)
            val matrix = Matrix().apply { postScale(scale.toFloat(), scale.toFloat()) }
            page.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_PRINT)
            page.close()
            renderer.close()
            fileDescriptor.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun renderPdfPageToCroppedImage(context: Context, pdfFile: File, tolerance: Int = 20): File? {
        val renderedBitmap = renderPdfPageToBitmap(context, pdfFile, renderScale = PDF_RENDER_SCALE) ?: return null
        val croppedBitmap = cropBitmap(renderedBitmap, tolerance)
        val outputFile = File(context.cacheDir, "pdf_render_${System.currentTimeMillis()}.jpg")

        val saved = saveBitmapToFile(croppedBitmap, outputFile, Bitmap.CompressFormat.JPEG, 92)
        if (croppedBitmap !== renderedBitmap) {
            renderedBitmap.recycle()
            croppedBitmap.recycle()
        } else {
            renderedBitmap.recycle()
        }

        return if (saved) outputFile else null
    }

    fun renderPdfPageToImage(context: Context, pdfFile: File): File? {
        val renderedBitmap = renderPdfPageToBitmap(context, pdfFile, renderScale = PDF_RENDER_SCALE) ?: return null
        val outputFile = File(context.cacheDir, "pdf_page_${System.currentTimeMillis()}.jpg")
        val saved = saveBitmapToFile(renderedBitmap, outputFile, Bitmap.CompressFormat.JPEG, 92)
        renderedBitmap.recycle()

        return if (saved) outputFile else null
    }

    fun convertDocumentToCroppedImage(context: Context, file: File, tolerance: Int = 20): File? {
        return if (file.extension.equals("pdf", true)) {
            renderPdfPageToCroppedImage(context, file, tolerance)
        } else {
            cropWhitespace(context, file, tolerance)
        }
    }

    /**
     * Crop whitespace from bitmap edges
     * @param bitmap Input bitmap
     * @param tolerance Color tolerance for whitespace detection
     * @return Cropped bitmap
     */
    private fun cropBitmap(bitmap: Bitmap, tolerance: Int = 20): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scanStep = if (maxOf(width, height) > 1400) 2 else 1
        
        var minX = width
        var minY = height
        var maxX = 0
        var maxY = 0
        
        for (y in 0 until height step scanStep) {
            for (x in 0 until width step scanStep) {
                val pixel = bitmap.getPixel(x, y)
                if (!isWhitish(pixel, tolerance)) {
                    minX = minOf(minX, x)
                    maxX = maxOf(maxX, x)
                    minY = minOf(minY, y)
                    maxY = maxOf(maxY, y)
                }
            }
        }
        
        // Add small padding to avoid cutting too close
        val padding = 8 * scanStep
        minX = maxOf(0, minX - padding)
        minY = maxOf(0, minY - padding)
        maxX = minOf(width - 1, maxX + padding)
        maxY = minOf(height - 1, maxY + padding)
        
        val newWidth = maxX - minX + 1
        val newHeight = maxY - minY + 1
        
        return if (newWidth > 0 && newHeight > 0) {
            Bitmap.createBitmap(bitmap, minX, minY, newWidth, newHeight)
        } else {
            bitmap
        }
    }

    /**
     * Check if a pixel color is whitish
     */
    private fun isWhitish(pixel: Int, tolerance: Int): Boolean {
        val r = (pixel shr 16) and 0xFF
        val g = (pixel shr 8) and 0xFF
        val b = pixel and 0xFF
        val a = (pixel shr 24) and 0xFF
        
        // If mostly transparent, consider it whitespace
        if (a < 128) return true
        
        // If all RGB components are close to white (255)
        return r >= 255 - tolerance && g >= 255 - tolerance && b >= 255 - tolerance
    }
}
