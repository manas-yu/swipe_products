package com.example.swipe_assignment.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.swipe_assignment.util.Progress

@Entity
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val productName: String,
    val productType: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isViewed: Boolean = false,
    val status: Progress = Progress.Pending
)