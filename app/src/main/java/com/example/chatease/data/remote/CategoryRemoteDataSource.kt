package com.example.chatease.data.remote

import com.example.chatease.data.remote.dto.CategoryDto
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CategoryRemoteDataSource(private val firestore: FirebaseFirestore) {

    suspend fun getCategories(): List<CategoryDto> {
        val snapshot = firestore
            .collection("categories")
            .get()
            .await()

        return snapshot.toObjects(CategoryDto::class.java)
    }
}