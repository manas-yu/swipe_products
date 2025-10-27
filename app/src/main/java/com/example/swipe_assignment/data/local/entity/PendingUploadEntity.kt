package com.example.swipe_assignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swipe_assignment.util.Constants

@Entity(tableName = Constants.PENDING_UPLOADS_TABLE)
data class PendingUploadEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val productType: String,
    val price: Double,
    val tax: Double,
    val imageUri: String?,
    val timestamp: Long = System.currentTimeMillis()
)