package com.jb.medineed.app.presentation.page.stock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.R
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.SortOrder
import com.jb.medineed.app.presentation.components.EmptyState
import com.jb.medineed.app.presentation.components.MedicineCard
import com.jb.medineed.app.presentation.components.StatCard
import com.jb.medineed.app.presentation.theme.ErrorRed
import com.jb.medineed.app.presentation.theme.SuccessGreen
import com.jb.medineed.app.presentation.theme.WarningOrange
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StockListScreen(
    onAddMedicine: () -> Unit,
    onSettings: () -> Unit,
    onMedicineClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: StockListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Medicine?>(null) }

    Scaffold(topBar = {
        TopAppBar(title = {
            Text(stringResource(R.string.app_name), fontWeight = FontWeight.Bold)
        }, actions = {
            Box {
                Row {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Filled.Sort, "Sort")
                    }
                    IconButton(onClick = onSettings) {
                        Icon(Icons.Filled.Settings, "Settings")
                    }
                }
                DropdownMenu(
                    expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                    Text(
                        stringResource(R.string.sort_by),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    SortOrder.entries.forEach { sort ->
                        DropdownMenuItem(text = { Text(sort.label) }, onClick = {
                            viewModel.onSortOrderChange(sort); showSortMenu = false
                        }, leadingIcon = {
                            if (uiState.sortOrder == sort) Icon(
                                Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary
                            )
                        })
                    }
                }
            }
        })
    }, floatingActionButton = {
        FloatingActionButton(
            onClick = onAddMedicine,
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
        ) {
            Icon(Icons.Default.Add, null)
        }
    }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stats row
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = stringResource(R.string.total),
                        value = uiState.totalCount.toString(),
                        icon = Icons.Default.Inventory2,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(R.string.low_stock),
                        value = uiState.lowStockCount.toString(),
                        icon = Icons.AutoMirrored.Filled.TrendingDown,
                        color = WarningOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = stringResource(R.string.out_of_stock),
                        value = uiState.outOfStockCount.toString(),
                        icon = Icons.Default.RemoveShoppingCart,
                        color = ErrorRed,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Inventory value
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SuccessGreen.copy(alpha = 0.1f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AccountBalance,
                                null,
                                tint = SuccessGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                stringResource(R.string.total_inventory_value),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            "₹%.2f".format(uiState.totalInventoryValue),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }

            // Search bar
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = viewModel::onSearchQueryChange,
                    placeholder = { Text(stringResource(R.string.search)) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, null)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = MaterialTheme.shapes.large
                )
            }

            // Category filter chips
            if (uiState.categories.isNotEmpty()) {
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = uiState.selectedCategory.isEmpty(),
                                onClick = { viewModel.onCategoryChange("") },
                                label = { Text(stringResource(R.string.all)) })
                        }
                        items(uiState.categories) { cat ->
                            FilterChip(
                                selected = uiState.selectedCategory == cat,
                                onClick = { viewModel.onCategoryChange(if (uiState.selectedCategory == cat) "" else cat) },
                                label = { Text(cat) })
                        }
                    }
                }
            }

            // Medicine list
            if (uiState.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp), contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.medicines.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize()) {
                        EmptyState(
                            message = if (uiState.searchQuery.isNotBlank()) stringResource(
                                R.string.no_medicine_found_with_query, uiState.searchQuery
                            )
                            else stringResource(R.string.no_medicine_empty),
                            icon = Icons.Default.MedicalServices
                        )
                    }
                }
            } else {
                items(uiState.medicines, key = { it.id }) { medicine ->
                    MedicineCard(
                        medicine = medicine,
                        onClick = { onMedicineClick(medicine.id) },
                        onEditClick = { onEditClick(medicine.id) })
                }
                item { Spacer(Modifier.height(72.dp)) } // FAB clearance
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { medicine ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text(stringResource(R.string.delete_medicine_title)) },
            text = {
                Text(
                    stringResource(
                        R.string.delete_medicine_message, medicine.name
                    )
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteMedicine(medicine)
                        }
                        showDeleteDialog = null
                    }) {
                    Text(
                        stringResource(R.string.delete), color = ErrorRed
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = null
                }) { Text(stringResource(R.string.cancel)) }
            })
    }
}