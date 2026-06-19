package com.example.chatease.domain.repository

import com.example.chatease.domain.model.Call
import com.example.chatease.domain.model.CallHistory
import com.example.chatease.domain.model.enums.CallStatus
import kotlinx.coroutines.flow.Flow

interface CallRepository {

    suspend fun createCall(call: Call)
    suspend fun updateCallStatus(callId: String, status: CallStatus)
    fun observeCall(callId: String): Flow<Call?>
    fun observeIncomingCall(userId: String): Flow<Call?>
    fun observeCallHistory(userId: String): Flow<List<CallHistory>>
    suspend fun createCallHistory(callHistory: CallHistory)
}