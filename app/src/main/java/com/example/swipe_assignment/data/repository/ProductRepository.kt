package com.example.swipe_assignment.data.repository

import android.net.Uri
import com.example.swipe_assignment.data.local.entity.ProductEntity
import com.example.swipe_assignment.domain.model.ErrorModel
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getAllProducts(): Flow<ErrorModel<List<ProductEntity>>>
    suspend fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageUri: Uri?,
        isForeground: Boolean
    ): ErrorModel<Unit>
    fun getUnViewedCount(): Flow<Int>
}