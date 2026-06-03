package com.receiptmerger.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object TemplateSelection : Screen("template_selection")
    data object MultiFilePicker : Screen("multi_file_picker")
    data object PdfPreview : Screen("pdf_preview")
    data object SaveShare : Screen("save_share")
}
