package com.example.swipe_assignment.data.repository
import com.example.swipe_assignment.data.local.entity.NotificationEntity
import com.example.swipe_assignment.domain.model.ErrorModel
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {
    fun getAll(): Flow<ErrorModel<List<NotificationEntity>>>
    suspend fun markAsViewed()
}