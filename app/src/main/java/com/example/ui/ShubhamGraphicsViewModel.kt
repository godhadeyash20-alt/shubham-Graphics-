package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.BusinessInvestment
import com.example.data.FinancialTransaction
import com.example.data.ShubhamGraphicsDatabase
import com.example.data.ShubhamGraphicsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ShubhamGraphicsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ShubhamGraphicsRepository

    // Expose lists reactively
    val allTransactions: StateFlow<List<FinancialTransaction>>
    val allExpenses: StateFlow<List<FinancialTransaction>>
    val allRevenue: StateFlow<List<FinancialTransaction>>
    val allInvestments: StateFlow<List<BusinessInvestment>>

    // Expose aggregates
    val totalExpense: StateFlow<Double>
    val totalRevenue: StateFlow<Double>
    val totalInvestment: StateFlow<Double>

    // UI state filters
    private val _selectedCategoryFilter = MutableStateFlow("All")
    val selectedCategoryFilter: StateFlow<String> = _selectedCategoryFilter.asStateFlow()

    init {
        val database = ShubhamGraphicsDatabase.getDatabase(application)
        repository = ShubhamGraphicsRepository(
            database.transactionDao(),
            database.investmentDao()
        )

        // Bind flows from repository
        allTransactions = repository.allTransactions.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allExpenses = repository.allExpenses.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allRevenue = repository.allRevenue.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allInvestments = repository.allInvestments.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        totalExpense = repository.totalExpense.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

        totalRevenue = repository.totalRevenue.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

        totalInvestment = repository.totalInvestment.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0.0
        )

        // Spark demo data on separate coroutine
        viewModelScope.launch {
            seedDemoDataIfEmpty()
        }
    }

    private suspend fun seedDemoDataIfEmpty() {
        val currentTrans = repository.allTransactions.first()
        if (currentTrans.isEmpty()) {
            // Seed Transactions
            val starterTransactions = listOf(
                FinancialTransaction(
                    title = "Starflex Backlit Flex Roll 10x100ft",
                    amount = 12500.0,
                    isExpense = true,
                    category = "Materials",
                    qtyOrSize = "1 Roll",
                    notes = "Heavy duty backlit media for outdoor box signage"
                ),
                FinancialTransaction(
                    title = "Seiko Solvent Yellow Ink Can",
                    amount = 4800.0,
                    isExpense = true,
                    category = "Ink",
                    qtyOrSize = "5 L Can",
                    notes = "Yellow pigment ink for Seiko print heads"
                ),
                FinancialTransaction(
                    title = "Airtel Canopy Graphic Flex",
                    amount = 28600.0,
                    isExpense = false,
                    category = "Revenue",
                    qtyOrSize = "6 Sets",
                    notes = "Canopy frame design + Starflex frontlit mounting"
                ),
                FinancialTransaction(
                    title = "High Voltage Industrial Power",
                    amount = 15900.0,
                    isExpense = true,
                    category = "Overheads",
                    qtyOrSize = "1420 KWh",
                    notes = "Monthly bill for Seiko & HP Latex operation"
                ),
                FinancialTransaction(
                    title = "Lenskart Glow Sign Board",
                    amount = 45000.0,
                    isExpense = false,
                    category = "Revenue",
                    qtyOrSize = "12x6 ft",
                    notes = "Fabrication with LEDs and acrylic dimensional logo"
                ),
                FinancialTransaction(
                    title = "Gloss Car Vinyl Wrap Media",
                    amount = 6500.0,
                    isExpense = true,
                    category = "Materials",
                    qtyOrSize = "2 Rolls",
                    notes = "Bubble-free premium cast vinyl for vehicle wrappers"
                ),
                FinancialTransaction(
                    title = "Late Night Shift Rush Wages",
                    amount = 7500.0,
                    isExpense = true,
                    category = "Labor",
                    qtyOrSize = "5 Designers",
                    notes = "Overtime work for political campaign poster printing"
                )
            )

            for (trans in starterTransactions) {
                repository.insertTransaction(trans)
            }
        }

        val currentInvests = repository.allInvestments.first()
        if (currentInvests.isEmpty()) {
            // Seed Investments
            val starterInvestments = listOf(
                BusinessInvestment(
                    name = "HP Latex 365 Large Format Printer",
                    amount = 420000.0,
                    category = "Machinery",
                    status = "Active",
                    notes = "Enables odorless, scratch-resistant indoor signage and ecological ink compliance",
                    expectedPowerUsageKw = 4.6,
                    expectedRevenueMultiplier = 1.6
                ),
                BusinessInvestment(
                    name = "CNC Router Acrylic Engraver Bed",
                    amount = 280000.0,
                    category = "Machinery",
                    status = "Planned",
                    notes = "Enables custom dimensional acrylic letters and metal board carving",
                    expectedPowerUsageKw = 3.2,
                    expectedRevenueMultiplier = 1.4
                ),
                BusinessInvestment(
                    name = "Glow Board Light Matrix R&D",
                    amount = 55000.0,
                    category = "Tech/Software",
                    status = "Active",
                    notes = "Testing smart programmable LED matrix modules for dynamic printing products",
                    expectedPowerUsageKw = 1.0,
                    expectedRevenueMultiplier = 1.2
                )
            )

            for (inv in starterInvestments) {
                repository.insertInvestment(inv)
            }
        }
    }

    fun setCategoryFilter(filter: String) {
        _selectedCategoryFilter.value = filter
    }

    fun insertTransaction(
        title: String,
        amount: Double,
        isExpense: Boolean,
        category: String,
        qtyOrSize: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertTransaction(
                FinancialTransaction(
                    title = title,
                    amount = amount,
                    isExpense = isExpense,
                    category = category,
                    qtyOrSize = qtyOrSize,
                    notes = notes
                )
            )
        }
    }

    fun deleteTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun insertInvestment(
        name: String,
        amount: Double,
        category: String,
        status: String,
        expectedPowerUsageKw: Double,
        expectedMultiplier: Double,
        notes: String
    ) {
        viewModelScope.launch {
            repository.insertInvestment(
                BusinessInvestment(
                    name = name,
                    amount = amount,
                    category = category,
                    status = status,
                    expectedPowerUsageKw = expectedPowerUsageKw,
                    expectedRevenueMultiplier = expectedMultiplier,
                    notes = notes
                )
            )
        }
    }

    fun deleteInvestment(id: Long) {
        viewModelScope.launch {
            repository.deleteInvestment(id)
        }
    }
}
