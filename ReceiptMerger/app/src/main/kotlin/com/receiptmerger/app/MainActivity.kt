package com.receiptmerger.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.receiptmerger.app.data.db.ReceiptMergerDatabase
import com.receiptmerger.app.ui.navigation.NavGraph
import com.receiptmerger.app.ui.theme.ReceiptMergerTheme

class MainActivity : ComponentActivity() {
    private lateinit var database: ReceiptMergerDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize database
        database = ReceiptMergerDatabase.getInstance(this)

        setContent {
            ReceiptMergerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    NavGraph(database = database)
                }
            }
        }
    }
}
