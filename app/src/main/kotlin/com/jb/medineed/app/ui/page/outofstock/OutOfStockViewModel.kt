package com.jb.medineed.app.ui.page.outofstock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.SortOrder
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class OutOfStockUiState(
    val medicines: List<Medicine> = emptyList(),
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val isLoading: Boolean = true
)

class OutOfStockViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _sortOrder = MutableStateFlow(SortOrder.NAME_ASC)
    private val _uiState = MutableStateFlow(OutOfStockUiState())
    val uiState: StateFlow<OutOfStockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(repository.getOutOfStockMedicines(), _sortOrder) { medicines, sort ->
                medicines.sortedWith(
                    when (sort) {
                        SortOrder.NAME_ASC      -> compareBy { it.name }
                        SortOrder.NAME_DESC     -> compareByDescending { it.name }
                        SortOrder.EXPIRY_ASC    -> compareBy { it.expiryDate }
                        SortOrder.EXPIRY_DESC   -> compareByDescending { it.expiryDate }
                        else -> compareBy { it.name }
                    }
                )
            }.collect { sorted ->
                _uiState.update { it.copy(medicines = sorted, isLoading = false) }
            }
        }
    }

    fun onSortOrderChange(s: SortOrder) {
        _sortOrder.value = s
        _uiState.update { it.copy(sortOrder = s) }
    }
}