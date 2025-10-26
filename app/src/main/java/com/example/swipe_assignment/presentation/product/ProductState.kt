package com.example.swipe_assignment.presentation.product

import com.example.swipe_assignment.data.local.entity.ProductEntity

data class ProductState(
    val products: List<ProductEntity> = emptyList(),
    val searchList: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = false,
    var error: String? = null,
    val searchQuery: String = "",
    var unViewedCount: Int = 0

)