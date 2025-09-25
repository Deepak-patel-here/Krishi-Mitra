package com.midnightcoder.krishimitra.di

import com.midnightcoder.krishimitra.data.remote.repoimpl.ChatRepositoryImpl
import com.midnightcoder.krishimitra.data.remote.repoimpl.ImageDetectionRepositoryImpl
import com.midnightcoder.krishimitra.data.remote.service.ChatService
import com.midnightcoder.krishimitra.data.remote.service.ImageDetectionService
import com.midnightcoder.krishimitra.domain.repository.ChatRepository
import com.midnightcoder.krishimitra.domain.repository.ImageDetectionRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideChatService(): ChatService{
        val retrofit= Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ChatService::class.java)
    }

    @Provides
    @Singleton
    fun provideImageDetectionService(): ImageDetectionService {
        val retrofit= Retrofit.Builder()
            .baseUrl("https://generativelanguage.googleapis.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(ImageDetectionService::class.java)
    }

    @Provides
    @Singleton
    fun provideChatRepository(chatService: ChatService): ChatRepository {
        return ChatRepositoryImpl(chatService)
    }

    @Provides
    @Singleton
    fun providesImageDetectionRepository(imageDetectionService: ImageDetectionService): ImageDetectionRepository =
        ImageDetectionRepositoryImpl(imageDetectionService)






}