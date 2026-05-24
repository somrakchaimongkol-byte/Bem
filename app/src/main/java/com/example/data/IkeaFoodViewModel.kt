package com.example.data

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IkeaFoodViewModel(private val repository: IkeaFoodRepository) : ViewModel() {

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    // Products and active filters
    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val locations: StateFlow<List<LocationEntity>> = repository.allLocations
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedLocationFilter = MutableStateFlow<String?>(null)
    val selectedLocationFilter = _selectedLocationFilter.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter = _selectedCategoryFilter.asStateFlow()

    private val _currentView = MutableStateFlow("dashboard") // dashboard, counting, inventory, locations
    val currentView = _currentView.asStateFlow()

    // Filtered list of products for the counting list
    val filteredProducts: StateFlow<List<ProductEntity>> = combine(
        products, _searchQuery, _selectedLocationFilter, _selectedCategoryFilter
    ) { prodList, query, loc, cat ->
        prodList.filter { p ->
            val matchesQuery = p.name.contains(query, ignoreCase = true) || p.sku.contains(query, ignoreCase = true)
            val matchesLoc = loc == null || p.location == loc
            val matchesCat = cat == null || p.category == cat
            matchesQuery && matchesLoc && matchesCat
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun changeView(view: String) {
        _currentView.value = view
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun selectLocationFilter(loc: String?) {
        _selectedLocationFilter.value = loc
    }

    fun selectCategoryFilter(cat: String?) {
        _selectedCategoryFilter.value = cat
    }

    fun incrementPhysicalCount(product: ProductEntity) {
        viewModelScope.launch {
            repository.updatePhysicalCount(product.sku, product.physicalCount + 1)
        }
    }

    fun decrementPhysicalCount(product: ProductEntity) {
        if (product.physicalCount > 0) {
            viewModelScope.launch {
                repository.updatePhysicalCount(product.sku, product.physicalCount - 1)
            }
        }
    }

    fun setPhysicalCountManual(product: ProductEntity, count: Int) {
        val safeCount = if (count < 0) 0 else count
        viewModelScope.launch {
            repository.updatePhysicalCount(product.sku, safeCount)
        }
    }

    fun commitAllCounts() {
        viewModelScope.launch {
            repository.commitAllCounts()
        }
    }

    fun addProduct(sku: String, name: String, category: String, location: String, unit: String, systemStock: Int) {
        viewModelScope.launch {
            repository.insertProduct(
                ProductEntity(
                    sku = sku,
                    name = name,
                    category = category,
                    location = location,
                    unit = unit,
                    systemStock = systemStock,
                    physicalCount = systemStock // Initialize physical count as system stock
                )
            )
        }
    }

    fun deleteProduct(sku: String) {
        viewModelScope.launch {
            repository.deleteProductBySku(sku)
        }
    }

    fun addLocation(name: String) {
        viewModelScope.launch {
            repository.insertLocation(name)
        }
    }

    fun deleteLocation(name: String) {
        viewModelScope.launch {
            repository.deleteLocation(name)
        }
    }

    fun simulateScan() {
        viewModelScope.launch {
            val list = products.value
            if (list.isNotEmpty()) {
                val randProduct = list.random()
                _searchQuery.value = randProduct.sku
                _currentView.value = "counting"
            }
        }
    }

    fun generateCsvContent(): String {
        val sb = StringBuilder()
        sb.append("SKU,Name,Category,Location,System Stock,Physical Count,Difference\n")
        products.value.forEach { p ->
            val diff = p.physicalCount - p.systemStock
            val escapedName = p.name.replace("\"", "\"\"")
            sb.append("${p.sku},\"${escapedName}\",${p.category},${p.location},${p.systemStock},${p.physicalCount},$diff\n")
        }
        return sb.toString()
    }

    fun copyCsvToClipboard(context: Context) {
        val csv = generateCsvContent()
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("IKEA Food Stock csv", csv)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "CSV copied to clipboard!", Toast.LENGTH_SHORT).show()
    }

    fun shareCsvContent(context: Context) {
        val csv = generateCsvContent()
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "IKEA Food Inventory Report")
            putExtra(Intent.EXTRA_TEXT, csv)
        }
        context.startActivity(Intent.createChooser(intent, "Share Stock Count CSV"))
    }
}

class IkeaFoodViewModelFactory(private val repository: IkeaFoodRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(IkeaFoodViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return IkeaFoodViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
