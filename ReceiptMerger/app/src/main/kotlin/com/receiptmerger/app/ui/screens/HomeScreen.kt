package com.receiptmerger.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.receiptmerger.app.ui.navigation.Screen
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: ReceiptMergerViewModel) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Receipt Merger") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.reset()
                    navController.navigate(Screen.TemplateSelection.route)
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Project")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Welcome to Receipt Merger",
                modifier = Modifier.padding(top = 32.dp),
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall
            )
            
            Text(
                "Create 2-per-A4 or 3-per-A4 receipt PDFs",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            ElevatedButton(
                onClick = {
                    viewModel.reset()
                    navController.navigate(Screen.TemplateSelection.route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text("Create New Project")
            }

            ElevatedButton(
                onClick = { navController.navigate(Screen.SavedPdfs.route) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Previously Created PDFs")
            }
        }
    }
}
