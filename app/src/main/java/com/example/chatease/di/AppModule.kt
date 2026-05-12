package com.example.chatease.di

import com.example.chatease.data.remote.CategoryRemoteDataSource
import com.example.chatease.data.repository.CategoryRepositoryImpl
import com.example.chatease.domain.repository.CategoryRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
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

}