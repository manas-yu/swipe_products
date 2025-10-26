package com.example.swipe_assignment.data.repository

import android.util.Log
import com.example.swipe_assignment.data.local.dao.NotificationDao
import com.example.swipe_assignment.data.local.entity.NotificationEntity
import com.example.swipe_assignment.domain.model.ErrorModel
import com.example.swipe_assignment.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class NotificationRepositoryImpl @Inject constructor(
    private val notificationDao: NotificationDao
) : NotificationRepository {
    override fun getAll(): Flow<ErrorModel<List<NotificationEntity>>> = flow {
        Log.d("REPOSITORY", "getAllProducts()")
        emit(ErrorModel.Loading())
        try {
            notificationDao.getAllNotification().collect { products ->
                emit(ErrorModel.Success(products))
            }
        } catch (e: Exception) {
            Log.d("REPOSITORY", "getAllProducts() ERRor ${e.message}")
            emit(ErrorModel.Error(e.message ?: "Unknown error"))
        }
    }

    override suspend fun markAsViewed() {
        Log.d("REPOSITORY", "markAsViewed() executed")
        notificationDao.updateAllProductsAsViewed()
    }
}