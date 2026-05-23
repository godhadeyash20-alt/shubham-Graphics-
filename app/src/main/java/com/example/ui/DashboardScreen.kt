package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.ui.draw.blur
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.BusinessInvestment
import com.example.data.FinancialTransaction
import com.example.ui.theme.*
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: ShubhamGraphicsViewModel,
    modifier: Modifier = Modifier
) {
    val transactions by viewModel.allTransactions.collectAsStateWithLifecycle()
    val expensesList by viewModel.allExpenses.collectAsStateWithLifecycle()
    val revenueList by viewModel.allRevenue.collectAsStateWithLifecycle()
    val investments by viewModel.allInvestments.collectAsStateWithLifecycle()

    val totalExpense by viewModel.totalExpense.collectAsStateWithLifecycle()
    val totalRevenue by viewModel.totalRevenue.collectAsStateWithLifecycle()
    val totalInvestment by viewModel.totalInvestment.collectAsStateWithLifecycle()

    val filterCategory by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()

    var activeTab by remember { mutableStateOf("Cashflow") } // Cashflow, Billing, Investments, Analytics

    // Dialog sheets state
    var showAddTransDialog by remember { mutableStateOf(false) }
    var showAddInvestDialog by remember { mutableStateOf(false) }

    // Number formatting helper
    val currencyFormatter = remember {
        NumberFormat.getCurrencyInstance(Locale("en", "IN")).apply {
            maximumFractionDigits = 0
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(SlateBlack),
        topBar = {
            Column(
                modifier = Modifier
                    .background(SlateBlack)
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                // Header with alignment crosshairs (Cyan, Magenta, Yellow, Black)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "SHUBHAM GRAPHICS",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = TitleWhite
                            )
                            // CMYK Dots indicator mimicking print registration layout
                            Spacer(modifier = Modifier.width(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(CyanPrimary))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(MagentaSecondary))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(YellowTertiary))
                                Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(Color.White))
                            }
                        }
                        Text(
                            text = "LARGE FORMAT FLEX PRINTING INTEL",
                            style = MaterialTheme.typography.labelSmall,
                            color = BodyGray,
                            fontSize = 9.sp
                        )
                    }

                    // Theme selection and nice minimalist date display
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Theme Toggle Button
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(SoftCardBg)
                                .clickable {
                                    isLightModeTheme = !isLightModeTheme
                                }
                                .padding(8.dp)
                                .testTag("theme_toggle_button")
                        ) {
                            Icon(
                                imageVector = if (isLightModeTheme) Icons.Rounded.LightMode else Icons.Rounded.DarkMode,
                                contentDescription = "Toggle Theme",
                                tint = if (isLightModeTheme) YellowTertiary else CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }

                        val currentDate = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date()) }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(30.dp))
                                .background(SoftCardBg)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Rounded.Event,
                                    contentDescription = "Date",
                                    tint = CyanPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = currentDate.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = TitleWhite,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            if (activeTab == "Cashflow") {
                FloatingActionButton(
                    onClick = { showAddTransDialog = true },
                    containerColor = CyanPrimary,
                    contentColor = SlateBlack,
                    modifier = Modifier
                        .testTag("add_transaction_fab")
                        .padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Add,
                        contentDescription = "New Log",
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else if (activeTab == "Investments") {
                FloatingActionButton(
                    onClick = { showAddInvestDialog = true },
                    containerColor = YellowTertiary,
                    contentColor = SlateBlack,
                    modifier = Modifier
                        .testTag("add_investment_fab")
                        .padding(bottom = 16.dp, end = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PostAdd,
                        contentDescription = "New Equipment",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(SlateBlack)
        ) {
            // Main balance card
            Spacer(modifier = Modifier.height(8.dp))
            BalanceMetricsView(
                totalRevenue = totalRevenue,
                totalExpense = totalExpense,
                totalInvestment = totalInvestment,
                currencyFormatter = currencyFormatter
            )

            // Dynamic view selector switcher (Tabs)
            Spacer(modifier = Modifier.height(20.dp))
            SegmentedTabView(
                activeTab = activeTab,
                onTabSelected = { activeTab = it }
            )

            // Filtering Chips (only show for Cashflow)
            if (activeTab == "Cashflow") {
                Spacer(modifier = Modifier.height(14.dp))
                CategoryChipsView(
                    selectedCategory = filterCategory,
                    onSelectedChange = { viewModel.setCategoryFilter(it) }
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Body Area
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(DeepSlateSurface)
                    .padding(horizontal = 20.dp)
            ) {
                when (activeTab) {
                    "Cashflow" -> {
                        val filteredList = remember(transactions, filterCategory) {
                            if (filterCategory == "All") {
                                transactions
                            } else {
                                transactions.filter {
                                    it.category.lowercase() == filterCategory.lowercase() ||
                                            (filterCategory == "Revenue" && !it.isExpense) ||
                                            (filterCategory == "Materials" && it.category.equals("Materials", true)) ||
                                            (filterCategory == "Ink" && it.category.equals("Ink", true))
                                }
                            }
                        }

                        if (filteredList.isEmpty()) {
                            EmptyStateView(
                                title = "No Print Studio Logs",
                                message = "There are no transactions listed in this category. Click '+' to log material rolls or ink purchases."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                item {
                                    Text(
                                        text = "RECENT LEDGER ACCOUNTS",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BodyGray,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                items(filteredList) { trans ->
                                    TransactionItemRow(
                                        transaction = trans,
                                        currencyFormatter = currencyFormatter,
                                        onDelete = { viewModel.deleteTransaction(trans.id) }
                                    )
                                }
                            }
                        }
                    }

                    "Billing" -> {
                        BillingView(
                            viewModel = viewModel,
                            transactions = transactions,
                            currencyFormatter = currencyFormatter
                        )
                    }

                    "Investments" -> {
                        if (investments.isEmpty()) {
                            EmptyStateView(
                                title = "No Capital Investments",
                                message = "No high-ticket machinery has been invested in yet. Feed printer setup to track growth multipliers."
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(14.dp)
                            ) {
                                item {
                                    Text(
                                        text = "PRINT SHOP CAPITAL MACHINERY",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = BodyGray,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    )
                                }
                                items(investments) { invest ->
                                    InvestmentCardItem(
                                        investment = invest,
                                        currencyFormatter = currencyFormatter,
                                        onDelete = { viewModel.deleteInvestment(invest.id) }
                                    )
                                }
                            }
                        }
                    }

                    "Analytics" -> {
                        AnalyticsView(
                            transactions = transactions,
                            investments = investments,
                            totalRevenue = totalRevenue,
                            totalExpense = totalExpense,
                            currencyFormatter = currencyFormatter
                        )
                    }
                }
            }
        }
    }

    // Modal dialog for adding transaction
    if (showAddTransDialog) {
        AddTransactionModal(
            onDismiss = { showAddTransDialog = false },
            onConfirm = { title, amt, isExp, category, qtySize, notes ->
                viewModel.insertTransaction(title, amt, isExp, category, qtySize, notes)
                showAddTransDialog = false
            }
        )
    }

    // Modal dialog for adding investment
    if (showAddInvestDialog) {
        AddInvestmentModal(
            onDismiss = { showAddInvestDialog = false },
            onConfirm = { name, amt, category, status, powerKw, multiplier, notes ->
                viewModel.insertInvestment(name, amt, category, status, powerKw, multiplier, notes)
                showAddInvestDialog = false
            }
        )
    }
}

@Composable
fun GlassBox(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 20.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        // Natural Blur Background Layer
        Box(
            modifier = Modifier
                .matchParentSize()
                .blur(16.dp)
                .background(
                    color = if (isLightModeTheme) Color.White.copy(alpha = 0.45f) else Color(0x331C2230),
                    shape = RoundedCornerShape(cornerRadius)
                )
        )
        
        // Polished light-reflective glass borders of the printing press
        Box(
            modifier = Modifier
                .matchParentSize()
                .border(
                    width = 1.dp,
                    brush = Brush.verticalGradient(
                        colors = if (isLightModeTheme) {
                            listOf(Color.White.copy(alpha = 0.85f), Color.Black.copy(alpha = 0.08f))
                        } else {
                            listOf(Color.White.copy(alpha = 0.15f), Color.White.copy(alpha = 0.02f))
                        }
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .background(
                    color = if (isLightModeTheme) Color(0xCCFFFFFF) else Color(0x77141822),
                    shape = RoundedCornerShape(cornerRadius)
                )
        )

        // Contents
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            content()
        }
    }
}

@Composable
fun BalanceMetricsView(
    totalRevenue: Double,
    totalExpense: Double,
    totalInvestment: Double,
    currencyFormatter: NumberFormat
) {
    val netProfit = totalRevenue - totalExpense

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        // Futuristic Glow Net Margin Card wrapped in dynamic Glassmorphic container
        GlassBox(
            cornerRadius = 20.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .drawBehind {
                        // Accent laser line representing plotter cut outline
                        drawLine(
                            color = if (netProfit >= 0) CyanPrimary else LossRed,
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f),
                            strokeWidth = 3.dp.toPx()
                        )
                    }
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.AccountBalanceWallet,
                            contentDescription = "Wallet",
                            tint = BodyGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "NET STUDIO OPERATING SURPLUS",
                            style = MaterialTheme.typography.labelSmall,
                            color = BodyGray,
                            letterSpacing = 1.sp
                        )
                    }

                    // Profit / Loss status indicator
                    val indicatorBg = if (netProfit >= 0) {
                        if (isLightModeTheme) Color(0xFFDCFCE7) else Color(0xFF003B26)
                    } else {
                        if (isLightModeTheme) Color(0xFFFEE2E2) else Color(0xFF4A0F1E)
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(indicatorBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (netProfit >= 0) "SURPLUS" else "DEFICIT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (netProfit >= 0) ProfitGreen else LossRed,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Beautiful numeric layout
                Text(
                    text = currencyFormatter.format(netProfit),
                    style = MaterialTheme.typography.displayMedium,
                    color = TitleWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Fine dividing line
                val dividerColor = if (isLightModeTheme) Color(0xFFCBD5E1) else Color(0xFF1E293B)
                Divider(color = dividerColor, thickness = 1.dp)

                Spacer(modifier = Modifier.height(12.dp))

                // Cyan & Magenta visual print bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(CyanPrimary))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "REVENUE",
                                style = MaterialTheme.typography.labelSmall,
                                color = BodyGray
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = currencyFormatter.format(totalRevenue),
                            style = MaterialTheme.typography.titleMedium,
                            color = TitleWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(MagentaSecondary))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "EXPENSES CURRENT",
                                style = MaterialTheme.typography.labelSmall,
                                color = BodyGray
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = currencyFormatter.format(totalExpense),
                            style = MaterialTheme.typography.titleMedium,
                            color = TitleWhite,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick mini ticker with beautiful glass background
        GlassBox(
            cornerRadius = 12.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Rounded.PrecisionManufacturing,
                        contentDescription = "Machinery",
                        tint = YellowTertiary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "MACHINERY ASSETS CAPEX",
                        style = MaterialTheme.typography.labelSmall,
                        color = TitleWhite,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = currencyFormatter.format(totalInvestment),
                    style = MaterialTheme.typography.labelSmall,
                    color = YellowTertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun SegmentedTabView(
    activeTab: String,
    onTabSelected: (String) -> Unit
) {
    val tabs = listOf("Cashflow", "Billing", "Investments", "Analytics")
    
    GlassBox(
        cornerRadius = 14.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            tabs.forEach { tabName ->
                val isSelected = activeTab == tabName
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                if (isLightModeTheme) Color(0xFFCBD5E1).copy(alpha = 0.6f) else Color(0xFF0C0E14)
                            } else Color.Transparent
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) {
                                if (isLightModeTheme) Color(0xFF94A3B8) else Color(0xFF1E293B)
                            } else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { onTabSelected(tabName) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabName.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            when (tabName) {
                                "Cashflow" -> CyanPrimary
                                "Billing" -> CyanPrimary
                                "Investments" -> YellowTertiary
                                else -> MagentaSecondary
                            }
                        } else BodyGray,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryChipsView(
    selectedCategory: String,
    onSelectedChange: (String) -> Unit
) {
    val categories = listOf("All", "Revenue", "Materials", "Ink", "Labor", "Overheads")
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { cat ->
            val isSelected = selectedCategory == cat
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(30.dp))
                    .background(if (isSelected) CyanPrimary else SoftCardBg)
                    .clickable { onSelectedChange(cat) }
                    .border(
                        1.dp,
                        if (isSelected) CyanPrimary else Color(0xFF263043),
                        RoundedCornerShape(30.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = cat,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) SlateBlack else TitleWhite,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun TransactionItemRow(
    transaction: FinancialTransaction,
    currencyFormatter: NumberFormat,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val contentBg = if (isExpanded) SoftCardBg else DeepSlateSurface

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF1B2232), RoundedCornerShape(16.dp))
            .background(contentBg)
            .clickable { isExpanded = !isExpanded }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Circular icon representing category
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            if (transaction.isExpense) Color(0xFF2F0F1E) else Color(0xFF0F3229)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val icon = when (transaction.category.lowercase()) {
                        "materials" -> Icons.Rounded.Category
                        "ink" -> Icons.Rounded.Opacity
                        "labor" -> Icons.Rounded.Engineering
                        "overheads" -> Icons.Rounded.FlashOn
                        "revenue" -> Icons.Rounded.CallMade
                        else -> Icons.Rounded.Feed
                    }
                    val iconColor = if (transaction.isExpense) MagentaSecondary else CyanPrimary
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.category,
                        tint = iconColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Title + Print Detail tag
                Column {
                    Text(
                        text = transaction.title,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TitleWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = transaction.category.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            color = BodyGray,
                            fontSize = 9.sp
                        )

                        if (transaction.qtyOrSize.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF232B3D))
                                    .padding(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = transaction.qtyOrSize,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = CyanPrimary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Amount with trailing icon
            Column(
                horizontalAlignment = Alignment.End,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = if (transaction.isExpense) {
                        "- " + currencyFormatter.format(transaction.amount)
                    } else {
                        "+ " + currencyFormatter.format(transaction.amount)
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (transaction.isExpense) MagentaSecondary else ProfitGreen,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp
                )

                val formattedDate = remember(transaction.date) {
                    SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(transaction.date))
                }
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelSmall,
                    color = BodyGray,
                    fontSize = 10.sp
                )
            }
        }

        // Expanded details
        if (isExpanded) {
            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = Color(0xFF2D3748), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(8.dp))

            if (transaction.notes.isNotEmpty()) {
                Text(
                    text = "NOTES",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanPrimary,
                    fontSize = 9.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.notes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TitleWhite,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Expanded Actions (Delete Account)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LossRed
                    ),
                    border = BorderStroke(1.dp, LossRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "PURGE LOG",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun InvestmentCardItem(
    investment: BusinessInvestment,
    currencyFormatter: NumberFormat,
    onDelete: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Color(0xFF222C3F), RoundedCornerShape(16.dp))
            .background(if (isExpanded) SoftCardBg else DeepSlateSurface)
            .clickable { isExpanded = !isExpanded }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Machine Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF332912)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.PrecisionManufacturing,
                        contentDescription = "Printer",
                        tint = YellowTertiary,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = investment.name,
                        style = MaterialTheme.typography.bodyLarge,
                        color = TitleWhite,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF2C1935))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "MULTIPLIER: ${investment.expectedRevenueMultiplier}x",
                                style = MaterialTheme.typography.labelSmall,
                                color = YellowTertiary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF192A35))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${investment.expectedPowerUsageKw} kW",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanPrimary,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = currencyFormatter.format(investment.amount),
                    style = MaterialTheme.typography.bodyLarge,
                    color = YellowTertiary,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 15.sp
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            if (investment.status.lowercase() == "active") Color(0xFF003D2E) else Color(0xFF3D3610)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = investment.status.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (investment.status.lowercase() == "active") ProfitGreen else YellowTertiary,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        if (isExpanded) {
            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFF222C3F), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "BUSINESS DEPRECIATION & VALUE PROFILE",
                style = MaterialTheme.typography.labelSmall,
                color = BodyGray,
                fontSize = 9.sp,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = investment.notes.ifEmpty { "No investment synopsis provided." },
                style = MaterialTheme.typography.bodyMedium,
                color = TitleWhite,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Status info
                val formattedDate = remember(investment.date) {
                    SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(investment.date))
                }
                Text(
                    text = "ACQUIRED: $formattedDate",
                    style = MaterialTheme.typography.labelSmall,
                    color = BodyGray,
                    fontSize = 9.sp
                )

                // Purge Action
                OutlinedButton(
                    onClick = onDelete,
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = LossRed
                    ),
                    border = BorderStroke(1.dp, LossRed),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.DeleteOutline,
                        contentDescription = "Delete",
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "LIQUIDATE ASSET",
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AnalyticsView(
    transactions: List<FinancialTransaction>,
    investments: List<BusinessInvestment>,
    totalRevenue: Double,
    totalExpense: Double,
    currencyFormatter: NumberFormat
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "REAL-TIME CASH FLOW TREND (SIMULATED)",
                style = MaterialTheme.typography.labelSmall,
                color = BodyGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        // Custom sweeping Area Chart of Revenues vs Expenses
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SoftCardBg)
                    .border(1.dp, Color(0xFF222C3F), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Draw grid lines
                    val gridLines = 4
                    for (i in 0..gridLines) {
                        val y = height * (i.toFloat() / gridLines)
                        drawLine(
                            color = Color(0xFF212B3B),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    // Create beautiful hypothetical wave values that represent simulated Cashflow progression
                    val points = listOf(0.15f, 0.45f, 0.35f, 0.65f, 0.5f, 0.85f, 0.75f)
                    val splinePath = Path()
                    val backgroundPath = Path()

                    val segmentWidth = width / (points.size - 1)

                    points.forEachIndexed { index, ratio ->
                        val x = index * segmentWidth
                        val y = height - (ratio * height * 0.8f) // buffer boundaries

                        if (index == 0) {
                            splinePath.moveTo(x, y)
                            backgroundPath.moveTo(x, height)
                            backgroundPath.lineTo(x, y)
                        } else {
                            // cubic curve points
                            val prevX = (index - 1) * segmentWidth
                            val prevY = height - (points[index - 1] * height * 0.8f)
                            val controlX1 = prevX + (segmentWidth / 2f)
                            val controlY1 = prevY
                            val controlX2 = prevX + (segmentWidth / 2f)
                            val controlY2 = y

                            splinePath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                            backgroundPath.cubicTo(controlX1, controlY1, controlX2, controlY2, x, y)
                        }

                        if (index == points.size - 1) {
                            backgroundPath.lineTo(x, height)
                            backgroundPath.close()
                        }
                    }

                    // Draw gradient filling beneath line
                    drawPath(
                        path = backgroundPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                CyanPrimary.copy(alpha = 0.3f),
                                Color.Transparent
                            )
                        )
                    )

                    // Draw line
                    drawPath(
                        path = splinePath,
                        color = CyanPrimary,
                        style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                    )

                    // Draw pulsing coordinates
                    points.forEachIndexed { index, ratio ->
                        val x = index * segmentWidth
                        val y = height - (ratio * height * 0.8f)
                        drawCircle(
                            color = SlateBlack,
                            radius = 5.dp.toPx(),
                            center = Offset(x, y)
                        )
                        drawCircle(
                            color = CyanPrimary,
                            radius = 3.dp.toPx(),
                            center = Offset(x, y)
                        )
                    }
                }
            }
        }

        // CMYK distribution ratios
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = SoftCardBg
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF222C3F))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "BUDGET CATEGORY DISTRIBUTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = BodyGray,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val categories = listOf("Materials", "Ink", "Labor", "Overheads")
                    val totals = categories.associateWith { cat ->
                        transactions.filter { it.category.equals(cat, true) && it.isExpense }.sumOf { it.amount }
                    }
                    val grandTotal = totals.values.sum()

                    categories.forEach { cat ->
                        val amount = totals[cat] ?: 0.0
                        val ratio = if (grandTotal > 0) (amount / grandTotal) else 0.0
                        val color = when (cat.lowercase()) {
                            "materials" -> CyanPrimary
                            "ink" -> MagentaSecondary
                            "labor" -> YellowTertiary
                            else -> Color.White
                        }

                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(color)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = TitleWhite,
                                        fontSize = 13.sp
                                    )
                                }

                                Text(
                                    text = "${(ratio * 100).toInt()}%  (${currencyFormatter.format(amount)})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BodyGray,
                                    fontSize = 11.sp
                                )
                            }
                            
                            Spacer(modifier = Modifier.height(6.dp))
                            
                            // Custom progress track
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0xFF2A2F3D))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxHeight()
                                        .fillMaxWidth(ratio.toFloat())
                                        .background(color)
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyStateView(title: String, message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Feed,
                contentDescription = "Empty",
                tint = BodyGray.copy(alpha = 0.3f),
                modifier = Modifier.size(54.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = TitleWhite,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = BodyGray,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(horizontal = 16.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionModal(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, Boolean, String, String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var isExpense by remember { mutableStateOf(true) }
    var selectedCategory by remember { mutableStateOf("Materials") }
    var qtySize by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Materials", "Ink", "Labor", "Overheads", "Revenue")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = SoftCardBg
            ),
            border = BorderStroke(1.dp, Color(0xFF222D41))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "LOG NEW STUDIO RECORD",
                    style = MaterialTheme.typography.labelSmall,
                    color = CyanPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Daily Ledger Intake",
                    style = MaterialTheme.typography.titleLarge,
                    color = TitleWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Toggle Selector for Expense vs Revenue
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DeepSlateSurface)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isExpense) MagentaSecondary else Color.Transparent)
                            .clickable { 
                                isExpense = true 
                                selectedCategory = "Materials"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "EXPENSE PAYMENT",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isExpense) SlateBlack else BodyGray,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isExpense) CyanPrimary else Color.Transparent)
                            .clickable { 
                                isExpense = false 
                                selectedCategory = "Revenue"
                            }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "REVENUE INTAKE",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (!isExpense) SlateBlack else BodyGray,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Inputs
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Record Title (e.g. Starflex Roll)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = Color(0xFF222D41),
                        focusedLabelColor = CyanPrimary,
                        unfocusedLabelColor = BodyGray,
                        focusedTextColor = TitleWhite,
                        unfocusedTextColor = TitleWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_trans_title"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = amountStr,
                        onValueChange = { amountStr = it },
                        label = { Text("Amount (INR)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFF222D41),
                            focusedLabelColor = CyanPrimary,
                            unfocusedLabelColor = BodyGray,
                            focusedTextColor = TitleWhite,
                            unfocusedTextColor = TitleWhite
                        ),
                        modifier = Modifier.weight(1.2f).testTag("add_trans_amount"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = qtySize,
                        onValueChange = { qtySize = it },
                        label = { Text("Qty / Dimensions") },
                        placeholder = { Text("10x12 ft") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CyanPrimary,
                            unfocusedBorderColor = Color(0xFF222D41),
                            focusedLabelColor = CyanPrimary,
                            unfocusedLabelColor = BodyGray,
                            focusedTextColor = TitleWhite,
                            unfocusedTextColor = TitleWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                if (isExpense) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "SELECT EXPENSE CLASSIFICATION",
                        style = MaterialTheme.typography.labelSmall,
                        color = BodyGray,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Flow of chips to select category
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.filter { it != "Revenue" }.forEach { cat ->
                            val isSel = selectedCategory == cat
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MagentaSecondary else DeepSlateSurface)
                                    .border(1.dp, if (isSel) MagentaSecondary else Color(0xFF2F3748), RoundedCornerShape(8.dp))
                                    .clickable { selectedCategory = cat }
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = cat.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isSel) SlateBlack else TitleWhite,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Contextual Notes (Optional)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanPrimary,
                        unfocusedBorderColor = Color(0xFF222D41),
                        focusedLabelColor = CyanPrimary,
                        unfocusedLabelColor = BodyGray,
                        focusedTextColor = TitleWhite,
                        unfocusedTextColor = TitleWhite
                    ),
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCEL", color = BodyGray)
                    }

                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            if (title.isNotEmpty() && amt > 0.0) {
                                onConfirm(
                                    title,
                                    amt,
                                    isExpense,
                                    if (isExpense) selectedCategory else "Revenue",
                                    qtySize,
                                    notes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExpense) MagentaSecondary else CyanPrimary,
                            contentColor = SlateBlack
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f).testTag("add_trans_submit")
                    ) {
                        Text("COMMIT RECORD", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddInvestmentModal(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String, Double, Double, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Machinery") }
    var status by remember { mutableStateOf("Active") }
    var powerKwStr by remember { mutableStateOf("") }
    var multiplierStr by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf("Machinery", "Infrastructure", "Marketing", "Tech/Software")
    val statuses = listOf("Active", "Planned", "Retired")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            colors = CardDefaults.cardColors(
                containerColor = SoftCardBg
            ),
            border = BorderStroke(1.dp, Color(0xFF222D41))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "ACQUIRE STUDIO CAPACITY / CAPEX",
                    style = MaterialTheme.typography.labelSmall,
                    color = YellowTertiary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Text(
                    text = "Invest in print Machinery",
                    style = MaterialTheme.typography.titleLarge,
                    color = TitleWhite,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Equipment Name (e.g. Seiko UV Spot)") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YellowTertiary,
                        unfocusedBorderColor = Color(0xFF222D41),
                        focusedLabelColor = YellowTertiary,
                        unfocusedLabelColor = BodyGray,
                        focusedTextColor = TitleWhite,
                        unfocusedTextColor = TitleWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_invest_name"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = amountStr,
                    onValueChange = { amountStr = it },
                    label = { Text("Acquisition Cost (INR)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YellowTertiary,
                        unfocusedBorderColor = Color(0xFF222D41),
                        focusedLabelColor = YellowTertiary,
                        unfocusedLabelColor = BodyGray,
                        focusedTextColor = TitleWhite,
                        unfocusedTextColor = TitleWhite
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("add_invest_amount"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = powerKwStr,
                        onValueChange = { powerKwStr = it },
                        label = { Text("Power Draw (kW)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = YellowTertiary,
                            unfocusedBorderColor = Color(0xFF222D41),
                            focusedLabelColor = YellowTertiary,
                            unfocusedLabelColor = BodyGray,
                            focusedTextColor = TitleWhite,
                            unfocusedTextColor = TitleWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    // Multiplier
                    OutlinedTextField(
                        value = multiplierStr,
                        onValueChange = { multiplierStr = it },
                        label = { Text("ROI Multiplier (e.g. 1.5)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = YellowTertiary,
                            unfocusedBorderColor = Color(0xFF222D41),
                            focusedLabelColor = YellowTertiary,
                            unfocusedLabelColor = BodyGray,
                            focusedTextColor = TitleWhite,
                            unfocusedTextColor = TitleWhite
                        ),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "MACHINERY CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = BodyGray,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    categories.forEach { cat ->
                        val isSel = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) YellowTertiary else DeepSlateSurface)
                                .border(1.dp, if (isSel) YellowTertiary else Color(0xFF2F3748), RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) SlateBlack else TitleWhite,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "ACQUISITION STATUS",
                    style = MaterialTheme.typography.labelSmall,
                    color = BodyGray,
                    fontSize = 10.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    statuses.forEach { stat ->
                        val isSel = status == stat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(0xFF332A15) else DeepSlateSurface)
                                .border(
                                    1.dp,
                                    if (isSel) YellowTertiary else Color(0xFF2F3748),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { status = stat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = stat.uppercase(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSel) YellowTertiary else BodyGray,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Depreciation & Capacity Notes") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YellowTertiary,
                        unfocusedBorderColor = Color(0xFF222D41),
                        focusedLabelColor = YellowTertiary,
                        unfocusedLabelColor = BodyGray,
                        focusedTextColor = TitleWhite,
                        unfocusedTextColor = TitleWhite
                    ),
                    modifier = Modifier.fillMaxWidth().height(90.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCEL", color = BodyGray)
                    }

                    Button(
                        onClick = {
                            val amt = amountStr.toDoubleOrNull() ?: 0.0
                            val power = powerKwStr.toDoubleOrNull() ?: 0.0
                            val multi = multiplierStr.toDoubleOrNull() ?: 1.2
                            if (name.isNotEmpty() && amt > 0.0) {
                                onConfirm(
                                    name,
                                    amt,
                                    category,
                                    status,
                                    power,
                                    multi,
                                    notes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowTertiary,
                            contentColor = SlateBlack
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f).testTag("add_invest_submit")
                    ) {
                        Text("COMMIT CAPITAL ASSET", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillingView(
    viewModel: ShubhamGraphicsViewModel,
    transactions: List<FinancialTransaction>,
    currencyFormatter: NumberFormat
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    // Form states
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var selectedMedia by remember { mutableStateOf("Starflex Frontlit") }
    var widthFeetStr by remember { mutableStateOf("10") }
    var heightFeetStr by remember { mutableStateOf("12") }
    var rateSqFtStr by remember { mutableStateOf("12") }
    var quantityStr by remember { mutableStateOf("1") }

    // Addon extra choices
    var eyeletsPipingEnabled by remember { mutableStateOf(false) }
    var woodFramingEnabled by remember { mutableStateOf(false) }
    var urgentRushEnabled by remember { mutableStateOf(false) }

    // Media list details
    val mediaOptions = remember {
        listOf(
            MediaDetails("Starflex Frontlit", 12.0),
            MediaDetails("Starflex Backlit", 35.0),
            MediaDetails("Vinyl Sticker", 22.0),
            MediaDetails("Oneway Vision", 28.0),
            MediaDetails("Eco-Solvent Gloss", 40.0),
            MediaDetails("Retro-Reflective", 75.0)
        )
    }

    // Calculations
    val widthFeet = widthFeetStr.toDoubleOrNull() ?: 0.0
    val heightFeet = heightFeetStr.toDoubleOrNull() ?: 0.0
    val rateSqFt = rateSqFtStr.toDoubleOrNull() ?: 0.0
    val quantity = quantityStr.toIntOrNull() ?: 1

    val totalSqFt = widthFeet * heightFeet * quantity
    val rawPrintingCost = totalSqFt * rateSqFt

    val extraEyeletsCost = if (eyeletsPipingEnabled) 150.0 else 0.0
    val extraFramingCost = if (woodFramingEnabled) 1800.0 else 0.0
    val extraRushCost = if (urgentRushEnabled) 500.0 else 0.0
    val totalExtras = extraEyeletsCost + extraFramingCost + extraRushCost

    val grandTotal = rawPrintingCost + totalExtras

    // Dynamically draft the invoice text for WhatsApp / SMS dispatches
    val billMessage = remember(
        customerName, customerPhone, selectedMedia, widthFeet, heightFeet, rateSqFt, quantity,
        eyeletsPipingEnabled, woodFramingEnabled, urgentRushEnabled, grandTotal
    ) {
        val dateFormatted = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date())
        val nameToDisplay = customerName.ifEmpty { "Valued Client" }
        val sizeLines = if (widthFeet > 0 && heightFeet > 0) {
            "📏 Dimensions: ${widthFeet}x${heightFeet} ft (${(widthFeet * heightFeet).toInt()} Sq.Ft)\n" +
            "🔢 Quantity: $quantity (${totalSqFt.toInt()} Sq.Ft Total)\n" +
            "💵 Rate: ₹${rateSqFt}/Sq.Ft\n"
        } else {
            "🛒 Item: $selectedMedia services\n"
        }

        var extrasText = ""
        if (eyeletsPipingEnabled) extrasText += "⚡ Eyelets & Piping: ₹150\n"
        if (woodFramingEnabled) extrasText += "⚡ Wooden Board Frame: ₹1800\n"
        if (urgentRushEnabled) extrasText += "⚡ Express Rush: ₹500\n"

        """
        ⚡ SHUBHAM GRAPHICS INVOICE ⚡
        -----------------------------------------
        📅 Date: $dateFormatted
        👤 Client: $nameToDisplay
        📞 Phone: ${customerPhone.ifEmpty { "N/A" }}
        
        🖥️ Job: $selectedMedia
        $sizeLines${if (extrasText.isNotEmpty()) "\n➕ Extras:\n$extrasText" else ""}-----------------------------------------
        💰 TOTAL AMOUNT: ₹${currencyFormatter.format(grandTotal).replace("₹", "").trim()} 
        📝 Status: Paid & Ledger Committed
        -----------------------------------------
        Thank you for printing with Shubham Graphics!
        📍 Large Format Starflex, Vinyl & Banner Studio
        📱 For support, call us on our business lines.
        """.trimIndent()
    }

    // Historical invoice repository
    val pastInvoices = remember(transactions) {
        transactions.filter { !it.isExpense && it.title.contains("[Bill]") }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().testTag("billing_view_container"),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "BILL BUILDER & DISPATCH SUITE",
                style = MaterialTheme.typography.labelSmall,
                color = BodyGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 2.dp)
            )
        }

        // Customer Inputs Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                border = BorderStroke(1.dp, Color(0xFF222C3F)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "CUSTOMER METADATA",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = customerName,
                            onValueChange = { customerName = it },
                            label = { Text("Client Name") },
                            leadingIcon = { Icon(Icons.Rounded.Person, contentDescription = null, tint = BodyGray, modifier = Modifier.size(16.dp)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF283248),
                                focusedTextColor = TitleWhite,
                                unfocusedTextColor = TitleWhite,
                                focusedLabelColor = CyanPrimary,
                                unfocusedLabelColor = BodyGray
                            ),
                            modifier = Modifier.weight(1f).testTag("bill_cust_name"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = customerPhone,
                            onValueChange = { customerPhone = it },
                            label = { Text("Customer Mobile") },
                            placeholder = { Text("9876543210") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            leadingIcon = { Icon(Icons.Rounded.Phone, contentDescription = null, tint = BodyGray, modifier = Modifier.size(16.dp)) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF283248),
                                focusedTextColor = TitleWhite,
                                unfocusedTextColor = TitleWhite,
                                focusedLabelColor = CyanPrimary,
                                unfocusedLabelColor = BodyGray
                            ),
                            modifier = Modifier.weight(1f).testTag("bill_cust_phone"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "PRINT MEDIA SELECTION",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Media selections row of chips
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        mediaOptions.forEach { option ->
                            val isSel = selectedMedia == option.name
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) CyanPrimary else DeepSlateSurface)
                                    .border(1.dp, if (isSel) CyanPrimary else Color(0xFF2E3A52), RoundedCornerShape(8.dp))
                                    .clickable {
                                        selectedMedia = option.name
                                        rateSqFtStr = option.defaultRateSqFt.toInt().toString()
                                    }
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = option.name,
                                    color = if (isSel) SlateBlack else TitleWhite,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "DIMENSIONS & CALCULATOR (IN FEET)",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = widthFeetStr,
                            onValueChange = { widthFeetStr = it },
                            label = { Text("Width (Ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF283248),
                                focusedTextColor = TitleWhite,
                                unfocusedTextColor = TitleWhite,
                                focusedLabelColor = CyanPrimary,
                                unfocusedLabelColor = BodyGray
                            ),
                            modifier = Modifier.weight(1f).testTag("bill_width"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = heightFeetStr,
                            onValueChange = { heightFeetStr = it },
                            label = { Text("Height (Ft)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF283248),
                                focusedTextColor = TitleWhite,
                                unfocusedTextColor = TitleWhite,
                                focusedLabelColor = CyanPrimary,
                                unfocusedLabelColor = BodyGray
                            ),
                            modifier = Modifier.weight(1f).testTag("bill_height"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = rateSqFtStr,
                            onValueChange = { rateSqFtStr = it },
                            label = { Text("Rate (₹/SqFt)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF283248),
                                focusedTextColor = TitleWhite,
                                unfocusedTextColor = TitleWhite,
                                focusedLabelColor = CyanPrimary,
                                unfocusedLabelColor = BodyGray
                            ),
                            modifier = Modifier.weight(1.2f).testTag("bill_rate"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = quantityStr,
                            onValueChange = { quantityStr = it },
                            label = { Text("Qty Count") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CyanPrimary,
                                unfocusedBorderColor = Color(0xFF283248),
                                focusedTextColor = TitleWhite,
                                unfocusedTextColor = TitleWhite,
                                focusedLabelColor = CyanPrimary,
                                unfocusedLabelColor = BodyGray
                            ),
                            modifier = Modifier.weight(1f).testTag("bill_qty"),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        text = "VALUE-ADD PRINT FINISHING (OPTIONAL)",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    FinishingSelectionRow(
                        title = "Metal Eyelets & Border Piping",
                        priceText = "+ ₹150",
                        description = "Reinforced edge cords and grommets for wind ropes",
                        enabled = eyeletsPipingEnabled,
                        onCheckedChange = { eyeletsPipingEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FinishingSelectionRow(
                        title = "Wooden Frame Support Structure",
                        priceText = "+ ₹1,800",
                        description = "Custom wooden block assembly with frontlit wrapping",
                        enabled = woodFramingEnabled,
                        onCheckedChange = { woodFramingEnabled = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FinishingSelectionRow(
                        title = "Overnight Express Rush",
                        priceText = "+ ₹500",
                        description = "Direct print queue priority and fast plotter cutting",
                        enabled = urgentRushEnabled,
                        onCheckedChange = { urgentRushEnabled = it }
                    )
                }
            }
        }

        // Total calculated view
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = DeepSlateSurface),
                border = BorderStroke(1.dp, Color(0xFF1F2B40)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "CALCULATED BILL INVOICE",
                                style = MaterialTheme.typography.labelSmall,
                                color = BodyGray,
                                fontSize = 10.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Grand Total",
                                style = MaterialTheme.typography.titleMedium,
                                color = TitleWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = currencyFormatter.format(grandTotal),
                            style = MaterialTheme.typography.headlineMedium,
                            color = CyanPrimary,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = Color(0xFF2C394F), thickness = 0.5.dp)
                    Spacer(modifier = Modifier.height(12.dp))

                    if (widthFeet > 0 && heightFeet > 0) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Total Print Area",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BodyGray
                            )
                            Text(
                                text = "${totalSqFt.toInt()} Sq.Ft (${widthFeet}x${heightFeet} ft)",
                                style = MaterialTheme.typography.bodyMedium,
                                color = TitleWhite,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Base Layout Price",
                            style = MaterialTheme.typography.bodyMedium,
                            color = BodyGray
                        )
                        Text(
                            text = currencyFormatter.format(rawPrintingCost),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TitleWhite,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (totalExtras > 0) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Finishing & Rush Add-ons",
                                style = MaterialTheme.typography.bodyMedium,
                                color = BodyGray
                            )
                            Text(
                                text = "+ " + currencyFormatter.format(totalExtras),
                                style = MaterialTheme.typography.bodyMedium,
                                color = YellowTertiary,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // WhatsApp Send button
                        Button(
                            onClick = {
                                val amt = grandTotal
                                val name = customerName.ifEmpty { "Customer" }
                                val phoneClean = customerPhone.ifEmpty { "" }

                                if (amt > 0) {
                                    viewModel.insertTransaction(
                                        title = "[Bill] $name - $selectedMedia",
                                        amount = amt,
                                        isExpense = false,
                                        category = "Revenue",
                                        qtyOrSize = if (widthFeet > 0) "${widthFeet}x${heightFeet} ft" else "",
                                        notes = "Cust: $name | Mob: $phoneClean | Job: $selectedMedia"
                                    )
                                    sendWhatsAppBill(context, phoneClean, billMessage)
                                    Toast.makeText(context, "Bill added and sent via WhatsApp!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Configure sizes and pricing first.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF25D366),
                                contentColor = SlateBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1.2f).testTag("btn_send_whatsapp")
                        ) {
                            Text(
                                text = "WHATSAPP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // SMS Send button
                        Button(
                            onClick = {
                                val amt = grandTotal
                                val name = customerName.ifEmpty { "Customer" }
                                val phoneClean = customerPhone.ifEmpty { "" }

                                if (amt > 0) {
                                    viewModel.insertTransaction(
                                        title = "[Bill] $name - $selectedMedia",
                                        amount = amt,
                                        isExpense = false,
                                        category = "Revenue",
                                        qtyOrSize = if (widthFeet > 0) "${widthFeet}x${heightFeet} ft" else "",
                                        notes = "Cust: $name | Mob: $phoneClean | Job: $selectedMedia"
                                    )
                                    sendSMSBill(context, phoneClean, billMessage)
                                    Toast.makeText(context, "Bill logged and SMS requested!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Configure layout dimensions / pricing.", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CyanPrimary,
                                contentColor = SlateBlack
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("btn_send_sms")
                        ) {
                            Text(
                                text = "SMS TEXT",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }

                        // Share / Copy invoice plaintext
                        IconButton(
                            onClick = {
                                clipboardManager.setText(AnnotatedString(billMessage))
                                Toast.makeText(context, "Pre-flight copy successful!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .size(height = 42.dp, width = 50.dp)
                                .border(1.dp, Color(0xFF28334E), RoundedCornerShape(10.dp))
                                .background(Color.Transparent)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Share,
                                contentDescription = "Copy Bill Outline",
                                tint = TitleWhite,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }

        // Plaintext invoice draft representation
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftCardBg),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF1D263B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "PRE-FLIGHT INVOICE MESSAGE PREVIEW",
                        style = MaterialTheme.typography.labelSmall,
                        color = BodyGray,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SlateBlack)
                            .padding(14.dp)
                    ) {
                        Text(
                            text = billMessage,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = TitleWhite,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "PAST GRAPHIC BILL HISTORICAL LEDGER",
                style = MaterialTheme.typography.labelSmall,
                color = BodyGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 10.dp)
            )
        }

        if (pastInvoices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No billing history logged yet. Construct a bill above.",
                        style = MaterialTheme.typography.bodySmall,
                        color = BodyGray
                    )
                }
            }
        } else {
            items(pastInvoices) { inv ->
                HistoricInvoiceItemRow(
                    invoice = inv,
                    currencyFormatter = currencyFormatter,
                    onInstantWhatsApp = {
                        val notesClean = inv.notes
                        val extractedPhone = extractPhoneNumber(notesClean) ?: ""
                        val contentMsg = """
                        ⚡ SHUBHAM GRAPHICS PREVIOUS INVOICE ⚡
                        -----------------------------------------
                        📅 Date: ${SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault()).format(Date(inv.date))}
                        📝 Job: ${inv.title.replace("[Bill]", "").trim()}
                        📏 Dimensions: ${inv.qtyOrSize}
                        💵 Rate: ₹${inv.amount} Total
                        
                        This is a reprint receipt invoice of registered cash register records.
                        -----------------------------------------
                        Thank you for your business!
                        """.trimIndent()
                        sendWhatsAppBill(context, extractedPhone, contentMsg)
                    },
                    onInstantSMS = {
                        val notesClean = inv.notes
                        val extractedPhone = extractPhoneNumber(notesClean) ?: ""
                        val contentMsg = """
                        Shubham Graphics: Reference Invoice: `${inv.title.replace("[Bill]", "").trim()}`, Size: ${inv.qtyOrSize}, Amt: ₹${inv.amount}. Marked paid in ledger. Thanks!
                        """.trimIndent()
                        sendSMSBill(context, extractedPhone, contentMsg)
                    }
                )
            }
        }
    }
}

data class MediaDetails(val name: String, val defaultRateSqFt: Double)

@Composable
fun FinishingSelectionRow(
    title: String,
    priceText: String,
    description: String,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(DeepSlateSurface)
            .clickable { onCheckedChange(!enabled) }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = enabled,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = CyanPrimary,
                checkmarkColor = SlateBlack,
                uncheckedColor = Color(0xFF2C394F)
            ),
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TitleWhite,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )

                Text(
                    text = priceText,
                    style = MaterialTheme.typography.labelSmall,
                    color = YellowTertiary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = BodyGray,
                fontSize = 11.sp,
                lineHeight = 14.sp
            )
        }
    }
}

@Composable
fun HistoricInvoiceItemRow(
    invoice: FinancialTransaction,
    currencyFormatter: NumberFormat,
    onInstantWhatsApp: () -> Unit,
    onInstantSMS: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, Color(0xFF1E283A), RoundedCornerShape(12.dp))
            .background(SoftCardBg)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invoice.title.replace("[Bill]", "").trim(),
                style = MaterialTheme.typography.bodyMedium,
                color = TitleWhite,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val dt = remember(invoice.date) {
                    SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(invoice.date))
                }
                Text(
                    text = dt,
                    style = MaterialTheme.typography.labelSmall,
                    color = BodyGray,
                    fontSize = 10.sp
                )

                if (invoice.qtyOrSize.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2C394F))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = invoice.qtyOrSize,
                            style = MaterialTheme.typography.labelSmall,
                            color = CyanPrimary,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = currencyFormatter.format(invoice.amount),
                style = MaterialTheme.typography.bodyMedium,
                color = ProfitGreen,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                fontSize = 13.sp
            )

            IconButton(
                onClick = onInstantWhatsApp,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F321C))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Message,
                    contentDescription = "Share WA",
                    tint = Color(0xFF25D366),
                    modifier = Modifier.size(13.dp)
                )
            }

            IconButton(
                onClick = onInstantSMS,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F2632))
            ) {
                Icon(
                    imageVector = Icons.Rounded.Send,
                    contentDescription = "Share SMS",
                    tint = CyanPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

fun extractPhoneNumber(notes: String): String? {
    val regexText = """Mob:\s*([+]*\d{10,14})""".toRegex()
    val matchText = regexText.find(notes)
    if (matchText != null) {
        return matchText.groupValues[1]
    }
    val anyPhone = """\d{10}""".toRegex()
    val matchAny = anyPhone.find(notes)
    return matchAny?.value
}

fun sendWhatsAppBill(context: Context, customerPhone: String, message: String) {
    try {
        val formattedPhone = customerPhone.filter { it.isDigit() }
        val finalPhone = if (formattedPhone.length == 10) "91$formattedPhone" else formattedPhone
        
        val url = "https://api.whatsapp.com/send?phone=$finalPhone&text=${Uri.encode(message)}"
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(url)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "WhatsApp sharing failed. Text copied instead.", Toast.LENGTH_LONG).show()
    }
}

fun sendSMSBill(context: Context, customerPhone: String, message: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("sms:$customerPhone?body=${Uri.encode(message)}")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        try {
            val backupIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("smsto:$customerPhone")
                putExtra("sms_body", message)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(backupIntent)
        } catch (err: Exception) {
            Toast.makeText(context, "Could not launch SMS. Invoice copied.", Toast.LENGTH_SHORT).show()
        }
    }
}
