package com.jb.medineed.app.ui.page.lowstock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.domain.model.ExpiryFilter
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class LowStockUiState(
    val medicines: List<Medicine> = emptyList(),
    val expiryFilter: ExpiryFilter = ExpiryFilter.ONE_MONTH,
    val sortOrder: SortOrder = SortOrder.QUANTITY_ASC,
    val isLoading: Boolean = true,
    val showExpiryOnly: Boolean = false
)

@OptIn(ExperimentalCoroutinesApi::class)
class LowStockViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _expiryFilter = MutableStateFlow(ExpiryFilter.ONE_MONTH)
    private val _sortOrder = MutableStateFlow(SortOrder.QUANTITY_ASC)
    private val _showExpiryOnly = MutableStateFlow(false)

    private val _uiState = MutableStateFlow(LowStockUiState())
    val uiState: StateFlow<LowStockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _expiryFilter.flatMapLatest { filter ->
                    repository.getMedicinesExpiringWithin(filter.days)
                },
                repository.getLowStockMedicines(),
                _sortOrder,
                _showExpiryOnly
            ) { expiring, lowStock, sort, expiryOnly ->
                val base = if (expiryOnly) expiring
                else (lowStock + expiring).distinctBy { it.id }

                base.sortedWith(
                    when (sort) {
                        SortOrder.NAME_ASC      -> compareBy { it.name }
                        SortOrder.NAME_DESC     -> compareByDescending { it.name }
                        SortOrder.QUANTITY_ASC  -> compareBy { it.quantity }
                        SortOrder.QUANTITY_DESC -> compareByDescending { it.quantity }
                        SortOrder.EXPIRY_ASC    -> compareBy { it.expiryDate }
                        SortOrder.EXPIRY_DESC   -> compareByDescending { it.expiryDate }
                    }
                )
            }.collect { sorted ->
                _uiState.update { it.copy(medicines = sorted, isLoading = false) }
            }
        }
    }

    fun onExpiryFilterChange(f: ExpiryFilter) {
        _expiryFilter.value = f
        _uiState.update { it.copy(expiryFilter = f) }
    }

    fun onSortOrderChange(s: SortOrder) {
        _sortOrder.value = s
        _uiState.update { it.copy(sortOrder = s) }
    }

    fun onShowExpiryOnlyChange(v: Boolean) {
        _showExpiryOnly.value = v
        _uiState.update { it.copy(showExpiryOnly = v) }
    }
}