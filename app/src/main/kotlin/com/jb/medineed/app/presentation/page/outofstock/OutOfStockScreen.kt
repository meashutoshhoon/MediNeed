package com.jb.medineed.app.presentation.page.outofstock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.automirrored.rounded.Sort
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.domain.model.SortOrder
import com.jb.medineed.app.presentation.components.EmptyState
import com.jb.medineed.app.presentation.components.MedicineCard
import com.jb.medineed.app.presentation.theme.ErrorRed
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OutOfStockScreen(
    onNavigateBack: () -> Unit,
    onMedicineClick: (Long) -> Unit,
    viewModel: OutOfStockViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showSortMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Column {
                    Text("Out of Stock", fontWeight = FontWeight.Bold)
                    Text(
                        "${uiState.medicines.size} medicines to order",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }, navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        Icons.AutoMirrored.Rounded.ArrowBack,
                        null
                    )
                }
            }, actions = {
                Box {
                    IconButton(onClick = { showSortMenu = true }) {
                        Icon(Icons.AutoMirrored.Rounded.Sort, null)
                    }
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }) {
                        listOf(
                            SortOrder.NAME_ASC,
                            SortOrder.NAME_DESC,
                            SortOrder.EXPIRY_ASC,
                            SortOrder.EXPIRY_DESC
                        ).forEach { sort ->
                            DropdownMenuItem(
                                text = { Text(sort.label) },
                                onClick = {
                                    viewModel.onSortOrderChange(sort); showSortMenu = false
                                },
                                leadingIcon = {
                                    if (uiState.sortOrder == sort) Icon(
                                        Icons.Default.Check,
                                        null,
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                })
                        }
                    }
                }
            })
        }) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (uiState.medicines.isNotEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = ErrorRed.copy(alpha = 0.1f))) {
                        Row(
                            Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.ShoppingCart, null, tint = ErrorRed)
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(
                                    "Order Required",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ErrorRed
                                )
                                Text(
                                    "${uiState.medicines.size} medicines are completely out of stock and need to be ordered.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ErrorRed
                                )
                            }
                        }
                    }
                }
            }

            if (uiState.isLoading) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            } else if (uiState.medicines.isEmpty()) {
                item {
                    EmptyState(
                        message = "No out-of-stock medicines!\nAll medicines are available.",
                        icon = Icons.Default.CheckCircle
                    )
                }
            } else {
                items(uiState.medicines, key = { it.id }) { medicine ->
                    MedicineCard(
                        medicine = medicine, onClick = { onMedicineClick(medicine.id) })
                }
            }
        }
    }
}