package com.example.swipe_assignment.data.remote.dto

import com.example.swipe_assignment.domain.model.Product
import com.google.gson.annotations.SerializedName

data class ProductResponse(
    val message: String,
    @SerializedName("product_details")
    val details: Product,
    @SerializedName("product_id")
    val id: Int,
    val success: Boolean
)