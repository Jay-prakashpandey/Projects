package com.receiptmerger.app.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.receiptmerger.app.data.MergeProjectEntity
import com.receiptmerger.app.data.ReceiptEntity
import com.receiptmerger.app.data.db.ReceiptMergerDatabase
import com.receiptmerger.app.utils.AdvancedPdfGenerator
import com.receiptmerger.app.utils.FileUtils
import com.receiptmerger.app.utils.PermissionUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

data class FileItem(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
)

class ReceiptMergerViewModel(private val database: ReceiptMergerDatabase? = null) : ViewModel() {
    private val _currentTemplate = MutableStateFlow<String>("collage3")
    val currentTemplate = _currentTemplate.asStateFlow()

    private val _selectedFiles = MutableStateFlow<List<FileItem>>(emptyList())
    val selectedFiles = _selectedFiles.asStateFlow()

    // Settings and Config
    private val _pdfQuality = MutableStateFlow(100)
    val pdfQuality = _pdfQuality.asStateFlow()

    private val _userSignature = MutableStateFlow<String?>(null)
    val userSignature = _userSignature.asStateFlow()

    private val _userSignatureImageUri = MutableStateFlow<Uri?>(null)
    val userSignatureImageUri = _userSignatureImageUri.asStateFlow()

    private val _gridRows = MutableStateFlow(2)
    val gridRows = _gridRows.asStateFlow()

    private val _gridCols = MutableStateFlow(2)
    val gridCols = _gridCols.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress = _processingProgress.asStateFlow()

    private val _selectedProjectIds = MutableStateFlow<Set<String>>(emptySet())
    val selectedProjectIds = _selectedProjectIds.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _generatedPdfPath = MutableStateFlow<String?>(null)
    val generatedPdfPath = _generatedPdfPath.asStateFlow()

    private val _allProjects = MutableStateFlow<List<MergeProjectEntity>>(emptyList())
    val allProjects = _allProjects.asStateFlow()

    fun setTemplate(template: String) {
        _currentTemplate.value = template
    }

    fun setQuality(quality: Int) {
        _pdfQuality.value = quality
    }

    fun setSignature(signature: String?) {
        _userSignature.value = if (signature.isNullOrBlank()) null else signature
    }

    fun setSignatureImage(uri: Uri?) {
        _userSignatureImageUri.value = uri
    }

    fun setGridDimensions(rows: Int, cols: Int) {
        _gridRows.value = rows
        _gridCols.value = cols
    }

    fun toggleProjectSelection(projectId: String) {
        val current = _selectedProjectIds.value.toMutableSet()
        if (current.contains(projectId)) current.remove(projectId) else current.add(projectId)
        _selectedProjectIds.value = current
    }

    fun selectAllProjects() {
        _selectedProjectIds.value = _allProjects.value.map { it.id }.toSet()
    }

    fun clearProjectSelection() {
        _selectedProjectIds.value = emptySet()
    }

    fun deleteSelectedProjects() {
        viewModelScope.launch(Dispatchers.IO) {
            val toDelete = _allProjects.value.filter { _selectedProjectIds.value.contains(it.id) }
            toDelete.forEach { project ->
                project.outputPath.takeIf { it.isNotEmpty() }?.let { File(it).delete() }
                database?.projectDao()?.deleteProject(project)
            }
            _selectedProjectIds.value = emptySet()
            loadProjects()
        }
    }

