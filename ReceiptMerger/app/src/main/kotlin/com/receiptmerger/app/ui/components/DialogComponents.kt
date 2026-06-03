package com.receiptmerger.app.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ErrorDialog(
    title: String,
    message: String,
    onDismiss: () -> Unit,
    onRetry: (() -> Unit)? = null,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("OK")
            }
        },
        dismissButton = if (onRetry != null) {
            {
                TextButton(onClick = onRetry) {
                    Text("Retry")
                }
            }
        } else null,
    )
}

@Composable
fun ConfirmDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    confirmText: String = "Confirm",
    cancelText: String = "Cancel",
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(title) },
        text = { Text(message) },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(cancelText)
            }
        },
    )
}

@Composable
fun LoadingDialog(
    title: String = "Processing...",
    message: String = "Please wait",
) {
    AlertDialog(
        onDismissRequest = {},
        title = { Text(title) },
        text = { 
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(message)
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        },
        confirmButton = {},
    )
}
