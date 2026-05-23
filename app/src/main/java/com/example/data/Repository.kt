package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ShubhamGraphicsRepository(
    private val transactionDao: TransactionDao,
    private val investmentDao: InvestmentDao
) {
    val allTransactions: Flow<List<FinancialTransaction>> = transactionDao.getAllTransactions()
    val allExpenses: Flow<List<FinancialTransaction>> = transactionDao.getExpenses()
    val allRevenue: Flow<List<FinancialTransaction>> = transactionDao.getRevenue()
    
    val allInvestments: Flow<List<BusinessInvestment>> = investmentDao.getAllInvestments()

    val totalExpense: Flow<Double> = transactionDao.getTotalExpenseFlow().map { it ?: 0.0 }
    val totalRevenue: Flow<Double> = transactionDao.getTotalRevenueFlow().map { it ?: 0.0 }
    val totalInvestment: Flow<Double> = investmentDao.getTotalInvestmentFlow().map { it ?: 0.0 }

    suspend fun insertTransaction(transaction: FinancialTransaction) {
        transactionDao.insertTransaction(transaction)
    }

    suspend fun deleteTransaction(id: Long) {
        transactionDao.deleteTransactionById(id)
    }

    suspend fun insertInvestment(investment: BusinessInvestment) {
        investmentDao.insertInvestment(investment)
    }

    suspend fun deleteInvestment(id: Long) {
        investmentDao.deleteInvestmentById(id)
    }
}
