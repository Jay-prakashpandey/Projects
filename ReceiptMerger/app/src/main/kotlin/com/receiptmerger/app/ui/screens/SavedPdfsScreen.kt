package com.receiptmerger.app.ui.screens

import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import com.receiptmerger.app.data.MergeProjectEntity
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.receiptmerger.app.utils.ShareUtils
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SavedPdfsScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    val projects by viewModel.allProjects.collectAsState()
    val selectedIds by viewModel.selectedProjectIds.collectAsState()
    val context = LocalContext.current

    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var projectToDelete by remember { mutableStateOf<MergeProjectEntity?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadProjects()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (selectedIds.isEmpty()) "Saved PDFs" else "${selectedIds.size} Selected") 
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        if (selectedIds.isNotEmpty()) viewModel.clearProjectSelection() 
                        else navController.popBackStack() 
                    }) {
                        Icon(if (selectedIds.isNotEmpty()) Icons.Default.Close else Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.selectAllProjects() }) {
                        Icon(Icons.Default.SelectAll, contentDescription = "Select All")
                    }
                    if (selectedIds.isNotEmpty()) {
                        IconButton(onClick = {
                            projectToDelete = null
                            showDeleteConfirmation = true
                        }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                        IconButton(onClick = {
                            val selectedProjects = projects.filter { selectedIds.contains(it.id) }
                            val uris = ArrayList(selectedProjects.mapNotNull { project ->
                                project.outputPath.takeIf { it.isNotEmpty() }?.let { path ->
                                    FileProvider.getUriForFile(context, "${context.packageName}.provider", File(path))
                                }
                            })

                            val intent = if (uris.size > 1) {
                                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
                                }
                            } else {
                                Intent(Intent.ACTION_SEND).apply {
                                    putExtra(Intent.EXTRA_STREAM, uris.firstOrNull())
                                }
                            }.apply {
                                type = "application/pdf"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(intent, "Share PDF(s)"))
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (projects.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No PDFs created yet", style = MaterialTheme.typography.bodyLarge)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(projects) { project ->
                    val isSelected = selectedIds.contains(project.id)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { 
                                    if (selectedIds.isNotEmpty()) viewModel.toggleProjectSelection(project.id)
                                    else { /* Optional: Navigate to preview */ }
                                },
                                onLongClick = { viewModel.toggleProjectSelection(project.id) }
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                           else MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(project.name, style = MaterialTheme.typography.titleMedium)
                                Text("Template: ${project.template}", style = MaterialTheme.typography.bodySmall)
                            }
                            if (isSelected) {
                                Icon(Icons.Default.CheckCircle, contentDescription = "Selected", tint = MaterialTheme.colorScheme.primary)
                            }

                            // Restore individual open and share buttons
                            if (selectedIds.isEmpty() && project.outputPath.isNotEmpty()) {
                                IconButton(onClick = { ShareUtils.openPdf(context, project.outputPath) }) {
                                    Icon(Icons.Default.Visibility, contentDescription = "Open")
                                }
                                IconButton(onClick = { ShareUtils.sharePdf(context, project.outputPath) }) {
                                    Icon(Icons.Default.Share, contentDescription = "Share")
                                }
                                IconButton(onClick = {
                                    projectToDelete = project
                                    showDeleteConfirmation = true
                                }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (showDeleteConfirmation) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirmation = false },
                title = { Text("Confirm Deletion") },
                text = {
                    val message = if (projectToDelete != null) {
                        "Are you sure you want to delete '${projectToDelete?.name}'? This action cannot be undone."
                    } else {
                        "Are you sure you want to delete ${selectedIds.size} selected PDF(s)? This action cannot be undone."
                    }
                    Text(message)
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (projectToDelete != null) {
                                viewModel.deleteProject(projectToDelete!!)
                            } else {
                                viewModel.deleteSelectedProjects()
                            }
                            showDeleteConfirmation = false
                        }
                    ) {
                        Text("Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirmation = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}