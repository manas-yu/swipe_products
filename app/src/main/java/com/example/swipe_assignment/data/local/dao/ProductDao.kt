package com.example.swipe_assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.swipe_assignment.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("""
        SELECT COUNT(*) FROM products 
        WHERE productName = :name AND productType = :type 
          AND price = :price AND tax = :tax
    """)
    suspend fun countByKey(name: String, type: String, price: Double, tax: Double): Int

    @Query("""
        UPDATE products SET image = :serverImage
        WHERE productName = :name AND productType = :type 
          AND price = :price AND tax = :tax
    """)
    suspend fun updateImageByKey(
        name: String,
        type: String,
        price: Double,
        tax: Double,
        serverImage: String?
    )
}
