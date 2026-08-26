package com.example.chatease.domain.repository

interface FcmTokenProvider {
    suspend fun deleteToken()
    suspend fun getToken(): String
}