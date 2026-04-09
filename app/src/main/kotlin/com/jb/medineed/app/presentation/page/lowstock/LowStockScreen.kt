package com.jb.medineed.app.presentation.page.lowstock

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.domain.model.ExpiryFilter
import com.jb.medineed.app.domain.model.SortOrder
import com.jb.medineed.app.presentation.components.EmptyState
import com.jb.medineed.app.presentation.components.MedicineCard
import com.jb.medineed.app.presentation.theme.WarningOrange
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LowStockScreen(
    onNavigateBack: () -> Unit,
    onMedicineClick: (Long) -> Unit,
    viewModel: LowStockViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Low & Expiring Stock", fontWeight = FontWeight.Bold)
                        Text("${uiState.medicines.size} medicines", style = MaterialTheme.typography.labelSmall)
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    Box {
                        IconButton(onClick = { showSortMenu = true }) {
                            Icon(Icons.AutoMirrored.Filled.Sort, null)
                        }
                        DropdownMenu(expanded = showSortMenu, onDismissRequest = { showSortMenu = false }) {
                            Text("Sort By", style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                                color = MaterialTheme.colorScheme.primary)
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
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Info banner
            item {
                Card(colors = CardDefaults.cardColors(containerColor = WarningOrange.copy(alpha = 0.1f))) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, null, tint = WarningOrange)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Shows medicines with low stock quantity AND medicines expiring within the selected time window.",
                            style = MaterialTheme.typography.bodySmall,
                            color = WarningOrange
                        )
                    }
                }
            }

            // Toggle: show expiry only
            item {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Show expiring stock only", Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                    Switch(
                        checked = uiState.showExpiryOnly,
                        onCheckedChange = viewModel::onShowExpiryOnlyChange
                    )
                }
            }

            // Expiry filter chips
            item {
                Text("Expiry Window", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(ExpiryFilter.entries) { filter ->
                        FilterChip(
                            selected = uiState.expiryFilter == filter,
                            onClick = { viewModel.onExpiryFilterChange(filter) },
                            label = { Text(filter.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = WarningOrange,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.medicines.isEmpty()) {
                item {
                    EmptyState(
                        message = "No low stock or expiring medicines found.\nYour inventory looks healthy!",
                        icon = Icons.Default.CheckCircle
                    )
                }
            } else {
                items(uiState.medicines, key = { it.id }) { medicine ->
                    MedicineCard(
                        medicine = medicine,
                        onClick = { onMedicineClick(medicine.id) }
                    )
                }
            }
        }
    }
}