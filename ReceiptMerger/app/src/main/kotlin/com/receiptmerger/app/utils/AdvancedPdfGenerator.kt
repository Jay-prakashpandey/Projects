package com.receiptmerger.app.utils

import android.content.Context
import android.net.Uri
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.utils.PdfMerger
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.properties.TextAlignment
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AdvancedPdfGenerator {
    
    fun mergePdfFiles(
        inputFiles: List<File>,
        outputPath: String,
    ): Boolean {
        return try {
            val output = File(outputPath)
            output.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(output))
            val pdfDocument = PdfDocument(pdfWriter)
            val merger = PdfMerger(pdfDocument)

            inputFiles.forEach { file ->
                if (file.exists() && file.extension == "pdf") {
                    val reader = PdfReader(FileInputStream(file))
                    val sourceDocument = PdfDocument(reader)
                    merger.merge(sourceDocument, 1, sourceDocument.numberOfPages)
                    sourceDocument.close()
                }
            }

            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun createReceiptPdfFromImages(
        context: Context,
        imageUris: List<Uri>,
        outputPath: String,
        templateType: String = "standard",
        title: String = "Receipt",
    ): Boolean {
        return try {
            val output = File(outputPath)
            output.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(output))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)

            // Add header
            val headerParagraph = Paragraph(title)
                .setFontSize(20f)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER)
            document.add(headerParagraph)

            val dateParagraph = Paragraph("Generated: ${getCurrentDate()}")
                .setFontSize(10f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(dateParagraph)

            document.add(Paragraph("\n"))

            // Add images
            imageUris.forEach { uri ->
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    inputStream?.use { stream ->
                        val imageData = ImageDataFactory.create(stream.readBytes())
                        val image = Image(imageData)
                        
                        // Scale to fit A4 width with margins
                        image.scaleToFit(550f, 700f)
                        document.add(image)
                        document.add(Paragraph("\n"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Add footer
            val footerParagraph = Paragraph("End of Receipt")
                .setFontSize(8f)
                .setTextAlignment(TextAlignment.CENTER)
            document.add(footerParagraph)

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun createStandardReceiptTemplate(
        outputPath: String,
        items: List<ReceiptItem>,
        totalAmount: Double = 0.0,
    ): Boolean {
        return try {
            val output = File(outputPath)
            output.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(output))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)

            // Header
            document.add(
                Paragraph("RECEIPT")
                    .setFontSize(18f)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER)
            )
            document.add(Paragraph("Date: ${getCurrentDate()}").setFontSize(10f))
            document.add(Paragraph("\n"))

            // Items table header
            document.add(Paragraph("Description              Qty    Price      Total"))
            document.add(Paragraph("─".repeat(50)))

            // Items
            items.forEach { item ->
                document.add(
                    Paragraph(
                        String.format(
                            "%-24s %3d  $%7.2f  $%7.2f",
                            item.description.take(24),
                            item.quantity,
                            item.price,
                            item.price * item.quantity
                        )
                    ).setFontSize(10f)
                )
            }

            document.add(Paragraph("─".repeat(50)))
            document.add(
                Paragraph(String.format("TOTAL:                                  $%7.2f", totalAmount))
                    .setBold()
                    .setFontSize(12f)
            )

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun addWatermark(inputPath: String, outputPath: String, watermarkText: String): Boolean {
        return try {
            val reader = PdfReader(FileInputStream(inputPath))
            val writer = PdfWriter(FileOutputStream(outputPath))
            val pdfDocument = PdfDocument(reader, writer)

            val numPages = pdfDocument.numberOfPages
            for (page in 1..numPages) {
                val pdfPage = pdfDocument.getPage(page)
                val canvas = com.itextpdf.kernel.pdf.canvas.PdfCanvas(pdfPage)
                
                // Add watermark (optional - can be implemented if needed)
            }

            pdfDocument.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    }
}

data class ReceiptItem(
    val description: String,
    val quantity: Int,
    val price: Double,
)
