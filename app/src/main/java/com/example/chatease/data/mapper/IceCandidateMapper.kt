package com.example.chatease.data.mapper

import com.example.chatease.data.remote.dto.IceCandidateDto
import org.webrtc.IceCandidate

fun IceCandidate.toDto(): IceCandidateDto {
    return IceCandidateDto(
        sdpMid = sdpMid,
        sdpMLineIndex = sdpMLineIndex,
        sdp = sdp
    )
}

fun IceCandidateDto.toWebRtcIceCandidate(): IceCandidate {
    return IceCandidate(
        sdpMid,
        sdpMLineIndex,
        sdp
    )
}
