package com.receiptmerger.app.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

object ShareUtils {
    fun openPdf(context: Context, pdfPath: String) {
        try {
            val file = File(pdfPath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, "application/pdf")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(openIntent, "Open PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun sharePdf(context: Context, pdfPath: String) {
        try {
            val file = File(pdfPath)
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun savePdfToDownloads(context: Context, pdfPath: String, fileName: String): Boolean {
        return try {
            val sourceFile = File(pdfPath)
            val downloadsDir = context.getExternalFilesDir(null)
            val destFile = File(downloadsDir, fileName)

            sourceFile.inputStream().use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
