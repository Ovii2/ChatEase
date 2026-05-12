package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Category

interface CategoryRepository {

    suspend fun getCategories(): List<Category>
}