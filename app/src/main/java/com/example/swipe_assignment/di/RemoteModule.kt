// app/src/main/java/com/example/swipe_assignment/di/BindModule.kt
package com.example.swipe_assignment.di

import com.example.swipe_assignment.data.repository.NotificationRepositoryImpl
import com.example.swipe_assignment.data.repository.ProductRepositoryImpl
import com.example.swipe_assignment.domain.repository.NotificationRepository
import com.example.swipe_assignment.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RemoteModule {
    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository
}
