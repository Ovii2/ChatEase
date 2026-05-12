package com.example.chatease.data.repository

import com.example.chatease.data.mapper.toDomain
import com.example.chatease.data.remote.CategoryRemoteDataSource
import com.example.chatease.domain.model.Category
import com.example.chatease.domain.repository.CategoryRepository

class CategoryRepositoryImpl(private val remoteDataSource: CategoryRemoteDataSource) :
    CategoryRepository {

    override suspend fun getCategories(): List<Category> {
        return remoteDataSource
            .getCategories()
            .map { it.toDomain() }
    }
}