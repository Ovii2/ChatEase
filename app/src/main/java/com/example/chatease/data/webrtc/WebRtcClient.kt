package com.example.chatease.data.webrtc

import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import android.util.Log
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

class WebRtcClient(
    private val context: Context
) {
    companion object {
        private const val TAG = "WebRTC"
        private const val LOCAL_AUDIO_TRACK_ID = "local_audio_track"
        private const val AUDIO_STREAM_ID = "audio_stream"
    }

    lateinit var peerConnectionFactory: PeerConnectionFactory
        private set

    private var peerConnection: PeerConnection? = null
    private var audioSource: AudioSource? = null
    private var localAudioTrack: AudioTrack? = null
    private var onIceCandidateCreated: ((IceCandidate) -> Unit)? = null

    private val audioManager =
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    private val iceServers = listOf(
        PeerConnection.IceServer
            .builder("stun:stun.l.google.com:19302")
            .createIceServer()
    )

    private val rtcConfiguration = PeerConnection.RTCConfiguration(iceServers)

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions
                .builder(context)
                .createInitializationOptions()
        )

        peerConnectionFactory = PeerConnectionFactory
            .builder()
            .createPeerConnectionFactory()
    }

    fun setOnIceCandidateCreatedListener(listener: (IceCandidate) -> Unit) {
        onIceCandidateCreated = listener
    }

    fun initializeAudio() {
        configureAudioRoute()

        audioSource?.dispose()

        audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())
        localAudioTrack = peerConnectionFactory.createAudioTrack(
            LOCAL_AUDIO_TRACK_ID,
            audioSource
        )
    }

    fun createPeerConnection(): PeerConnection? {
        peerConnection = peerConnectionFactory.createPeerConnection(
            rtcConfiguration,
            observer
        )

        localAudioTrack?.let { track ->
            peerConnection?.addTrack(track, listOf(AUDIO_STREAM_ID))
        }

        return peerConnection
    }

    fun createOffer(onOfferCreated: (SessionDescription) -> Unit) {
        peerConnection?.createOffer(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription?) {
                    if (description != null) {
                        peerConnection?.setLocalDescription(this, description)
                        onOfferCreated(description)
                    }
                }

                override fun onSetSuccess() = Unit
                override fun onCreateFailure(error: String?) = Unit
                override fun onSetFailure(error: String?) = Unit
            },
            MediaConstraints()
        )
    }

    fun createAnswer(onAnswerCreated: (SessionDescription) -> Unit) {
        peerConnection?.createAnswer(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription?) {
                    if (description != null) {
                        peerConnection?.setLocalDescription(this, description)
                        onAnswerCreated(description)
                    }
                }

                override fun onSetSuccess() = Unit
                override fun onCreateFailure(error: String?) = Unit
                override fun onSetFailure(error: String?) = Unit
            },
            MediaConstraints()
        )
    }

    fun setRemoteDescription(description: SessionDescription) {
        peerConnection?.setRemoteDescription(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription?) = Unit
                override fun onSetSuccess() = Unit
                override fun onCreateFailure(error: String?) = Unit
                override fun onSetFailure(error: String?) = Unit
            },
            description
        )
    }

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection?.addIceCandidate(candidate)
    }

    fun setMuted(isMuted: Boolean) {
        localAudioTrack?.setEnabled(!isMuted)
    }

    fun setSpeakerEnabled(enabled: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val deviceType = if (enabled) {
                AudioDeviceInfo.TYPE_BUILTIN_SPEAKER
            } else {
                AudioDeviceInfo.TYPE_BUILTIN_EARPIECE
            }

            val device = audioManager.availableCommunicationDevices.firstOrNull {
                it.type == deviceType
            }

            if (device != null) {
                audioManager.setCommunicationDevice(device)
            }
        } else {
            audioManager.isSpeakerphoneOn = enabled
        }

        setVoiceCallVolumeToMax()
    }

    fun endCall() {
        localAudioTrack?.setEnabled(false)
        localAudioTrack = null

        audioSource?.dispose()
        audioSource = null

        peerConnection?.close()
        peerConnection?.dispose()
        peerConnection = null

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            audioManager.clearCommunicationDevice()
        }

        audioManager.mode = AudioManager.MODE_NORMAL
    }

    private fun configureAudioRoute() {
        audioManager.mode = AudioManager.MODE_IN_COMMUNICATION

        Log.v(
            TAG,
            "Audio mode=${audioManager.mode}, expected=${AudioManager.MODE_IN_COMMUNICATION}"
        )
    }

    private fun setVoiceCallVolumeToMax() {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL)

        audioManager.setStreamVolume(
            AudioManager.STREAM_VOICE_CALL,
            maxVolume,
            0
        )

        Log.v(
            TAG,
            "Voice call volume=${audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL)} / $maxVolume"
        )
    }

    private val observer = object : PeerConnection.Observer {
        override fun onSignalingChange(state: PeerConnection.SignalingState?) = Unit

        override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
            Log.v(TAG, "ICE connection state: $state")
        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) = Unit
        override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) = Unit

        override fun onIceCandidate(candidate: IceCandidate?) {
            if (candidate != null) {
                onIceCandidateCreated?.invoke(candidate)
            }
        }

        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate?>?) = Unit
        override fun onAddStream(stream: MediaStream?) = Unit
        override fun onRemoveStream(stream: MediaStream?) = Unit
        override fun onDataChannel(channel: DataChannel?) = Unit
        override fun onRenegotiationNeeded() = Unit
    }
}