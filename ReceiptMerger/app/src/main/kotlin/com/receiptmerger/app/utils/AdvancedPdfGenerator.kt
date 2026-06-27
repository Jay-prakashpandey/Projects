package com.receiptmerger.app.utils

import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.font.PdfEncodings
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
import com.itextpdf.layout.properties.VerticalAlignment
import com.itextpdf.layout.properties.HorizontalAlignment
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
        signature: String? = null,
        signatureImageUri: Uri? = null,
        quality: Int = 100
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
                    val imageData = getImageDataWithQuality(context, uri, quality)
                    val image = Image(imageData)
                    
                    // Scale to fit A4 width with margins
                    image.scaleToFit(550f, 650f)
                    image.setHorizontalAlignment(HorizontalAlignment.CENTER)
                    document.add(image)

                    signatureImageUri?.let { sigUri ->
                        val sigImageData = getImageDataWithQuality(context, sigUri, quality)
                        val sigImage = Image(sigImageData)
                        sigImage.scaleToFit(252f, 100f) // Business card width, max height for logo
                        sigImage.setHorizontalAlignment(HorizontalAlignment.CENTER)
                        sigImage.setMarginTop(10f)
                        document.add(sigImage)
                    }

                    // Add individual text signature per image
                    signature?.let { text ->
                        document.add(Paragraph(text)
                            .setFontSize(12f)
                            .setItalic()
                            .setTextAlignment(TextAlignment.CENTER)
                            .setMarginTop(5f))
                    }

                    document.add(Paragraph("\n"))
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
        signature: String? = null,
        signatureImageUri: Uri? = null,
        quality: Int = 100
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
                        signature = signature,
                        signatureImageUri = signatureImageUri,
                        quality = quality
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

    /**
     * Create a grid layout (Rows x Columns) PDF supporting images and PDF files
     */
    fun createGridPdf(
        context: Context,
        filePaths: List<String>,
        outputPath: String,
        rows: Int,
        cols: Int,
        signature: String? = null,
        signatureImageUri: Uri? = null,
        quality: Int = 100,
        tolerance: Int = 10
    ): Boolean {
        return try {
            val output = File(outputPath)
            output.parentFile?.mkdirs()

            val pdfWriter = PdfWriter(FileOutputStream(output))
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument, PageSize.A4)
            
            val pageSize = PageSize.A4
            val margin = 20f
            val availableWidth = pageSize.width - (margin * 2f)
            val availableHeight = pageSize.height - (margin * 2f)
            
            val cellWidth = availableWidth / cols
            val cellHeight = availableHeight / rows

            var placedCount = 0
            filePaths.forEachIndexed { index, filePath ->
                val sourceFile = File(filePath)
                if (!sourceFile.exists()) return@forEachIndexed

                val processedReceipt = ImageProcessor.convertDocumentToCroppedImage(context, sourceFile, tolerance)
                    ?: return@forEachIndexed

                try {
                    val uri = Uri.fromFile(processedReceipt)
                    val posInPage = placedCount % (rows * cols)
                    
                    if (posInPage == 0) pdfDocument.addNewPage()
                    
                    val pageNumber = pdfDocument.numberOfPages
                    val r = posInPage / cols
                    val c = posInPage % cols
                    
                    val textSigHeight = if (signature != null) 34f else 0f
                    val sigHeight = if (signatureImageUri != null) 100f else 0f
                    val gap = if (signatureImageUri != null || signature != null) 10f else 0f
                    val adjustedCellHeight = cellHeight - sigHeight - textSigHeight - gap

                    val cellX = margin + (c * cellWidth)
                    val cellY = pageSize.height - margin - ((r + 1) * cellHeight)

                    val imageData = getImageDataWithQuality(context, uri, quality)
                    val image = Image(imageData)
                    
                    val scale = min(cellWidth / imageData.width, adjustedCellHeight / imageData.height)
                    image.scaleAbsolute(imageData.width * scale, imageData.height * scale)
                    
                    val x = cellX + (cellWidth - image.imageScaledWidth) / 2f
                    val y = cellY + sigHeight + textSigHeight + gap + (adjustedCellHeight - image.imageScaledHeight) / 2f
                    
                    image.setFixedPosition(pageNumber, x, y)
                    document.add(image)

                    signatureImageUri?.let { sigUri ->
                        val sigImageData = getImageDataWithQuality(context, sigUri, quality)
                        val sigImage = Image(sigImageData)
                        val sigScale = min(252f / sigImageData.width, 100f / sigImageData.height)
                        sigImage.scaleAbsolute(sigImageData.width * sigScale, sigImageData.height * sigScale)
                        val sx = cellX + (cellWidth - sigImage.imageScaledWidth) / 2f
                        val sy = cellY + textSigHeight
                        sigImage.setFixedPosition(pageNumber, sx, sy)
                        document.add(sigImage)
                    }

                    signature?.let { text ->
                        val p = Paragraph(text).setFontSize(10f).setItalic().setTextAlignment(TextAlignment.CENTER)
                        document.showTextAligned(p, cellX + cellWidth / 2f, cellY, pageNumber, 
                            TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0f)
                    }
                    placedCount++
                } finally {
                    processedReceipt.delete()
                }
            }

            document.close()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun getImageDataWithQuality(context: Context, uri: Uri, quality: Int): com.itextpdf.io.image.ImageData {
        val inputStream = context.contentResolver.openInputStream(uri)
        val originalBytes = inputStream?.use { it.readBytes() } ?: throw Exception("Cannot read image")
        if (quality >= 100) return ImageDataFactory.create(originalBytes)

        val bitmap = BitmapFactory.decodeByteArray(originalBytes, 0, originalBytes.size)
        val out = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, out)
        return ImageDataFactory.create(out.toByteArray())
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
        signature: String? = null,
        signatureImageUri: Uri? = null,
        quality: Int = 100
    ) {
        if (!sourceFile.exists()) return

        val textSigHeight = if (signature != null) 34f else 0f
        val sigHeight = if (signatureImageUri != null) 100f else 0f
        val gap = if (signatureImageUri != null || signature != null) 10f else 0f
        val adjustedSlotHeight = slotHeight - sigHeight - textSigHeight - gap

        val processedReceipt = ImageProcessor.convertDocumentToCroppedImage(context, sourceFile, tolerance) ?: return

        try {
            val uri = Uri.fromFile(processedReceipt)
            val imageData = getImageDataWithQuality(context, uri, quality)
            val image = Image(imageData)
            val scale = min(slotWidth / imageData.width, adjustedSlotHeight / imageData.height)
            val scaledWidth = imageData.width * scale
            val scaledHeight = imageData.height * scale
            val x = slotX + ((slotWidth - scaledWidth) / 2f)
            val y = slotY + sigHeight + textSigHeight + gap + ((adjustedSlotHeight - scaledHeight) / 2f)

            image.scaleAbsolute(scaledWidth, scaledHeight)
            image.setFixedPosition(pageNumber, x, y)
            document.add(image)

            signature?.let { text ->
                // Note: To support emojis, you must load a Unicode font:
                // val font = PdfFontFactory.createFont("path/to/noto-sans.ttf", PdfEncodings.IDENTITY_H)
                val p = Paragraph(text)
                    .setFontSize(11f)
                    .setItalic()
                    .setTextAlignment(TextAlignment.CENTER)
                
                document.showTextAligned(p, slotX + slotWidth / 2f, slotY + 2f, pageNumber, 
                    TextAlignment.CENTER, VerticalAlignment.BOTTOM, 0f)
            }

            signatureImageUri?.let { sigUri ->
                val sigImageData = getImageDataWithQuality(context, sigUri, quality)
                val sigImage = Image(sigImageData)
                
                // Limit logo to business card width (252pt)
                val maxWidth = min(slotWidth, 252f)
                val sigScale = min(maxWidth / sigImageData.width, 100f / sigImageData.height)
                
                sigImage.scaleAbsolute(sigImageData.width * sigScale, sigImageData.height * sigScale)
                val sigX = slotX + ((slotWidth - sigImage.imageScaledWidth) / 2f)
                val sigY = slotY + textSigHeight
                sigImage.setFixedPosition(pageNumber, sigX, sigY)
                document.add(sigImage)
            }
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
