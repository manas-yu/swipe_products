package com.example.swipe_assignment.presentation.notification

import com.example.swipe_assignment.data.local.entity.NotificationEntity


data class NotificationUiState(
    val notificationList: List<NotificationEntity> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)