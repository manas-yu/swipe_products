package com.example.swipe_assignment.presentation.product

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swipe_assignment.data.local.entity.ProductEntity
import com.example.swipe_assignment.domain.model.ErrorModel
import com.example.swipe_assignment.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductState())
    val uiState: StateFlow<ProductState> = _uiState.asStateFlow()

    init {
        loadProducts()
        getNotificationCount()
    }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) } // Show loading

            repository.getAllProducts().collect { result ->
                val products = result.data ?: emptyList()
                _uiState.update { state ->
                    when (result) {
                        is ErrorModel.Success -> state.copy(
                            products = products,
                            searchList = filterProducts(products, state.searchQuery),
                            isLoading = false,
                            error = null
                        )

                        is ErrorModel.Error -> state.copy(
                            isLoading = false,
                            error = result.message
                        )

                        is ErrorModel.Loading -> state.copy(
                            isLoading = true,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private fun getNotificationCount() {
        viewModelScope.launch {
            repository.getUnViewedCount().collect { count ->
                _uiState.update { it.copy(unViewedCount = count) }
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { currentState ->
            currentState.copy(
                searchQuery = query,
                searchList = filterProducts(currentState.products, query)
            )
        }
    }

    private fun filterProducts(products: List<ProductEntity>, query: String): List<ProductEntity> {
        return if (query.isBlank()) {
            products
        } else {
            products.filter { product ->
                product.productName.contains(query, ignoreCase = true) ||
                        product.productType.contains(query, ignoreCase = true) ||
                        product.price.toString().contains(query, ignoreCase = true)
            }
        }
    }


    fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            //     _uiState.update { it.copy(isLoading = true, error = null) } // Show loading
            try {
                val result = repository.addProduct(
                    productName = productName,
                    productType = productType,
                    price = price,
                    tax = tax,
                    imageUri = imageUri,
                    isForeground = true
                )

                when (result) {
                    is ErrorModel.Success -> {
                        Log.d("VIEWMODEL", "addProduct: ${result.message}")
                        loadProducts()
                    }

                    is ErrorModel.Error -> {
                        Log.d("VIEWMODEL", "addProduct error: ${result.message}")
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.message,
                            )
                        }
                    }

                    is ErrorModel.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }
            } catch (e: Exception) {
                Log.d("VIEWMODEL", "addProduct catch block: ${e.message}")
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message,
                    )
                }
            }

        }
    }

    fun clearError() {
        _uiState.value.error = null
    }
}