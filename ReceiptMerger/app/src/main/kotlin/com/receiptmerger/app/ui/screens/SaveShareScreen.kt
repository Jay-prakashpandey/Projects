package com.receiptmerger.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.receiptmerger.app.ui.navigation.Screen
import com.receiptmerger.app.utils.PermissionUtils
import com.receiptmerger.app.utils.ShareUtils
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveShareScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    val context = LocalContext.current

    val generatedPdfPath by viewModel.generatedPdfPath.collectAsState()
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
                "PDF Ready",
                style = MaterialTheme.typography.headlineSmall
            )

            Text(
                generatedPdfPath?.let { File(it).name }.orEmpty(),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ElevatedButton(
                    onClick = {
                        generatedPdfPath?.let { path ->
                            ShareUtils.openPdf(context, path)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open PDF")
                }

                ElevatedButton(
                    onClick = {
                        generatedPdfPath?.let { path ->
                            try {
                                ShareUtils.sharePdf(context, path)
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
                "Saved in: ${PermissionUtils.getDownloadsDirectory(context).absolutePath}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (showError.value != null) {
            ErrorDialog(
                title = "Error",
                message = showError.value.orEmpty(),
                onDismiss = {
                    showError.value = null
                }
            )
        }
    }
}
