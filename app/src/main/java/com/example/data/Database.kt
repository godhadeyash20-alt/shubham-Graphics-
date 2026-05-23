package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [FinancialTransaction::class, BusinessInvestment::class],
    version = 1,
    exportSchema = false
)
abstract class ShubhamGraphicsDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    abstract fun investmentDao(): InvestmentDao

    companion object {
        @Volatile
        private var INSTANCE: ShubhamGraphicsDatabase? = null

        fun getDatabase(context: Context): ShubhamGraphicsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ShubhamGraphicsDatabase::class.java,
                    "shubham_graphics_finance_db"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
