package com.midnightcoder.krishimitra.domain.repository

interface ChatRepository {
    suspend fun sendMessage(message: String): String
}