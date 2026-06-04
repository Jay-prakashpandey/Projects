package com.receiptmerger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.navigation.NavController
import com.receiptmerger.app.ui.components.ErrorDialog
import com.receiptmerger.app.ui.components.ProgressBar
import com.receiptmerger.app.ui.navigation.Screen
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfPreviewScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    val context = LocalContext.current

    val isProcessing by viewModel.isProcessing.collectAsState()
    val progress by viewModel.processingProgress.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val generatedPdfPath by viewModel.generatedPdfPath.collectAsState()
    val currentTemplate by viewModel.currentTemplate.collectAsState()
    val receiptsPerPage = if (currentTemplate == "collage2") 2 else 3

    val projectName = remember {
        mutableStateOf(
            "Receipt_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())}"
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Generate PDF") },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        enabled = !isProcessing
                    ) {
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
                "Enter project name",
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = projectName.value,
                onValueChange = { projectName.value = it },
                label = { Text("Project Name") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isProcessing
            )

            if (isProcessing) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("Generating PDF... ${(progress * 100).toInt()}%")
                        ProgressBar(
                            progress = progress,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else if (generatedPdfPath != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "✓ PDF Generated Successfully!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            generatedPdfPath.orEmpty(),
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ready to generate PDF")
                }
            }

            Button(
                onClick = {
                    val outputDir = context.cacheDir
                    viewModel.generateCollagePdf(context, outputDir, projectName.value)
                },
                enabled = !isProcessing && generatedPdfPath == null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate ${receiptsPerPage} per A4 PDF")
            }

            if (generatedPdfPath != null) {
                Button(
                    onClick = { navController.navigate(Screen.SaveShare.route) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Continue to Save")
                }
            }
        }

        if (errorMessage != null) {
            ErrorDialog(
                title = "Error",
                message = errorMessage.orEmpty(),
                onDismiss = { viewModel.clearError() },
                onRetry = {
                    viewModel.clearError()
                    val outputDir = context.cacheDir
                    viewModel.generateCollagePdf(context, outputDir, projectName.value)
                }
            )
        }
    }
}
