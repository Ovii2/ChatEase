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

class WebRtcClient(private val context: Context) {

    lateinit var peerConnectionFactory: PeerConnectionFactory
        private set

    lateinit var localAudioTrack: AudioTrack
        private set

    lateinit var audioSource: AudioSource
        private set

    private val iceServers = listOf(
        PeerConnection
            .IceServer
            .builder("stun:stun.l.google.com:19302")
            .createIceServer()
    )

    private var peerConnection: PeerConnection? = null

    private val rtcConfiguration = PeerConnection.RTCConfiguration(iceServers)

    private var onIceCandidateCreated: ((IceCandidate) -> Unit)? = null

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions
                .builder(context)
                .createInitializationOptions()
        )

        peerConnectionFactory = PeerConnectionFactory
            .builder()
            .createPeerConnectionFactory()

        audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())

        localAudioTrack = peerConnectionFactory.createAudioTrack(
            "local_audio_track",
            audioSource
        )
    }

    fun setOnIceCandidateCreatedListener(listener: (IceCandidate) -> Unit) {
        onIceCandidateCreated = listener
    }

    val observer = object : PeerConnection.Observer {
        override fun onSignalingChange(p0: PeerConnection.SignalingState?) {

        }

        override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
            Log.v("WebRTC", "ICE connection state: $state")
        }

        override fun onIceConnectionReceivingChange(p0: Boolean) {

        }

        override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {

        }

        override fun onIceCandidate(candidate: IceCandidate?) {
            if (candidate != null) {
                onIceCandidateCreated?.invoke(candidate)
            }
        }

        override fun onIceCandidatesRemoved(p0: Array<out IceCandidate?>?) {

        }

        override fun onAddStream(p0: MediaStream?) {

        }

        override fun onRemoveStream(p0: MediaStream?) {

        }

        override fun onDataChannel(p0: DataChannel?) {

        }

        override fun onRenegotiationNeeded() {

        }

    }

    fun createPeerConnection(): PeerConnection? {
        peerConnection = peerConnectionFactory.createPeerConnection(
            rtcConfiguration,
            observer
        )

        peerConnection?.addTrack(localAudioTrack, listOf("audio_stream"))

        return peerConnection
    }

    fun setMuted(isMuted: Boolean) {
        localAudioTrack.setEnabled(!isMuted)
    }

    fun createOffer(onOfferCreated: (SessionDescription) -> Unit) {
        val constraints = MediaConstraints()

        peerConnection?.createOffer(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription?) {
                    if (description != null) {
                        peerConnection?.setLocalDescription(this, description)
                        onOfferCreated(description)
                    }
                }

                override fun onSetSuccess() {

                }

                override fun onCreateFailure(p0: String?) {

                }

                override fun onSetFailure(p0: String?) {

                }
            },
            constraints
        )
    }

    fun createAnswer(onAnswerCreated: (SessionDescription) -> Unit) {
        val constraints = MediaConstraints()

        peerConnection?.createAnswer(
            object : SdpObserver {
                override fun onCreateSuccess(description: SessionDescription?) {
                    if (description != null) {
                        peerConnection?.setLocalDescription(this, description)
                        onAnswerCreated(description)
                    }
                }

                override fun onSetSuccess() {

                }

                override fun onCreateFailure(p0: String?) {

                }

                override fun onSetFailure(p0: String?) {

                }
            },
            constraints
        )
    }

    fun setRemoteDescription(description: SessionDescription) {
        peerConnection?.setRemoteDescription(
            object : SdpObserver {
                override fun onCreateSuccess(p0: SessionDescription?) {

                }

                override fun onSetSuccess() {

                }

                override fun onCreateFailure(p0: String?) {

                }

                override fun onSetFailure(p0: String?) {

                }
            },
            description
        )
    }

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection?.addIceCandidate(candidate)
    }

    fun initializeAudio() {
        configureAudioRoute()

        audioSource = peerConnectionFactory.createAudioSource(MediaConstraints())

        localAudioTrack = peerConnectionFactory.createAudioTrack(
            "local_audio_track",
            audioSource
        )
    }

    fun endCall() {
        localAudioTrack.setEnabled(false)
        audioSource.dispose()
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

        Log.v("WebRTC", "Audio mode = ${audioManager.mode}")
        Log.v("WebRTC", "Speaker on = ${audioManager.isSpeakerphoneOn}")
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

            device?.let {
                audioManager.setCommunicationDevice(it)
            }
        } else {
            audioManager.isSpeakerphoneOn = enabled
        }
    }

}
