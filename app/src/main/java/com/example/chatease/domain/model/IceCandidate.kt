package com.example.chatease.domain.model

data class IceCandidate(
    val sdpMid: String? = null,
    val sdpMLineIndex: Int = 0,
    val sdp: String = ""
)
