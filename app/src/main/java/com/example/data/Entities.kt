package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transactions")
data class FinancialTransaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val title: String,
    val amount: Double,
    val isExpense: Boolean, // true for expense/payout, false for revenue/payment received
    val category: String, // Materials, Ink, Machinery, Overheads, Revenue, etc.
    val date: Long = System.currentTimeMillis(),
    val qtyOrSize: String = "", // e.g., "120 sq.ft", "5 Litres", "2 Rolls"
    val notes: String = ""
) : Serializable

@Entity(tableName = "investments")
data class BusinessInvestment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String, // e.g., "HP Latex 365 Printer"
    val amount: Double,
    val date: Long = System.currentTimeMillis(),
    val category: String, // Machinery, Infrastructure, Marketing, Tech/Software
    val status: String = "Active", // Active, Planned, Fully Depreciated
    val notes: String = "",
    val expectedPowerUsageKw: Double = 0.0, // Specific graphic shop field
    val expectedRevenueMultiplier: Double = 1.2 // Multiplier of direct cost returned
) : Serializable
