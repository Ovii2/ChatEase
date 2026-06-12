package com.example.chatease.di

import android.content.Context
import com.example.chatease.data.local.datastore.user_preferences.UserPreferencesRepository
import com.example.chatease.data.remote.CategoryRemoteDataSource
import com.example.chatease.data.repository.CategoryRepositoryImpl
import com.example.chatease.data.repository.ContactRequestRepositoryImpl
import com.example.chatease.data.repository.ContactsRepositoryImpl
import com.example.chatease.data.repository.ConversationRepositoryImpl
import com.example.chatease.data.repository.UserRepositoryImpl
import com.example.chatease.domain.repository.CategoryRepository
import com.example.chatease.domain.repository.ContactRequestRepository
import com.example.chatease.domain.repository.ContactsRepository
import com.example.chatease.domain.repository.ConversationRepository
import com.example.chatease.domain.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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
    fun provideCategoryRemoteDataSource(firestore: FirebaseFirestore): CategoryRemoteDataSource =
        CategoryRemoteDataSource(firestore)

    @Provides
    @Singleton
    fun provideCategoryRepository(remoteDataSource: CategoryRemoteDataSource): CategoryRepository =
        CategoryRepositoryImpl(remoteDataSource)

    @Provides
    @Singleton
    fun provideUserRepository(firestore: FirebaseFirestore, auth: FirebaseAuth): UserRepository =
        UserRepositoryImpl(firestore, auth)

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
}