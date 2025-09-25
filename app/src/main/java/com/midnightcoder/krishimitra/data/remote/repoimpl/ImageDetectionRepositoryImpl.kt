package com.midnightcoder.krishimitra.data.remote.repoimpl

import android.graphics.Bitmap
import com.midnightcoder.krishimitra.data.remote.dto.geminiimage.GenerateContentRequest

import com.midnightcoder.krishimitra.data.remote.dto.geminitext.GeminiResponse

import com.midnightcoder.krishimitra.data.remote.service.ImageDetectionService
import com.midnightcoder.krishimitra.domain.repository.ImageDetectionRepository
import okhttp3.RequestBody
import javax.inject.Inject

class ImageDetectionRepositoryImpl @Inject constructor(private val imageDetectionService: ImageDetectionService) : ImageDetectionRepository {
    override suspend fun startUpload(
        apiKey: String,
        numBytes: Long,
        mimeType: String,
        metaData: Map<String, Any>
    ) {
        imageDetectionService.startUpload(apiKey=apiKey, numBytes = numBytes, mimeType = mimeType, metadata = metaData)
    }

    override suspend fun uploadFile(
        uploadUrl: String,
        apiKey: String,
        numBytes: Long,
        offset: Long,
        file: RequestBody
    ) {

        imageDetectionService.uploadBytes(
            uploadUrl = uploadUrl,
            apiKey = apiKey,
            numBytes = numBytes,
            offset = offset,
            file = file
        )

    }

    override suspend fun uploadImageToGemini(
        apiKey: String,
        request: GenerateContentRequest
    ) {
        imageDetectionService.uploadFile(
            apiKey = apiKey,
            request = request
        )
    }


}