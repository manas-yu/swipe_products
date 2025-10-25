package com.example.swipe_assignment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import androidx.room.OnConflictStrategy
import com.example.swipe_assignment.data.local.entity.PendingUploadEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingUploadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(upload: PendingUploadEntity)

    @Query("SELECT * FROM pending_uploads ORDER BY timestamp ASC")
    fun getAll(): Flow<List<PendingUploadEntity>>

    @Delete
    suspend fun delete(upload: PendingUploadEntity)

    @Query("DELETE FROM pending_uploads")
    suspend fun deleteAll()
}
