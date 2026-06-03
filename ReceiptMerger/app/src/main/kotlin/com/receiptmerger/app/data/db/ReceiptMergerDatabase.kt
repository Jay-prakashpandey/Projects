package com.receiptmerger.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.receiptmerger.app.data.MergeProjectEntity
import com.receiptmerger.app.data.ReceiptEntity
import com.receiptmerger.app.data.dao.MergeProjectDao
import com.receiptmerger.app.data.dao.ReceiptDao

@Database(
    entities = [ReceiptEntity::class, MergeProjectEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ReceiptMergerDatabase : RoomDatabase() {
    abstract fun receiptDao(): ReceiptDao
    abstract fun projectDao(): MergeProjectDao

    companion object {
        @Volatile
        private var INSTANCE: ReceiptMergerDatabase? = null

        fun getInstance(context: Context): ReceiptMergerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ReceiptMergerDatabase::class.java,
                    "receipt_merger_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
