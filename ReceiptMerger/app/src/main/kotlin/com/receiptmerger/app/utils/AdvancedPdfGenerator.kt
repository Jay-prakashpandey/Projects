package com.receiptmerger.app.utils

import android.content.Context
import android.net.Uri
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfReader
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.canvas.PdfCanvas
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState
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
import kotlin.math.min

object AdvancedPdfGenerator {
    
    fun mergePdfFiles(
        inputFiles: List<File>,
        outputPath: String,
    ): Boolean {
        if (inputFiles.isEmpty()) {
            return false
        }

        return try {
            val output = File(outputPath)
            output.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(output))
            val pdfDocument = PdfDocument(pdfWriter)
            val merger = PdfMerger(pdfDocument)

            inputFiles.forEach { file ->
                if (file.exists() && file.extension.equals("pdf", true)) {
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

            // Template-aware title
            val effectiveTitle = when (templateType) {
                "invoice" -> "Invoice"
                "receipt_tax" -> "$title (Tax Receipt)"
                else -> title
            }

            // Add header
            val headerParagraph = Paragraph(effectiveTitle)
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

    /**
     * Create a collage of three cropped receipts stacked vertically on each A4 page.
     */
    fun createA4CollagePdf(
        context: Context,
        receiptPaths: List<String>,
        outputPath: String,
        tolerance: Int = 10,
        receiptsPerPage: Int = 3,
    ): Boolean {
        if (receiptPaths.isEmpty()) return false

        return try {
            val outputFile = File(outputPath)
            outputFile.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(outputFile))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)
            document.setMargins(0f, 0f, 0f, 0f)

            val pageSize = PageSize.A4
            val pagePadding = 5f
            val receiptGap = 6f
            val receiptsOnPage = receiptsPerPage.coerceIn(2, 3)
            val slotWidth = pageSize.width - (pagePadding * 2f)
            val slotHeight = (
                pageSize.height - (pagePadding * 2f) - (receiptGap * (receiptsOnPage - 1))
            ) / receiptsOnPage

            for (i in receiptPaths.indices step receiptsOnPage) {
                pdfDocument.addNewPage(pageSize)
                val pageNumber = pdfDocument.numberOfPages

                receiptPaths.drop(i).take(receiptsOnPage).forEachIndexed { slotIndex, receiptPath ->
                    val slotFromBottom = receiptsOnPage - slotIndex - 1
                    placeReceiptInSlot(
                        context = context,
                        document = document,
                        pageNumber = pageNumber,
                        sourceFile = File(receiptPath),
                        slotX = pagePadding,
                        slotY = pagePadding + (slotFromBottom * (slotHeight + receiptGap)),
                        slotWidth = slotWidth,
                        slotHeight = slotHeight,
                        tolerance = tolerance,
                    )
                }
            }

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun placeReceiptInSlot(
        context: Context,
        document: Document,
        pageNumber: Int,
        sourceFile: File,
        slotX: Float,
        slotY: Float,
        slotWidth: Float,
        slotHeight: Float,
        tolerance: Int,
    ) {
        if (!sourceFile.exists()) return

        val processedReceipt = ImageProcessor.convertDocumentToCroppedImage(context, sourceFile, tolerance) ?: return

        try {
            val imageData = ImageDataFactory.create(processedReceipt.absolutePath)
            val image = Image(imageData)
            val scale = min(slotWidth / imageData.width, slotHeight / imageData.height)
            val scaledWidth = imageData.width * scale
            val scaledHeight = imageData.height * scale
            val x = slotX + ((slotWidth - scaledWidth) / 2f)
            val y = slotY + ((slotHeight - scaledHeight) / 2f)

            image.scaleAbsolute(scaledWidth, scaledHeight)
            image.setFixedPosition(pageNumber, x, y)
            document.add(image)
        } finally {
            processedReceipt.delete()
        }
    }

    /**
     * Create a batch collage - merges receipts in groups of three (3 per PDF)
     * All outputs go to the receipts folder
     * 
     * @param context Android context
     * @param receiptPaths List of receipt image file paths
     * @param receiptsFolder Output folder path
     * @return Number of PDFs created successfully
     */
    fun createBatchCollages(
        context: Context,
        receiptPaths: List<String>,
        receiptsFolder: String,
    ): Int {
        var successCount = 0
        var batchIndex = 1

        for (i in receiptPaths.indices step 3) {
            val batch = receiptPaths.drop(i).take(3)
            if (batch.size == 3) {
                val outputPath = File(receiptsFolder, "collage_batch_$batchIndex.pdf").absolutePath
                val success = createA4CollagePdf(
                    context = context,
                    receiptPaths = batch,
                    outputPath = outputPath,
                    tolerance = 10,
                    receiptsPerPage = 3
                )
                if (success) successCount++
                batchIndex++
            } else {
                Logger.i("Skipping final ${batch.size} receipt(s); collage batch needs 3 receipts")
            }
        }

        Logger.i("Batch collage creation complete: $successCount PDFs created")
        return successCount
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
                val canvas = PdfCanvas(pdfPage)
                val pageSize = pdfPage.pageSize
                val graphicsState = PdfExtGState().setFillOpacity(0.12f)

                canvas.saveState()
                canvas.setExtGState(graphicsState)
                canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD), 72f)
                    .moveText(pageSize.width / 2.0 - 180.0, pageSize.height / 2.0)
                    .showText(watermarkText)
                    .endText()
                canvas.restoreState()
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
