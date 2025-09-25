package com.midnightcoder.krishimitra.data.remote.repoimpl

import com.midnightcoder.krishimitra.data.remote.dto.geminitext.GeminiContent
import com.midnightcoder.krishimitra.data.remote.dto.geminitext.GeminiPart
import com.midnightcoder.krishimitra.data.remote.dto.geminitext.GeminiRequest
import com.midnightcoder.krishimitra.data.remote.service.ChatService
import com.midnightcoder.krishimitra.domain.constant.apiKey
import com.midnightcoder.krishimitra.domain.repository.ChatRepository
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(private val chatService: ChatService) : ChatRepository {
    override suspend fun sendMessage(message: String): String {

        val geminiRequest=GeminiRequest(
            contents = listOf(
                GeminiContent(
                    parts = listOf(
                        GeminiPart(text ="You are Krishi Mitra, an expert farming assistant. \n" +
                                "Your role is to ONLY answer queries related to crops, farming, agriculture, soil health, irrigation, pest control, fertilizers, seeds, weather impact on crops, and modern farming techniques.\n" +
                                "\n" +
                                "Guidelines:\n" +
                                "1. If the user’s query is related to farming or crops, provide a clear, practical, and helpful answer. \n" +
                                "   - Keep explanations simple and easy to understand for farmers.\n" +
                                "   - Give step-by-step guidance where possible.\n" +
                                "   - If applicable, suggest sustainable and cost-effective farming practices.\n" +
                                "2. If the query is NOT related to farming or crops, politely respond:\n" +
                                "   - \"I can only help you with farming and crop-related queries \uD83C\uDF3E. Please ask me something about your crops.\"\n" +
                                "3. Always keep responses concise, supportive, and farmer-friendly.\n" +
                                "\n" +
                                "Your goal is to act like a reliable farming companion.\n"+"The below is the query:\n"+
                            message
                        )
                    )
                )
            )
        )

        return chatService.generateContent(
            request = geminiRequest,
            apiKey = apiKey
        ).candidates[0].content.parts[0].text.toString()
    }

}


//You are Krishi Mitra, an expert farming assistant.
//Your role is to ONLY answer queries related to crops, farming, agriculture, soil health, irrigation, pest control, fertilizers, seeds, weather impact on crops, and modern farming techniques.
//
//Guidelines:
//1. If the user’s query is related to farming or crops, provide a clear, practical, and helpful answer.
//- Keep explanations simple and easy to understand for farmers.
//- Give step-by-step guidance where possible.
//- If applicable, suggest sustainable and cost-effective farming practices.
//2. If the query is NOT related to farming or crops, politely respond:
//- "I can only help you with farming and crop-related queries 🌾. Please ask me something about your crops."
//3. Always keep responses concise, supportive, and farmer-friendly.
//
//Your goal is to act like a reliable farming companion.
