package com.example.chatease.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chatease.data.local.entity.MediaItemEntity

@Dao
interface MediaItemsDao {

    @Query(
        """
        SELECT * FROM media_items
        WHERE conversationId = :conversationId
        ORDER BY timeStamp DESC
        """
    )
    suspend fun getMediaItems(
        conversationId: String
    ): List<MediaItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMediaItems(
        mediaItems: List<MediaItemEntity>
    )

    @Query(
        """
        DELETE FROM media_items
        WHERE conversationId = :conversationId
        """
    )
    suspend fun deleteMediaItems(
        conversationId: String
    )
}