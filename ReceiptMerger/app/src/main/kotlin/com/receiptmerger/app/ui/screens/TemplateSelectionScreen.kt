package com.receiptmerger.app.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.receiptmerger.app.ui.navigation.Screen
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TemplateSelectionScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    val templates = listOf(
        TemplateOption(
            id = "collage3",
            title = "3 Receipts per A4",
            description = "Three cropped sub-orders on each A4 page"
        ),
        TemplateOption(
            id = "collage2",
            title = "2 Receipts per A4",
            description = "Two cropped sub-orders on each A4 page"
        ),
        TemplateOption(
            id = "grid_rc",
            title = "Grid Layout (R*C)",
            description = "Custom grid arrangement (e.g. 2x2 or 3x2) for multiple receipts"
        ),
    )

    val currentTemplate by viewModel.currentTemplate.collectAsState()
    val selectedTemplate = remember { mutableStateOf<String?>(currentTemplate) }
    var showSettings by remember { mutableStateOf(false) }
    val gridRows by viewModel.gridRows.collectAsState()
    val gridCols by viewModel.gridCols.collectAsState()

    val onTemplateSelected = { id: String ->
        selectedTemplate.value = id
        viewModel.setTemplate(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Choose Template") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showSettings = true }) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Choose how many receipts to place on each A4 page",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templates) { template ->
                    TemplateCard(
                        template = template,
                        isSelected = selectedTemplate.value == template.id,
                        onSelect = { onTemplateSelected(template.id) }
                    )
                }
            }

            if (selectedTemplate.value == "grid_rc") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = gridRows.toString(),
                        onValueChange = { viewModel.setGridDimensions(it.toIntOrNull() ?: 1, gridCols) },
                        label = { Text("Rows") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = gridCols.toString(),
                        onValueChange = { viewModel.setGridDimensions(gridRows, it.toIntOrNull() ?: 1) },
                        label = { Text("Cols") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }

            Button(
                onClick = { navController.navigate(Screen.MultiFilePicker.route) },
                enabled = true,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }

        if (showSettings) {
            SettingsDialog(
                viewModel = viewModel,
                onDismiss = { showSettings = false }
            )
        }
    }
}

@Composable
fun SettingsDialog(viewModel: ReceiptMergerViewModel, onDismiss: () -> Unit) {
    val quality by viewModel.pdfQuality.collectAsState()
    val signature by viewModel.userSignature.collectAsState()
    val signatureImageUri by viewModel.userSignatureImageUri.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.setSignatureImage(uri)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("PDF Settings") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Image Quality", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(25, 50, 75, 100).forEach { q ->
                        Button(
                            onClick = { viewModel.setQuality(q) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (quality == q) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(4.dp)
                        ) {
                            Text("${q}%", fontSize = MaterialTheme.typography.labelSmall.fontSize)
                        }
                    }
                }
                
                OutlinedTextField(
                    value = signature ?: "",
                    onValueChange = { viewModel.setSignature(it) },
                    label = { Text("Custom Footer/Signature") },
                    placeholder = { Text("e.g. Thank you for shopping!") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text("Signature Image (Canva)", style = MaterialTheme.typography.labelLarge)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { launcher.launch("image/*") },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(if (signatureImageUri == null) "Select Image" else "Change Image")
                    }
                    
                    if (signatureImageUri != null) {
                        IconButton(onClick = { viewModel.setSignatureImage(null) }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear Image")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Done") }
        }
    )
}

data class TemplateOption(
    val id: String,
    val title: String,
    val description: String,
)

@Composable
fun TemplateCard(
    template: TemplateOption,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else MaterialTheme.colorScheme.surface,
                shape = MaterialTheme.shapes.medium
            )
            .selectable(
                selected = isSelected,
                onClick = onSelect,
                role = Role.RadioButton
            )
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    template.title,
                    style = MaterialTheme.typography.bodyLarge,
                )
                Text(
                    template.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(
                selected = isSelected,
                onClick = onSelect
            )
        }
    }
}
