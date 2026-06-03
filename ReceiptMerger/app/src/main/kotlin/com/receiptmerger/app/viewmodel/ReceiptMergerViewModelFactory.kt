package com.receiptmerger.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.receiptmerger.app.data.db.ReceiptMergerDatabase

class ReceiptMergerViewModelFactory(
    private val database: ReceiptMergerDatabase?
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ReceiptMergerViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ReceiptMergerViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
