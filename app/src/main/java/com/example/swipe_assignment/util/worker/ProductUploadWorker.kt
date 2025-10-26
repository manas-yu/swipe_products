package com.example.swipe_assignment.util.worker

import android.content.Context
import android.net.Uri
import android.util.Log
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

class ProductUploadWorker(
    context: Context,
    params: WorkerParameters,
    private val repository: ProductRepository,
    private val pendingUploadDao: PendingUploadDao,
    private val notificationHelper: NotificationHelper
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        try {
            pendingUploadDao.getAll().forEach { pendingUpload ->
                notificationHelper.showUploadProgressNotification(pendingUpload.productName)
                val result = repository.addProduct(
                    productName = pendingUpload.productName,
                    productType = pendingUpload.productType,
                    price = pendingUpload.price,
                    tax = pendingUpload.tax,
                    imageUri = pendingUpload.imageUri?.let { Uri.parse(it) },
                    isForeground = false
                )
                when (result) {
                    is ErrorModel.Success -> {
                        pendingUploadDao.delete(pendingUpload)
                    }

                    is ErrorModel.Error -> {
                        Log.e("WORKER", "Upload failed for ${pendingUpload.productName}")
                    }

                    else -> Log.d("WORKER", "Unexpected state during upload")
                }
            }
            return Result.success()
        } catch (e: Exception) {
            Log.e("WORKER", "Error during product upload: ${e.message}")
            return Result.retry()
        }
    }

    companion object {
        private const val UNIQUE_WORK_NAME = "product_upload_work"

        fun schedule(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val uploadWorkRequest = OneTimeWorkRequestBuilder<ProductUploadWorker>()
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                UNIQUE_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                uploadWorkRequest
            )
        }
    }
}
