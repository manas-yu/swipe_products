package com.example.swipe_assignment.domain.model

import com.example.swipe_assignment.data.local.entity.ProductEntity
import com.google.gson.annotations.SerializedName

data class Product(
    val image: String? = null,
    val price: Double,
    @SerializedName("product_name")
    val name: String,
    @SerializedName("product_type")
    val type: String,
    val tax: Double
) {
    fun toEntity(): ProductEntity {
        return ProductEntity(
            id = 0,
            image = image,
            price = price,
            productName = name,
            productType = type,
            tax = tax,
        )
    }
}