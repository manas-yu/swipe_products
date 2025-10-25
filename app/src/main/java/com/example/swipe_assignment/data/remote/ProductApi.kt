package com.example.swipe_assignment.data.remote

import com.example.swipe_assignment.data.remote.dto.ProductResponse
import com.example.swipe_assignment.domain.model.Product
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface ProductApi {

    @GET("get")
    suspend fun getProducts(): Response<List<Product>>

    /**
     * Add or upload a product.
     *
     * @param productData Key-value pairs of product fields (name, type, price, tax, etc.)
     * @param images Optional list of images
     */
    @Multipart
    @POST("add")
    suspend fun addOrUploadProduct(
        @PartMap productData: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part images: List<MultipartBody.Part> = emptyList()
    ): Response<ProductResponse>
}