    fun deleteProject(project: MergeProjectEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            project.outputPath.takeIf { it.isNotEmpty() }?.let { File(it).delete() }
            database?.projectDao()?.deleteProject(project)
            loadProjects()
        }
    }

    fun receiptsPerPage(): Int {
        return if (_currentTemplate.value == "collage2") 2 else 3
    }

    private fun isPdfFile(fileItem: FileItem): Boolean {
        return fileItem.mimeType.equals("application/pdf", true) || fileItem.name.endsWith(".pdf", true)
    }

    private fun isImageFile(fileItem: FileItem): Boolean {
        val lowerMime = fileItem.mimeType.lowercase()
        return lowerMime.startsWith("image/") ||
            fileItem.name.endsWith(".jpg", true) ||
            fileItem.name.endsWith(".jpeg", true) ||
            fileItem.name.endsWith(".png", true) ||
            fileItem.name.endsWith(".bmp", true) ||
            fileItem.name.endsWith(".webp", true)
    }

    fun addFile(context: Context, uri: Uri) {
        viewModelScope.launch {
            try {
                val name = FileUtils.getDisplayName(context, uri)
                val size = FileUtils.getFileSize(context, uri)
                val mimeType = FileUtils.getMimeType(context, uri)
                val fileItem = FileItem(uri, name, size, mimeType)
                _selectedFiles.value = _selectedFiles.value + fileItem
                _errorMessage.value = null
            } catch (e: Exception) {
                _errorMessage.value = "Failed to add file: ${e.message}"
            }
        }
    }

    fun removeFile(index: Int) {
        _selectedFiles.value = _selectedFiles.value.filterIndexed { i, _ -> i != index }
    }

    fun generatePdf(context: Context, outputDir: File, projectName: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _errorMessage.value = null
            _processingProgress.value = 0f
            val cachedFiles = mutableListOf<File>()

            try {
                val files = _selectedFiles.value
                if (files.isEmpty()) {
                    _errorMessage.value = "No files selected"
                    _isProcessing.value = false
                    return@launch
                }

                val projectId = System.currentTimeMillis().toString()
                outputDir.mkdirs()
                val outputPath = File(outputDir, "$projectName.pdf").absolutePath

                // Copy files to cache first
                files.forEachIndexed { index, file ->
                    val cached = FileUtils.copyToCache(context, file.uri, "${index}_${file.name}")
                    if (cached != null) {
                        cachedFiles.add(cached)
                    }
                    _processingProgress.value = (index.toFloat() / files.size) * 0.5f
                }

                if (cachedFiles.isEmpty()) {
                    _errorMessage.value = "Failed to process files"
                    _isProcessing.value = false
                    return@launch
                }

                // Validate files for the selected template
                val allPdfFiles = files.all { isPdfFile(it) }
                val hasPdfFiles = files.any { isPdfFile(it) }
                val hasImageFiles = files.any { isImageFile(it) }

                val success = when (_currentTemplate.value) {
                    "pdf_merge" -> {
                        if (!hasPdfFiles) {
                            _errorMessage.value = "PDF Merge requires PDF files."
                            false
                        } else if (!allPdfFiles) {
                            _errorMessage.value = "PDF Merge only supports PDF files. Remove non-PDF files."
                            false
                        } else {
                            AdvancedPdfGenerator.mergePdfFiles(cachedFiles, outputPath)
                        }
                    }
                    "image_merge" -> {
                        when {
                            allPdfFiles -> AdvancedPdfGenerator.mergePdfFiles(cachedFiles, outputPath)
                            hasPdfFiles -> {
                                _errorMessage.value = "Image to PDF cannot mix images and PDFs. Use PDF Merge for PDFs."
                                false
                            }
                            !hasImageFiles -> {
                                _errorMessage.value = "Image to PDF requires image files."
                                false
                            }
                            else -> AdvancedPdfGenerator.createReceiptPdfFromImages(
                                context,
                                files.map { it.uri },
                                outputPath,
                                _currentTemplate.value,
                                projectName,
                                _userSignature.value,
                                _userSignatureImageUri.value,
                                _pdfQuality.value
                            )
                        }
                    }
                    else -> {
                        if (hasPdfFiles) {
                            _errorMessage.value = "Selected PDF files cannot be converted using the current template. Use PDF Merge instead."
                            false
                        } else {
                            if (_currentTemplate.value == "grid_rc") {
                                AdvancedPdfGenerator.createGridPdfFromImages(
                                    context,
                                    files.map { it.uri },
                                    outputPath,
                                    _gridRows.value,
                                    _gridCols.value,
                                    _userSignature.value,
                                    _userSignatureImageUri.value,
                                    _pdfQuality.value
                                )
                            } else {
                                AdvancedPdfGenerator.createReceiptPdfFromImages(
                                    context,
                                    files.map { it.uri },
                                    outputPath,
                                    _currentTemplate.value,
                                    projectName,
                                    _userSignature.value,
                                    _userSignatureImageUri.value,
                                    _pdfQuality.value
                                )
                            }
                        }
                    }
                }

                if (success) {
                    _generatedPdfPath.value = outputPath
                    _processingProgress.value = 1f

                    // Save to database
                    database?.let {
                        val project = MergeProjectEntity(
                            id = projectId,
                            name = projectName,
                            template = _currentTemplate.value,
                            outputPath = outputPath,
                            status = "completed",
                        )
                        it.projectDao().insertProject(project)

                        files.forEachIndexed { index, file ->
                            val receipt = ReceiptEntity(
                                projectId = projectId,
                                filePath = cachedFiles.getOrNull(index)?.absolutePath ?: "",
                                fileName = file.name,
                                fileSize = file.size,
                                mimeType = file.mimeType,
                            )
                            it.receiptDao().insertReceipt(receipt)
                        }
                    }
                } else if (_errorMessage.value.isNullOrBlank()) {
                    _errorMessage.value = "Failed to generate PDF"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                e.printStackTrace()
            } finally {
                // Clean up cache always, even on error
                cachedFiles.forEach { FileUtils.deleteFile(it) }
                _isProcessing.value = false
            }
        }
    }

    fun generateCollagePdf(context: Context, outputDir: File, projectName: String) {
        viewModelScope.launch {
            _isProcessing.value = true
            _errorMessage.value = null
            _processingProgress.value = 0f

            try {
                val files = _selectedFiles.value
                val receiptsPerPage = receiptsPerPage()
                if (files.size < 1) {
                    _errorMessage.value = "Please select at least 1 receipt"
                    _isProcessing.value = false
                    return@launch
                }

                val invalidFiles = files.filterNot { isPdfFile(it) || isImageFile(it) }
                if (invalidFiles.isNotEmpty()) {
                    _errorMessage.value = "Collage mode only supports PDF or image receipts. Remove unsupported files."
                    _isProcessing.value = false
                    return@launch
                }

                val projectId = System.currentTimeMillis().toString()
                val generationResult = withContext(Dispatchers.IO) {
                    outputDir.mkdirs()

                    val cachedFiles = mutableListOf<File>()
                    files.forEachIndexed { index, file ->
                        val cached = FileUtils.copyToCache(context, file.uri, "${index}_${file.name}")
                        if (cached != null) {
                            cachedFiles.add(cached)
                        }
                        _processingProgress.value = (index.toFloat() / files.size) * 0.3f
                    }

                    if (cachedFiles.isEmpty()) {
                        return@withContext CollageGenerationResult(
                            success = false,
                            outputPath = null,
                            cachedFiles = cachedFiles,
                        )
                    }

                    val outputPath = File(outputDir, "${projectName}_${receiptsPerPage}_per_A4.pdf").absolutePath
                    val success = AdvancedPdfGenerator.createA4CollagePdf(
                        context = context,
                        receiptPaths = cachedFiles.map { it.absolutePath },
                        outputPath = outputPath,
                        tolerance = 10,
                        receiptsPerPage = receiptsPerPage,
                        signature = _userSignature.value,
                        signatureImageUri = _userSignatureImageUri.value,
                        quality = _pdfQuality.value
                    )

                    CollageGenerationResult(
                        success = success,
                        outputPath = outputPath,
                        cachedFiles = cachedFiles,
                    )
                }

                if (generationResult.cachedFiles.isEmpty()) {
                    _errorMessage.value = "Failed to process files"
                    _isProcessing.value = false
                    return@launch
                }

                if (generationResult.success && generationResult.outputPath != null) {
                    _generatedPdfPath.value = generationResult.outputPath
                    _processingProgress.value = 1f

                    // Save to database
                    database?.let {
                        val project = MergeProjectEntity(
                            id = projectId,
                            name = projectName,
                            template = _currentTemplate.value,
                            outputPath = generationResult.outputPath,
                            status = "completed",
                        )
                        it.projectDao().insertProject(project)

                        files.forEachIndexed { index, file ->
                            val receipt = ReceiptEntity(
                                projectId = projectId,
                                filePath = generationResult.cachedFiles.getOrNull(index)?.absolutePath ?: "",
                                fileName = file.name,
                                fileSize = file.size,
                                mimeType = file.mimeType,
                            )
                            it.receiptDao().insertReceipt(receipt)
                        }
                    }
                } else {
                    _errorMessage.value = "Failed to generate collage PDF"
                }

                // Clean up cache
                withContext(Dispatchers.IO) {
                    generationResult.cachedFiles.forEach { FileUtils.deleteFile(it) }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                e.printStackTrace()
            } finally {
                _isProcessing.value = false
            }
        }
    }

    private data class CollageGenerationResult(
        val success: Boolean,
        val outputPath: String?,
        val cachedFiles: List<File>,
    )

    fun clearError() {
        _errorMessage.value = null
    }

    fun reset() {
        _selectedFiles.value = emptyList()
        _currentTemplate.value = "collage3"
        _generatedPdfPath.value = null
        _errorMessage.value = null
    }

    fun loadProjects() {
        viewModelScope.launch {
            database?.projectDao()?.getAllProjects()?.collect { projects ->
                _allProjects.value = projects
            }
        }
    }
}
