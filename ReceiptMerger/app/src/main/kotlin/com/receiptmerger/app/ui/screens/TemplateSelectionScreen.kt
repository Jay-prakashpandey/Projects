package com.receiptmerger.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
            id = "standard",
            title = "Standard Receipt",
            description = "Simple receipt format with item list and total"
        ),
        TemplateOption(
            id = "image_merge",
            title = "Image to PDF",
            description = "Convert images into a single PDF document"
        ),
        TemplateOption(
            id = "pdf_merge",
            title = "PDF Merge",
            description = "Merge multiple PDF files into one"
        ),
        TemplateOption(
            id = "invoice",
            title = "Invoice Template",
            description = "Professional invoice format with company info"
        ),
        TemplateOption(
            id = "receipt_tax",
            title = "Receipt with Tax",
            description = "Receipt format including tax calculations"
        ),
    )

    val selectedTemplate = remember { mutableStateOf<String?>(null) }

    val onTemplateSelected = { id: String ->
        selectedTemplate.value = id
        viewModel.setTemplate(id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Template") },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                "Choose a template for your merged document",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(templates.size) { index ->
                    TemplateCard(
                        template = templates[index],
                        isSelected = selectedTemplate.value == templates[index].id,
                        onSelect = { onTemplateSelected(templates[index].id) }
                    )
                }
            }

            Button(
                onClick = { navController.navigate(Screen.MultiFilePicker.route) },
                enabled = selectedTemplate.value != null,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    }
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
        androidx.compose.foundation.layout.Row(
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
