package com.jb.medineed.app.presentation.page.update

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLocale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.data.local.entity.TransactionType
import com.jb.medineed.app.domain.model.StockStatus
import com.jb.medineed.app.domain.model.StockTransaction
import com.jb.medineed.app.presentation.components.dateFormatter
import com.jb.medineed.app.presentation.theme.ErrorRed
import com.jb.medineed.app.presentation.theme.SuccessGreen
import com.jb.medineed.app.presentation.theme.WarningOrange
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockUpdateScreen(
    medicineId: Long,
    onNavigateBack: () -> Unit,
    viewModel: StockUpdateViewModel = koinViewModel(parameters = { parametersOf(medicineId) })
) {
    val uiState by viewModel.uiState.collectAsState()
    val medicine = uiState.medicine
    var activeTab by remember { mutableIntStateOf(0) } // 0=Quick, 1=Exact, 2=History

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                Column {
                    Text(medicine?.name ?: "Update Stock", fontWeight = FontWeight.Bold)
                    medicine?.let {
                        Text(
                            "Current: ${it.quantity} units",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, null) }
            }, colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
            )
        }) { padding ->
        if (uiState.isLoading || medicine == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Success banner
            item {
                AnimatedVisibility(visible = uiState.isSaved) {
                    Card(colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.15f))) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, null, tint = SuccessGreen)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Stock updated successfully!",
                                color = SuccessGreen,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Medicine info card
            item {
                Card(shape = RoundedCornerShape(12.dp)) {
                    Column(
                        Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(
                                    medicine.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    medicine.genericName,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
                                )
                            }
                            val statusColor = when (medicine.stockStatus) {
                                StockStatus.IN_STOCK -> SuccessGreen
                                StockStatus.LOW_STOCK -> WarningOrange
                                StockStatus.OUT_OF_STOCK -> ErrorRed
                            }
                            Surface(
                                color = statusColor.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Text(
                                    medicine.stockStatus.name.replace("_", " "),
                                    color = statusColor,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                )
                            }
                        }
                        HorizontalDivider()
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LabelValue("Category", medicine.category)
                            LabelValue("Batch", medicine.batchNumber)
                            LabelValue("Expires", medicine.expiryDate.format(dateFormatter))
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LabelValue("Price/Unit", "₹%.2f".format(medicine.pricePerUnit))
                            LabelValue("Low Stock At", "${medicine.lowStockThreshold} units")
                            LabelValue("Supplier", medicine.supplier.ifBlank { "—" })
                        }
                    }
                }
            }

            // Current stock indicator
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)) {
                    Row(
                        Modifier.fillMaxWidth().padding(20.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Inventory2,
                            null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.width(16.dp))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                medicine.quantity.toString(),
                                style = MaterialTheme.typography.displayMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                "units in stock",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Tabs
            item {
                TabRow(selectedTabIndex = activeTab) {
                    Tab(
                        selected = activeTab == 0,
                        onClick = { activeTab = 0 },
                        text = { Text("Quick") },
                        icon = { Icon(Icons.Default.Bolt, null, Modifier.size(16.dp)) })
                    Tab(
                        selected = activeTab == 1,
                        onClick = { activeTab = 1 },
                        text = { Text("Exact") },
                        icon = { Icon(Icons.Default.Edit, null, Modifier.size(16.dp)) })
                    Tab(
                        selected = activeTab == 2,
                        onClick = { activeTab = 2 },
                        text = { Text("History") },
                        icon = { Icon(Icons.Default.History, null, Modifier.size(16.dp)) })
                }
            }

            // Tab content
            when (activeTab) {
                0 -> {
                    // Quick adjust
                    item {
                        OutlinedTextField(
                            value = uiState.note,
                            onValueChange = viewModel::onNoteChange,
                            label = { Text("Note (optional)") },
                            leadingIcon = { Icon(Icons.Default.Notes, null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Text(
                            "Sell Units",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = ErrorRed
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 5, 10, 20).forEach { qty ->
                                OutlinedButton(
                                    onClick = { viewModel.sell(qty) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(
                                            ErrorRed.copy(
                                                0.5f
                                            )
                                        )
                                    )
                                ) {
                                    Text("-$qty", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                    item {
                        Text(
                            "Restock Units",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = SuccessGreen
                        )
                    }
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(1, 5, 10, 50).forEach { qty ->
                                Button(
                                    onClick = { viewModel.restock(qty) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                                ) {
                                    Text("+$qty", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Set exact quantity
                    item {
                        OutlinedTextField(
                            value = uiState.adjustmentAmount,
                            onValueChange = viewModel::onAdjustmentChange,
                            label = { Text("New Exact Quantity") },
                            leadingIcon = { Icon(Icons.Default.Numbers, null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = uiState.note,
                            onValueChange = viewModel::onNoteChange,
                            label = { Text("Note (optional)") },
                            leadingIcon = { Icon(Icons.Default.Notes, null) },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                    }
                    item {
                        Button(
                            onClick = {
                                uiState.adjustmentAmount.toIntOrNull()
                                    ?.let { viewModel.setExactQuantity(it) }
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            enabled = uiState.adjustmentAmount.isNotBlank()
                        ) {
                            Icon(Icons.Default.Tune, null)
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Set Quantity to ${uiState.adjustmentAmount.ifBlank { "?" }}",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                2 -> {
                    // Transaction history
                    if (uiState.transactions.isEmpty()) {
                        item {
                            Box(
                                Modifier.fillMaxWidth().padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "No transaction history yet.",
                                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                                )
                            }
                        }
                    } else {
                        items(uiState.transactions) { txn ->
                            TransactionRow(txn)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LabelValue(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
        )
        Text(value, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TransactionRow(txn: StockTransaction) {
    val (color, icon, prefix) = when (txn.transactionType) {
        TransactionType.SALE -> Triple(ErrorRed, Icons.Default.ShoppingCart, "-")
        TransactionType.RESTOCK -> Triple(SuccessGreen, Icons.Default.AddShoppingCart, "+")
        TransactionType.ADJUSTMENT -> Triple(WarningOrange, Icons.Default.Tune, "→")
        TransactionType.INITIAL -> Triple(
            MaterialTheme.colorScheme.primary,
            Icons.Default.PlayCircle,
            "+"
        )
    }
    val sdf = SimpleDateFormat("dd MMM yyyy, HH:mm", LocalLocale.current.platformLocale)

    Card(colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.07f))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    txn.transactionType.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
                if (txn.note.isNotBlank()) Text(
                    txn.note,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    sdf.format(Date(txn.timestamp)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "$prefix${abs(txn.quantityChange)}",
                    color = color,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    "${txn.quantityBefore} → ${txn.quantityAfter}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(0.5f)
                )
            }
        }
    }
}