package com.example.swipe_assignment.data.repository

import android.content.Context
import android.net.Uri
import com.example.swipe_assignment.data.local.dao.NotificationDao
import com.example.swipe_assignment.data.local.dao.PendingUploadDao
import com.example.swipe_assignment.data.local.dao.ProductDao
import com.example.swipe_assignment.data.local.entity.NotificationEntity
import com.example.swipe_assignment.data.local.entity.PendingUploadEntity
import com.example.swipe_assignment.data.local.entity.ProductEntity
import com.example.swipe_assignment.data.remote.ProductApi
import com.example.swipe_assignment.domain.model.ErrorModel
import com.example.swipe_assignment.domain.repository.ProductRepository
import com.example.swipe_assignment.util.NetworkChecker
import com.example.swipe_assignment.util.NotificationHelper
import com.example.swipe_assignment.util.Progress
import com.example.swipe_assignment.util.worker.ProductUploadWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApi,
    private val productDao: ProductDao,
    private val pendingUploadDao: PendingUploadDao,
    private val notificationDao: NotificationDao,
    @ApplicationContext private val context: Context,
    private val notificationHelper: NotificationHelper,
    private val networkChecker: NetworkChecker
) : ProductRepository {

    override fun getAllProducts(): Flow<ErrorModel<List<ProductEntity>>> = flow {
        emit(ErrorModel.Loading())
        try {
            if (networkChecker.isOnline()) refreshProducts()
            productDao.getAllProducts().collect { products ->
                emit(ErrorModel.Success(products))
            }
        } catch (e: Exception) {
            emit(ErrorModel.Error(e.message ?: "Unknown error"))
        }
    }

    private suspend fun refreshProducts() {
        try {
            val response = api.getProducts()
            if (response.isSuccessful) {
                productDao.deleteAllProducts()
                response.body()?.forEach { product ->
                    productDao.insertProduct(product.toEntity())
                }
            }
        } catch (e: Exception) {
            return
        }
    }

    override suspend fun addProduct(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageUri: Uri?,
        isForeground: Boolean,
    ): ErrorModel<Unit> {
        return try {
            if (!networkChecker.isOnline()) {
                savePendingUpload(productName, productType, price, tax, imageUri)
                return ErrorModel.Success(Unit)
            }

            notificationHelper.showUploadProgressNotification(productName)
            if (notificationDao.getNotificationByProductName(productName) == 0) {
                notificationDao.insertProductNotification(
                    NotificationEntity(
                        productType = productType,
                        productName = productName,
                        status = Progress.Pending
                    )
                )
            }

            val requestBodyMap = mutableMapOf<String, RequestBody>()
            requestBodyMap["product_name"] =
                productName.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["product_type"] =
                productType.toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["price"] =
                price.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            requestBodyMap["tax"] = tax.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart =
                if (!isForeground)
                    imageUri?.let { createImagePart(Uri.parse(it.toString()).path.toString()) }
                else
                    imageUri?.let { createImagePart(it) }

            val response = api.addProduct(requestBodyMap, imagePart)
            if (response.isSuccessful) {
                notificationDao.updateProductStatus(
                    productName = productName,
                    status = Progress.Uploaded,
                    isViewed = false
                )
                notificationHelper.hideProgressNotification()
                notificationHelper.showUploadSuccessNotification(productName)
                ErrorModel.Success(Unit)
            } else {
                notificationHelper.hideProgressNotification()
                notificationHelper.showUploadFailureNotification(productName, response.message())
                ErrorModel.Error(response.message() ?: "Error while adding the product")
            }
        } catch (e: Exception) {
            notificationHelper.hideProgressNotification()
            notificationHelper.showUploadFailureNotification(
                productName,
                e.localizedMessage ?: "Unknown error"
            )
            notificationDao.updateProductStatus(
                productName = productName,
                status = Progress.Failed,
                isViewed = false
            )
            ErrorModel.Error(e.message ?: "Unknown error")
        }
    }

    private fun createImagePart(uri: Uri): MultipartBody.Part? {
        try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null

            val tempFile = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
            tempFile.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }

            val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
            return MultipartBody.Part.createFormData("files[]", tempFile.name, requestFile)
        } catch (e: Exception) {
            return null
        }
    }

    private suspend fun savePendingUpload(
        productName: String,
        productType: String,
        price: Double,
        tax: Double,
        imageUri: Uri?
    ) {
        val imagePath = imageUri?.let { persistImage(it) }
        pendingUploadDao.insert(
            PendingUploadEntity(
                productName = productName,
                productType = productType,
                price = price,
                tax = tax,
                imageUri = imagePath
            )
        )
        notificationDao.insertProductNotification(
            NotificationEntity(
                productType = productType,
                productName = productName,
                status = Progress.Pending
            )
        )
        notificationHelper.showUploadProgressNotification(
            productName
        )
        ProductUploadWorker.schedule(context)
    }

    private fun persistImage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, "image_${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)
            inputStream?.copyTo(outputStream)
            outputStream.close()
            inputStream?.close()
            file.absolutePath
        } catch (e: Exception) {
            null
        }
    }

    private fun createImagePart(filePath: String): MultipartBody.Part? {
        return try {
            val file = File(filePath)
            val requestFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("files[]", file.name, requestFile)
        } catch (e: Exception) {
            null
        }
    }

    override fun getUnViewedCount(): Flow<Int> = flow {
        notificationDao.getUnViewedCount().collect { emit(it) }
    }
}
