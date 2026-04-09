package com.jb.medineed.app.presentation.page.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.data.local.entity.TransactionType
import com.jb.medineed.app.presentation.components.EmptyState
import com.jb.medineed.app.presentation.components.StatCard
import com.jb.medineed.app.presentation.theme.SuccessGreen
import com.jb.medineed.app.presentation.theme.WarningOrange
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.platform.LocalLocale
import com.jb.medineed.app.presentation.theme.ErrorRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: ReportsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Share PDF when ready
    LaunchedEffect(uiState.pdfUri) {
        uiState.pdfUri?.let { uri ->
            viewModel.sharePdf(uri)
            viewModel.clearPdfUri()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Analytics", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    if (uiState.isGeneratingPdf) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(end = 8.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.generateAndSharePdf() }) {
                            Icon(Icons.Default.PictureAsPdf, "Export PDF")
                        }
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Period filter
            item {
                Text("Report Period", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ReportPeriod.entries) { period ->
                        FilterChip(
                            selected = uiState.period == period,
                            onClick = { viewModel.onPeriodChange(period) },
                            label = { Text(period.label) }
                        )
                    }
                }
            }

            // Stats
            item {
                Text("Overview", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Total Meds", uiState.totalMedicines.toString(),
                        Icons.Default.Inventory2, MaterialTheme.colorScheme.primary, Modifier.weight(1f))
                    StatCard("Units Sold", uiState.totalSold.toString(),
                        Icons.Default.ShoppingCart, SuccessGreen, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatCard("Revenue", "₹%.0f".format(uiState.totalSales),
                        Icons.Default.CurrencyRupee, SuccessGreen, Modifier.weight(1f))
                    StatCard("Out of Stock", uiState.outOfStockCount.toString(),
                        Icons.Default.RemoveShoppingCart, ErrorRed, Modifier.weight(1f))
                }
                Spacer(Modifier.height(8.dp))
                StatCard("Low Stock", uiState.lowStockCount.toString(),
                    Icons.Default.TrendingDown, WarningOrange, Modifier.fillMaxWidth())
            }

            // Export PDF button
            item {
                Button(
                    onClick = { viewModel.generateAndSharePdf() },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = !uiState.isGeneratingPdf
                ) {
                    if (uiState.isGeneratingPdf) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                        Spacer(Modifier.width(8.dp))
                        Text("Generating PDF…")
                    } else {
                        Icon(Icons.Default.PictureAsPdf, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Export & Share PDF Report", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Transaction history section header
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Transaction History", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                    Text("${uiState.transactions.size} records", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.5f))
                }
                HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
            }

            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.transactions.isEmpty()) {
                item {
                    EmptyState(
                        message = "No transactions found\nfor ${uiState.period.label.lowercase()}.",
                        icon = Icons.Default.ReceiptLong
                    )
                }
            } else {
                items(uiState.transactions.take(50)) { txn ->
                    val allMeds = uiState.allMedicines.associateBy { it.id }
                    val medicineName = allMeds[txn.medicineId]?.name ?: "Unknown"
                    val sdf = SimpleDateFormat("dd MMM yy  HH:mm", LocalLocale.current.platformLocale)

                    val (color, icon) = when (txn.transactionType) {
                        TransactionType.SALE       -> SuccessGreen to Icons.Default.ShoppingCart
                        TransactionType.RESTOCK    -> MaterialTheme.colorScheme.primary to Icons.Default.AddShoppingCart
                        TransactionType.ADJUSTMENT -> WarningOrange to Icons.Default.Tune
                        TransactionType.INITIAL    -> MaterialTheme.colorScheme.primary to Icons.Default.PlayCircle
                    }

                    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.07f))) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(medicineName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                Text(txn.transactionType.name.lowercase().replaceFirstChar { it.uppercase() },
                                    style = MaterialTheme.typography.labelSmall, color = color)
                                Text(sdf.format(Date(txn.timestamp)), style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                val change = txn.quantityChange
                                Text(
                                    if (change >= 0) "+$change" else "$change",
                                    color = if (change >= 0) color else ErrorRed,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Text("${txn.quantityBefore}→${txn.quantityAfter}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
                            }
                        }
                    }
                }

                if (uiState.transactions.size > 50) {
                    item {
                        Text(
                            "Showing 50 of ${uiState.transactions.size} transactions. Export PDF to see all.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(0.5f),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Error
            uiState.errorMessage?.let { err ->
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Error, null, tint = MaterialTheme.colorScheme.error)
                            Spacer(Modifier.width(8.dp))
                            Text(err, Modifier.weight(1f), color = MaterialTheme.colorScheme.onErrorContainer)
                            IconButton(onClick = viewModel::clearError) {
                                Icon(Icons.Default.Close, null)
                            }
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}