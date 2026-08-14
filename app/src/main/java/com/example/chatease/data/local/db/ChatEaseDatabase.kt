package com.example.chatease.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.chatease.data.local.dao.MediaItemsDao
import com.example.chatease.data.local.entity.MediaItemEntity

@Database(
    entities = [MediaItemEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ChatEaseDatabase : RoomDatabase() {

    abstract fun mediaItemsDao(): MediaItemsDao
}