package com.midnightcoder.krishimitra.domain.repository

import android.graphics.Bitmap
import com.midnightcoder.krishimitra.data.remote.dto.geminiimage.GenerateContentRequest
import com.midnightcoder.krishimitra.data.remote.dto.geminitext.GeminiResponse
import okhttp3.RequestBody

interface ImageDetectionRepository  {

    suspend fun startUpload(
        apiKey:String,
        numBytes: Long,
        mimeType: String,
        metaData: Map<String, Any>
    )

    suspend fun uploadFile(
        uploadUrl:String,
        apiKey: String,
        numBytes: Long,
        offset: Long = 0,

        file: RequestBody
    )

    suspend fun uploadImageToGemini(
        apiKey: String,
        request: GenerateContentRequest
    )
}