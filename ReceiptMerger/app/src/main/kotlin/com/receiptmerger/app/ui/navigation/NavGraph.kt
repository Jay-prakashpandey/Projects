package com.receiptmerger.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.receiptmerger.app.data.db.ReceiptMergerDatabase
import com.receiptmerger.app.ui.screens.HomeScreen
import com.receiptmerger.app.ui.screens.TemplateSelectionScreen
import com.receiptmerger.app.ui.screens.MultiFilePickerScreen
import com.receiptmerger.app.ui.screens.PdfPreviewScreen
import com.receiptmerger.app.ui.screens.SaveShareScreen
import com.receiptmerger.app.ui.screens.SavedPdfsScreen
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModel
import com.receiptmerger.app.viewmodel.ReceiptMergerViewModelFactory

@Composable
fun NavGraph(database: ReceiptMergerDatabase) {
    val navController = rememberNavController()
    // Create a single shared ViewModel instance for all screens
    val viewModel: ReceiptMergerViewModel = viewModel(
        factory = ReceiptMergerViewModelFactory(database)
    )

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.TemplateSelection.route) {
            TemplateSelectionScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.MultiFilePicker.route) {
            MultiFilePickerScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.PdfPreview.route) {
            PdfPreviewScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.SaveShare.route) {
            SaveShareScreen(navController = navController, viewModel = viewModel)
        }
        composable(Screen.SavedPdfs.route) {
            SavedPdfsScreen(navController = navController)
        }
    }
}
