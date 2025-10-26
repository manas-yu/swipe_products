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
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(productRepositoryImpl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindNotificationRepository(notificationRepositoryImpl: NotificationRepositoryImpl): NotificationRepository

}