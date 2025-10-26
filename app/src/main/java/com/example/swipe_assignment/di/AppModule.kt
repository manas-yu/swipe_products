package com.example.swipe_assignment.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.swipe_assignment.data.local.ProductLocalDB
import com.example.swipe_assignment.data.local.dao.NotificationDao
import com.example.swipe_assignment.data.local.dao.PendingUploadDao
import com.example.swipe_assignment.data.local.dao.ProductDao
import com.example.swipe_assignment.data.remote.ProductApi
import com.example.swipe_assignment.util.Constants.BASE_URL
import com.example.swipe_assignment.util.NetworkChecker
import com.example.swipe_assignment.util.NotificationHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- Networking ---
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @Singleton
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient =
        OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): ProductApi =
        retrofit.create(ProductApi::class.java)

    // --- Room database + DAOs ---
    @Provides
    @Singleton
    fun provideDatabase(app: Application): ProductLocalDB =
        Room.databaseBuilder(app, ProductLocalDB::class.java, "swipe-db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideProductDao(db: ProductLocalDB): ProductDao = db.productDao()

    @Provides
    @Singleton
    fun provideNotificationDao(db: ProductLocalDB): NotificationDao = db.notificationDao()

    @Provides
    @Singleton
    fun providePendingUploadDao(db: ProductLocalDB): PendingUploadDao = db.pendingUploadDao()

    // --- Utilities used in repositories/workers ---
    @Provides
    @Singleton
    fun provideUploadNotifier(@ApplicationContext context: Context): NotificationHelper =
        NotificationHelper(context)

    @Provides
    @Singleton
    fun provideNetworkChecker(@ApplicationContext context: Context): NetworkChecker =
        NetworkChecker(context)
}
