package com.jb.medineed.app.presentation.page.entry

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.domain.model.Medicine
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate

data class MedicineEntryUiState(
    val id: Long = 0,
    val name: String = "",
    val genericName: String = "",
    val batchNumber: String = "",
    val category: String = "",
    val customCategory: String = "",
    val quantity: String = "",
    val lowStockThreshold: String = "10",
    val manufacturingDate: LocalDate? = null,
    val expiryDate: LocalDate? = null,
    val pricePerUnit: String = "",
    val supplier: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val errorMessage: String? = null,
    val categories: List<String> = emptyList(),
    val isEditMode: Boolean = false
)

class MedicineEntryViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(MedicineEntryUiState())
    val uiState: StateFlow<MedicineEntryUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllCategories().collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    fun loadMedicine(id: Long) {
        viewModelScope.launch {
            repository.getMedicineById(id).filterNotNull().first().let { m ->
                _uiState.update {
                    it.copy(
                        id = m.id,
                        name = m.name,
                        genericName = m.genericName,
                        batchNumber = m.batchNumber,
                        category = m.category,
                        quantity = m.quantity.toString(),
                        lowStockThreshold = m.lowStockThreshold.toString(),
                        manufacturingDate = m.manufacturingDate,
                        expiryDate = m.expiryDate,
                        pricePerUnit = m.pricePerUnit.toString(),
                        supplier = m.supplier,
                        isEditMode = true
                    )
                }
            }
        }
    }

    fun onNameChange(v: String) = _uiState.update { it.copy(name = v, errorMessage = null) }
    fun onGenericNameChange(v: String) = _uiState.update { it.copy(genericName = v) }
    fun onBatchNumberChange(v: String) = _uiState.update { it.copy(batchNumber = v) }
    fun onCategoryChange(v: String) = _uiState.update { it.copy(category = v) }
    fun onCustomCategoryChange(v: String) = _uiState.update { it.copy(customCategory = v) }
    fun onQuantityChange(v: String) = _uiState.update { it.copy(quantity = v.filter { c -> c.isDigit() }) }
    fun onThresholdChange(v: String) = _uiState.update { it.copy(lowStockThreshold = v.filter { c -> c.isDigit() }) }
    fun onManufacturingDateChange(v: LocalDate) = _uiState.update { it.copy(manufacturingDate = v) }
    fun onExpiryDateChange(v: LocalDate) = _uiState.update { it.copy(expiryDate = v) }
    fun onPriceChange(v: String) = _uiState.update { it.copy(pricePerUnit = v) }
    fun onSupplierChange(v: String) = _uiState.update { it.copy(supplier = v) }
    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    fun saveMedicine() {
        val state = _uiState.value
        if (!validate(state)) return

        val effectiveCategory = if (state.category == "Other") state.customCategory else state.category

        val medicine = Medicine(
            id = state.id,
            name = state.name.trim(),
            genericName = state.genericName.trim(),
            batchNumber = state.batchNumber.trim(),
            category = effectiveCategory.trim(),
            quantity = state.quantity.toIntOrNull() ?: 0,
            lowStockThreshold = state.lowStockThreshold.toIntOrNull() ?: 10,
            manufacturingDate = state.manufacturingDate!!,
            expiryDate = state.expiryDate!!,
            pricePerUnit = state.pricePerUnit.toDoubleOrNull() ?: 0.0,
            supplier = state.supplier.trim()
        )

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                if (state.isEditMode) repository.updateMedicine(medicine)
                else repository.addMedicine(medicine)
                _uiState.update { it.copy(isSaved = true, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message, isLoading = false) }
            }
        }
    }

    private fun validate(state: MedicineEntryUiState): Boolean {
        return when {
            state.name.isBlank() -> { _uiState.update { it.copy(errorMessage = "Medicine name is required") }; false }
            state.batchNumber.isBlank() -> { _uiState.update { it.copy(errorMessage = "Batch number is required") }; false }
            state.category.isBlank() -> { _uiState.update { it.copy(errorMessage = "Category is required") }; false }
            state.category == "Other" && state.customCategory.isBlank() -> { _uiState.update { it.copy(errorMessage = "Please enter a custom category") }; false }
            state.quantity.isBlank() -> { _uiState.update { it.copy(errorMessage = "Quantity is required") }; false }
            state.manufacturingDate == null -> { _uiState.update { it.copy(errorMessage = "Manufacturing date is required") }; false }
            state.expiryDate == null -> { _uiState.update { it.copy(errorMessage = "Expiry date is required") }; false }
            state.expiryDate!!.isBefore(state.manufacturingDate) -> { _uiState.update { it.copy(errorMessage = "Expiry date must be after manufacturing date") }; false }
            else -> true
        }
    }
}