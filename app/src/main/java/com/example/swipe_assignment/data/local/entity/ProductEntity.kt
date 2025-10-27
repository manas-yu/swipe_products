package com.example.swipe_assignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swipe_assignment.util.Constants

@Entity(tableName = Constants.PRODUCTS_TABLE)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val image: String?,
    val price: Double,
    val productName: String,
    val productType: String,
    val tax: Double
)