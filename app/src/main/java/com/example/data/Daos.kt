package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM transactions WHERE isExpense = 1 ORDER BY date DESC")
    fun getExpenses(): Flow<List<FinancialTransaction>>

    @Query("SELECT * FROM transactions WHERE isExpense = 0 ORDER BY date DESC")
    fun getRevenue(): Flow<List<FinancialTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: FinancialTransaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Long)

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1")
    fun getTotalExpenseFlow(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0")
    fun getTotalRevenueFlow(): Flow<Double?>
}

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments ORDER BY date DESC")
    fun getAllInvestments(): Flow<List<BusinessInvestment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(investment: BusinessInvestment)

    @Query("DELETE FROM investments WHERE id = :id")
    suspend fun deleteInvestmentById(id: Long)

    @Query("SELECT SUM(amount) FROM investments")
    fun getTotalInvestmentFlow(): Flow<Double?>
}
