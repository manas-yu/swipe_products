package com.example.swipe_assignment.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.swipe_assignment.data.local.dao.NotificationDao
import com.example.swipe_assignment.data.local.dao.PendingUploadDao
import com.example.swipe_assignment.data.local.dao.ProductDao
import com.example.swipe_assignment.data.local.entity.NotificationEntity
import com.example.swipe_assignment.data.local.entity.PendingUploadEntity
import com.example.swipe_assignment.data.local.entity.ProductEntity

@Database(
    entities = [ProductEntity::class, PendingUploadEntity::class, NotificationEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ProductLocalDB : RoomDatabase() {
    abstract fun productDao(): ProductDao
    abstract fun pendingUploadDao(): PendingUploadDao
    abstract fun notificationDao(): NotificationDao
}