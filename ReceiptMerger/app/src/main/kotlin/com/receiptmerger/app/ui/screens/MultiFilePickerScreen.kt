package com.receiptmerger.app.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.receiptmerger.app.data.db.ReceiptMergerDatabase
import com.receiptmerger.app.ui.components.EmptyStateMessage
import com.receiptmerger.app.ui.components.ErrorDialog
import com.receiptmerger.app.ui.components.FileListItem
import com.receiptmerger.app.ui.navigation.Screen
import com.receiptmerger.app.utils.PermissionUtils
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MultiFilePickerScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    val context = LocalContext.current

    val selectedFiles by viewModel.selectedFiles.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val permissions = rememberMultiplePermissionsState(
        permissions = PermissionUtils.getRequiredPermissions().toList()
    )

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            viewModel.addFile(context, uri)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Files") },
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
                "Select receipts or images to merge",
                style = MaterialTheme.typography.bodyMedium
            )

            ElevatedButton(
                onClick = {
                    if (permissions.allPermissionsGranted) {
                        filePicker.launch("*/*")
                    } else {
                        permissions.launchMultiplePermissionRequest()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pick Files (${selectedFiles.size})")
            }

            if (selectedFiles.isEmpty()) {
                EmptyStateMessage(
                    title = "No Files Selected",
                    subtitle = "Tap 'Pick Files' to start adding receipts or images",
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(selectedFiles.size) { index ->
                        FileListItem(
                            fileName = selectedFiles[index].name,
                            fileSize = selectedFiles[index].size,
                            onDelete = { viewModel.removeFile(index) }
                        )
                    }
                }
            }

            Button(
                onClick = { navController.navigate(Screen.PdfPreview.route) },
                enabled = selectedFiles.isNotEmpty(),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Generate PDF")
            }
        }

        if (errorMessage != null) {
            ErrorDialog(
                title = "Error",
                message = errorMessage.orEmpty(),
                onDismiss = { viewModel.clearError() }
            )
        }
    }
}
