package com.receiptmerger.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.receiptmerger.app.data.db.ReceiptMergerDatabase
import com.receiptmerger.app.ui.components.ConfirmDialog
import com.receiptmerger.app.ui.components.ErrorDialog
import com.receiptmerger.app.ui.navigation.Screen
import com.receiptmerger.app.utils.FileUtils
import com.receiptmerger.app.utils.PermissionUtils
import com.receiptmerger.app.utils.ShareUtils
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel
import androidx.core.content.FileProvider
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveShareScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    val context = LocalContext.current

    val generatedPdfPath by viewModel.generatedPdfPath.collectAsState()
    val fileName = remember { mutableStateOf("merged_receipt.pdf") }
    val showSaveConfirm = remember { mutableStateOf(false) }
    val showError = remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Save & Share") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Save Your Merged PDF",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = fileName.value,
                onValueChange = { fileName.value = it.replace(".pdf", "") },
                label = { Text("File Name") },
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text(".pdf") }
            )

            Text(
                "Choose how to proceed:",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = {
                        showSaveConfirm.value = true
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save to Downloads")
                }

                ElevatedButton(
                    onClick = {
                        generatedPdfPath?.let { path ->
                            try {
                                val file = File(path)
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
                                context.startActivity(
                                    Intent.createChooser(shareIntent, "Share PDF")
                                )
                            } catch (e: Exception) {
                                showError.value = "Failed to share: ${e.message}"
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Share, contentDescription = null)
                    Text("Share PDF", modifier = Modifier.padding(start = 8.dp))
                }

                Button(
                    onClick = {
                        viewModel.reset()
                        navController.popBackStack(Screen.Home.route, false)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Another")
                }
            }

            Text(
                "File will be saved to: ${PermissionUtils.getDownloadsDirectory(context).absolutePath}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showSaveConfirm.value) {
            ConfirmDialog(
                title = "Save PDF",
                message = "Save '${fileName.value}.pdf' to Downloads folder?",
                onConfirm = {
                    generatedPdfPath?.let { path ->
                        try {
                            val downloadDir = PermissionUtils.getDownloadsDirectory(context)
                            downloadDir.mkdirs()
                            val sourceFile = File(path)
                            val destFile = File(downloadDir, "${fileName.value}.pdf")
                            
                            sourceFile.inputStream().use { input ->
                                destFile.outputStream().use { output ->
                                    input.copyTo(output)
                                }
                            }
                            
                            showError.value = "PDF saved successfully to:\n${destFile.absolutePath}"
                        } catch (e: Exception) {
                            showError.value = "Failed to save: ${e.message}"
                        }
                    }
                    showSaveConfirm.value = false
                },
                onCancel = {
                    showSaveConfirm.value = false
                }
            )
        }

        if (showError.value != null) {
            ErrorDialog(
                title = if (showError.value?.contains("successfully") == true) "Success" else "Error",
                message = showError.value.orEmpty(),
                onDismiss = {
                    showError.value = null
                    if (showError.value?.contains("successfully") == true) {
                        viewModel.reset()
                        navController.popBackStack(Screen.Home.route, false)
                    }
                }
            )
        }
    }
}
