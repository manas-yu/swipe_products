package com.example.swipe_assignment.util.worker

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.swipe_assignment.data.local.dao.PendingUploadDao
import com.example.swipe_assignment.domain.model.ErrorModel
import com.example.swipe_assignment.domain.repository.ProductRepository
import com.example.swipe_assignment.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class ProductUploadWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: ProductRepository,
    private val pendingUploadDao: PendingUploadDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = try {
        val items = pendingUploadDao.getAll()
        for (p in items) {
            notificationHelper.showUploadProgressNotification(p.productName)
            when (repository.addProduct(
                productName = p.productName,
                productType = p.productType,
                price = p.price,
                tax = p.tax,
                imageUri = p.imageUri?.let(Uri::parse),
                isForeground = false
            )) {
                is ErrorModel.Success -> {
                    pendingUploadDao.delete(p)
                }
                is ErrorModel.Error -> Log.e("WORKER", "Upload failed for ${p.productName}")
                else -> Log.d("WORKER", "Unexpected state for ${p.productName}")
            }
        }
        Result.success()
    } catch (e: Exception) {
        Log.e("WORKER", "Error: ${e.message}", e)
        Result.retry()
    } finally {
        notificationHelper.hideProgressNotification()
    }

    companion object {
        private const val UNIQUE = "product_upload_work_v5"

        fun schedule(context: Context) {
            val req = OneTimeWorkRequestBuilder<ProductUploadWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                )
                .addTag("product_upload_v5")
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE,
                ExistingWorkPolicy.REPLACE,
                req
            )
        }
    }
}
