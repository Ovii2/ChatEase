package com.example.chatease.domain.repository

interface GroupRepository {

    suspend fun createGroup(name: String, memberIds: List<String>, imageUrl: String?): String
}