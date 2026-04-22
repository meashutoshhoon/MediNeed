package com.jb.medineed.app.presentation.page.stock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jb.medineed.app.data.repository.MedicineRepository
import com.jb.medineed.app.domain.model.Medicine
import com.jb.medineed.app.domain.model.SortOrder
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class StockListUiState(
    val medicines: List<Medicine> = emptyList(),
    val totalCount: Int = 0,
    val outOfStockCount: Int = 0,
    val lowStockCount: Int = 0,
    val totalInventoryValue: Double = 0.0,
    val searchQuery: String = "",
    val sortOrder: SortOrder = SortOrder.NAME_ASC,
    val selectedCategory: String = "",
    val categories: List<String> = emptyList(),
    val isLoading: Boolean = true
)

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class StockListViewModel(private val repository: MedicineRepository) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _sortOrder = MutableStateFlow(SortOrder.NAME_ASC)
    private val _selectedCategory = MutableStateFlow("")

    private val _uiState = MutableStateFlow(StockListUiState())
    val uiState: StateFlow<StockListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                _searchQuery.debounce(300).flatMapLatest { q ->
                    if (q.isBlank()) repository.getAllMedicines()
                    else repository.searchMedicines(q)
                },
                _sortOrder,
                _selectedCategory
            ) { medicines, sort, category ->
                val filtered = if (category.isBlank()) medicines
                else medicines.filter { it.category == category }

                filtered.sortedWith(
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

        viewModelScope.launch {
            repository.getTotalCount().collect { count ->
                _uiState.update { it.copy(totalCount = count) }
            }
        }
        viewModelScope.launch {
            repository.getOutOfStockCount().collect { count ->
                _uiState.update { it.copy(outOfStockCount = count) }
            }
        }
        viewModelScope.launch {
            repository.getLowStockCount().collect { count ->
                _uiState.update { it.copy(lowStockCount = count) }
            }
        }
        viewModelScope.launch {
            repository.getTotalInventoryValue().collect { value ->
                _uiState.update { it.copy(totalInventoryValue = value ?: 0.0) }
            }
        }
        viewModelScope.launch {
            repository.getAllCategories().collect { cats ->
                _uiState.update { it.copy(categories = cats) }
            }
        }
    }

    fun onSearchQueryChange(q: String) {
        _searchQuery.value = q
        _uiState.update { it.copy(searchQuery = q) }
    }

    fun onSortOrderChange(sort: SortOrder) {
        _sortOrder.value = sort
        _uiState.update { it.copy(sortOrder = sort) }
    }

    fun onCategoryChange(cat: String) {
        _selectedCategory.value = cat
        _uiState.update { it.copy(selectedCategory = cat) }
    }

    suspend fun deleteMedicine(medicine: Medicine) = repository.deleteMedicine(medicine)
}
