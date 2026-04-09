package com.jb.medineed.app.presentation.page.update

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jb.medineed.app.data.local.entity.TransactionType
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.StockTransaction
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StockUpdateUiState(
    val medicine: Medicine? = null,
    val transactions: List<StockTransaction> = emptyList(),
    val adjustmentAmount: String = "",
    val note: String = "",
    val isLoading: Boolean = true,
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)

class StockUpdateViewModel(
    private val repository: MedicineRepository,
    private val medicineId: Long
) : ViewModel() {

    private val _uiState = MutableStateFlow(StockUpdateUiState())
    val uiState: StateFlow<StockUpdateUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getMedicineById(medicineId).collect { medicine ->
                _uiState.update { it.copy(medicine = medicine, isLoading = false) }
            }
        }
        viewModelScope.launch {
            repository.getAllTransactions()
                .map { list -> list.filter { it.medicineId == medicineId }.take(20) }
                .collect { txns -> _uiState.update { it.copy(transactions = txns) } }
        }
    }

    fun onAdjustmentChange(v: String) {
        _uiState.update { it.copy(adjustmentAmount = v.filter { c -> c.isDigit() }) }
    }

    fun onNoteChange(v: String) {
        _uiState.update { it.copy(note = v) }
    }

    fun sell(units: Int) {
        val current = _uiState.value.medicine?.quantity ?: return
        val newQty = (current - units).coerceAtLeast(0)
        updateStock(newQty, TransactionType.SALE, _uiState.value.note.ifBlank { "Sold $units unit(s)" })
    }

    fun restock(units: Int) {
        val current = _uiState.value.medicine?.quantity ?: return
        val newQty = current + units
        updateStock(newQty, TransactionType.RESTOCK, _uiState.value.note.ifBlank { "Restocked $units unit(s)" })
    }

    fun setExactQuantity(qty: Int) {
        updateStock(qty, TransactionType.ADJUSTMENT, _uiState.value.note.ifBlank { "Manual adjustment to $qty units" })
    }

    private fun updateStock(newQty: Int, type: TransactionType, note: String) {
        viewModelScope.launch {
            try {
                repository.updateStock(medicineId, newQty, type, note)
                _uiState.update { it.copy(isSaved = true, adjustmentAmount = "", note = "") }
                // Reset isSaved after brief delay so the confirmation banner shows
                kotlinx.coroutines.delay(2000)
                _uiState.update { it.copy(isSaved = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = e.message) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(errorMessage = null) }
}