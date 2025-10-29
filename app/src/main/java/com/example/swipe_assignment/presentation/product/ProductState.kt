package com.example.swipe_assignment.presentation.product

import androidx.compose.runtime.Immutable
import com.example.swipe_assignment.data.local.entity.ProductEntity

@Immutable
data class ProductState(
    val products: List<ProductEntity> = emptyList(),
    val searchList: List<ProductEntity> = emptyList(),
    val isLoading: Boolean = false,
    var error: String? = null,
    val searchQuery: String = "",
    val unViewedCount: Int = 0

)