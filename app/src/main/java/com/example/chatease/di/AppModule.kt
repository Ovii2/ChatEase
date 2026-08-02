package com.example.chatease.di

import android.content.Context
import com.example.chatease.data.local.datastore.user_preferences.UserPreferencesRepository
import com.example.chatease.data.repository.CallRepositoryImpl
import com.example.chatease.data.repository.ContactRequestRepositoryImpl
import com.example.chatease.data.repository.ContactsRepositoryImpl
import com.example.chatease.data.repository.ConversationRepositoryImpl
import com.example.chatease.data.repository.GroupRepositoryImpl
import com.example.chatease.data.repository.UserRepositoryImpl
import com.example.chatease.data.webrtc.WebRtcClient
import com.example.chatease.domain.repository.CallRepository
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.GroupRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideUserRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): UserRepository =
        UserRepositoryImpl(firestore, auth, storage)

    @Provides
    @Singleton
    fun provideConversationRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): ConversationRepository =
        ConversationRepositoryImpl(firestore, auth)

    @Provides
    @Singleton
    fun provideContactRequestRepository(firestore: FirebaseFirestore): ContactRequestRepository =
        ContactRequestRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideContactsRepository(firestore: FirebaseFirestore): ContactsRepository =
        ContactsRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideUserPreferencesRepository(@ApplicationContext context: Context): UserPreferencesRepository =
        UserPreferencesRepository(context)

    @Provides
    @Singleton
    fun provideCallRepository(firestore: FirebaseFirestore): CallRepository =
        CallRepositoryImpl(firestore)

    @Provides
    @Singleton
    fun provideWebRtcClient(@ApplicationContext context: Context): WebRtcClient =
        WebRtcClient(context)

    @Provides
    @Singleton
    fun provideGroupRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth,
        storage: FirebaseStorage
    ): GroupRepository = GroupRepositoryImpl(firestore, auth, storage)

}
