package com.receiptmerger.app.utils

import android.content.Context
import android.net.Uri
import com.itextpdf.io.image.ImageData
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import java.io.File
import java.io.FileOutputStream

object PdfGenerator {
    fun mergePdfs(
        context: Context,
        receipts: List<String>,
        outputPath: String,
    ): Boolean {
        return try {
            val output = File(outputPath)
            output.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(output))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            receipts.forEach { receiptPath ->
                val file = File(receiptPath)
                if (file.exists()) {
                    // Add receipt content to PDF
                    document.add(Paragraph(file.name))
                }
            }

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun addImageToPdf(
        context: Context,
        imageUri: Uri,
        pdfPath: String,
    ): Boolean {
        return try {
            val pdf = File(pdfPath)
            pdf.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(pdf))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)

            val inputStream = context.contentResolver.openInputStream(imageUri)
            inputStream?.let { stream ->
                val imageData = ImageDataFactory.create(stream.readBytes())
                val image = Image(imageData)
                image.scaleToFit(595f, 842f) // A4 size
                document.add(image)
            }

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}
