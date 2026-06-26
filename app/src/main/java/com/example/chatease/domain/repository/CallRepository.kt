package com.example.chatease.domain.repository

import com.example.chatease.data.remote.dto.IceCandidateDto
import com.example.chatease.data.remote.dto.SessionDescriptionDto
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
    suspend fun startCallTimeout(callId: String)
    suspend fun sendOffer(callId: String, offer: SessionDescriptionDto)
    suspend fun sendAnswer(callId: String, answer: SessionDescriptionDto)
    suspend fun sendIceCandidate(callId: String, candidate: IceCandidateDto)
    fun observeOffer(callId: String): Flow<SessionDescriptionDto?>
    fun observeAnswer(callId: String): Flow<SessionDescriptionDto?>
    fun observeIceCandidates(callId: String): Flow<List<IceCandidateDto>>
    suspend fun updateConnectedAt(callId: String, connectedAt: Long)
}