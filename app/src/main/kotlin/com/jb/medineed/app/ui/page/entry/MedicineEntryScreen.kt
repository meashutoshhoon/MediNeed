package com.jb.medineed.app.ui.page.entry

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.jb.medineed.app.ui.components.dateFormatter
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

val MEDICINE_CATEGORIES = listOf(
    "Analgesics", "Antibiotics", "Antacids", "Antihistamines",
    "Antidiabetics", "Antihypertensives", "Antivirals", "Vitamins & Supplements",
    "Dermatologicals", "Eye & Ear Drops", "Cough & Cold", "Cardiovascular",
    "Gastrointestinal", "Neurological", "Respiratory", "Other"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineEntryScreen(
    medicineId: Long = 0L,
    onNavigateBack: () -> Unit,
    viewModel: MedicineEntryViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(medicineId) {
        if (medicineId != 0L) viewModel.loadMedicine(medicineId)
    }

    LaunchedEffect(uiState.isSaved) {
        if (uiState.isSaved) onNavigateBack()
    }

    var showMfgDatePicker by remember { mutableStateOf(false) }
    var showExpDatePicker by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (uiState.isEditMode) "Edit Medicine" else "Add Medicine",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Error banner
            AnimatedVisibility(visible = uiState.errorMessage != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Error, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            uiState.errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = viewModel::clearError) {
                            Icon(Icons.Default.Close, contentDescription = "Dismiss")
                        }
                    }
                }
            }

            SectionHeader(title = "Basic Information", icon = Icons.Default.MedicalServices)

            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text("Medicine Name *") },
                leadingIcon = { Icon(Icons.Default.Medication, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.genericName,
                onValueChange = viewModel::onGenericNameChange,
                label = { Text("Generic Name") },
                leadingIcon = { Icon(Icons.Default.Science, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.batchNumber,
                onValueChange = viewModel::onBatchNumberChange,
                label = { Text("Batch Number *") },
                leadingIcon = { Icon(Icons.Default.Tag, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Category dropdown
            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }
            ) {
                OutlinedTextField(
                    value = uiState.category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category *") },
                    leadingIcon = { Icon(Icons.Default.Category, null) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }
                ) {
                    MEDICINE_CATEGORIES.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.onCategoryChange(cat)
                                categoryExpanded = false
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = uiState.category == "Other") {
                OutlinedTextField(
                    value = uiState.customCategory,
                    onValueChange = viewModel::onCustomCategoryChange,
                    label = { Text("Custom Category *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            SectionHeader(title = "Stock & Pricing", icon = Icons.Default.Inventory)

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = uiState.quantity,
                    onValueChange = viewModel::onQuantityChange,
                    label = { Text("Quantity *") },
                    leadingIcon = { Icon(Icons.Default.Numbers, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = uiState.lowStockThreshold,
                    onValueChange = viewModel::onThresholdChange,
                    label = { Text("Low Stock Alert At") },
                    leadingIcon = { Icon(Icons.Default.NotificationsActive, null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }

            OutlinedTextField(
                value = uiState.pricePerUnit,
                onValueChange = viewModel::onPriceChange,
                label = { Text("Price per Unit (₹)") },
                leadingIcon = { Icon(Icons.Default.CurrencyRupee, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = uiState.supplier,
                onValueChange = viewModel::onSupplierChange,
                label = { Text("Supplier / Manufacturer") },
                leadingIcon = { Icon(Icons.Default.Business, null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            SectionHeader(title = "Dates", icon = Icons.Default.DateRange)

            // Manufacturing Date
            OutlinedTextField(
                value = uiState.manufacturingDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Manufacturing Date *") },
                leadingIcon = { Icon(Icons.Default.Factory, null) },
                trailingIcon = {
                    IconButton(onClick = { showMfgDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Expiry Date
            OutlinedTextField(
                value = uiState.expiryDate?.format(dateFormatter) ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Expiry Date *") },
                leadingIcon = { Icon(Icons.Default.EventBusy, null) },
                trailingIcon = {
                    IconButton(onClick = { showExpDatePicker = true }) {
                        Icon(Icons.Default.CalendarMonth, null)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::saveMedicine,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text(if (uiState.isEditMode) "Update Medicine" else "Save Medicine", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }

    // Date pickers
    if (showMfgDatePicker) {
        MediDatePickerDialog(
            title = "Select Manufacturing Date",
            onDateSelected = { viewModel.onManufacturingDateChange(it) },
            onDismiss = { showMfgDatePicker = false }
        )
    }
    if (showExpDatePicker) {
        MediDatePickerDialog(
            title = "Select Expiry Date",
            onDateSelected = { viewModel.onExpiryDateChange(it) },
            onDismiss = { showExpDatePicker = false }
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(6.dp))
        Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.primary)
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediDatePickerDialog(
    title: String,
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                datePickerState.selectedDateMillis?.let { millis ->
                    val date = java.time.Instant.ofEpochMilli(millis)
                        .atZone(java.time.ZoneId.systemDefault()).toLocalDate()
                    onDateSelected(date)
                }
                onDismiss()
            }) { Text("OK") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    ) {
        DatePicker(state = datePickerState, title = { Text(title, modifier = Modifier.padding(16.dp)) })
    }
}