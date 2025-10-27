package com.example.swipe_assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.swipe_assignment.data.local.entity.PendingUploadEntity

@Dao
interface PendingUploadDao {

    @Delete
    suspend fun delete(upload: PendingUploadEntity)

    @Insert
    suspend fun insert(upload: PendingUploadEntity)

    @Query("SELECT * FROM pending_uploads ORDER BY timestamp ASC")
    fun getAll(): List<PendingUploadEntity>
}