package com.jb.medineed.app.presentation.page.stock

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
    onMedicineClick: (Long) -> Unit,
    onEditClick: (Long) -> Unit,
    viewModel: StockListViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<Medicine?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("MediStock", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.Default.Sort, "Sort")
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            Text(
                                "Sort By",
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                            SortOrder.entries.forEach { sort ->
                                DropdownMenuItem(
                                    text = { Text(sort.label) },
                                    onClick = { viewModel.onSortOrderChange(sort); showSortMenu = false },
                                    leadingIcon = {
                                        if (uiState.sortOrder == sort)
                                            Icon(Icons.Default.Check, null, tint = MaterialTheme.colorScheme.primary)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMedicine,
                modifier = Modifier.navigationBarsPadding(),
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, null)
            }
        }
    ) { padding ->
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
                        title = "Total",
                        value = uiState.totalCount.toString(),
                        icon = Icons.Default.Inventory2,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Low Stock",
                        value = uiState.lowStockCount.toString(),
                        icon = Icons.Default.TrendingDown,
                        color = WarningOrange,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "Out of Stock",
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
                            Icon(Icons.Default.AccountBalance, null, tint = SuccessGreen, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Total Inventory Value", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
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
                    placeholder = { Text("Search by name, generic name, category...") },
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
                    shape = MaterialTheme.shapes.medium
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
                                label = { Text("All") }
                            )
                        }
                        items(uiState.categories) { cat ->
                            FilterChip(
                                selected = uiState.selectedCategory == cat,
                                onClick = { viewModel.onCategoryChange(if (uiState.selectedCategory == cat) "" else cat) },
                                label = { Text(cat) }
                            )
                        }
                    }
                }
            }

            // Medicine list
            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.medicines.isEmpty()) {
                item {
                    Box(Modifier.fillParentMaxSize()) {
                        EmptyState(
                            message = if (uiState.searchQuery.isNotBlank()) "No medicines found for \"${uiState.searchQuery}\""
                            else "No medicines added yet.\nTap + to add your first medicine.",
                            icon = Icons.Default.MedicalServices
                        )
                    }
                }
            } else {
                items(uiState.medicines, key = { it.id }) { medicine ->
                    MedicineCard(
                        medicine = medicine,
                        onClick = { onMedicineClick(medicine.id) },
                        onEditClick = { onEditClick(medicine.id) }
                    )
                }
                item { Spacer(Modifier.height(72.dp)) } // FAB clearance
            }
        }
    }

    // Delete confirmation dialog
    showDeleteDialog?.let { medicine ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Medicine") },
            text = { Text("Are you sure you want to delete \"${medicine.name}\"? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            viewModel.deleteMedicine(medicine)
                        }
                        showDeleteDialog = null
                    }
                ) { Text("Delete", color = ErrorRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) { Text("Cancel") }
            }
        )
    }
}
