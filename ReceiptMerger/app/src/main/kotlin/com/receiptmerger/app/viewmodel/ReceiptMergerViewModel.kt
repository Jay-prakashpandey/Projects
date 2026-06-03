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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

data class FileItem(
    val uri: Uri,
    val name: String,
    val size: Long,
    val mimeType: String,
)

class ReceiptMergerViewModel(private val database: ReceiptMergerDatabase? = null) : ViewModel() {
    private val _currentTemplate = MutableStateFlow<String>("standard")
    val currentTemplate = _currentTemplate.asStateFlow()

    private val _selectedFiles = MutableStateFlow<List<FileItem>>(emptyList())
    val selectedFiles = _selectedFiles.asStateFlow()

    private val _isProcessing = MutableStateFlow(false)
    val isProcessing = _isProcessing.asStateFlow()

    private val _processingProgress = MutableStateFlow(0f)
    val processingProgress = _processingProgress.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage = _errorMessage.asStateFlow()

    private val _generatedPdfPath = MutableStateFlow<String?>(null)
    val generatedPdfPath = _generatedPdfPath.asStateFlow()

    private val _allProjects = MutableStateFlow<List<MergeProjectEntity>>(emptyList())
    val allProjects = _allProjects.asStateFlow()

    fun setTemplate(template: String) {
        _currentTemplate.value = template
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
                val cachedFiles = mutableListOf<File>()
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

                // Generate PDF
                val success = when (_currentTemplate.value) {
                    "pdf_merge" -> AdvancedPdfGenerator.mergePdfFiles(cachedFiles, outputPath)
                    else -> AdvancedPdfGenerator.createReceiptPdfFromImages(
                        context,
                        files.map { it.uri },
                        outputPath,
                        _currentTemplate.value,
                        projectName
                    )
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
                } else {
                    _errorMessage.value = "Failed to generate PDF"
                }

                // Clean up cache
                cachedFiles.forEach { FileUtils.deleteFile(it) }
            } catch (e: Exception) {
                _errorMessage.value = "Error: ${e.message}"
                e.printStackTrace()
            } finally {
                _isProcessing.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun reset() {
        _selectedFiles.value = emptyList()
        _currentTemplate.value = "standard"
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
