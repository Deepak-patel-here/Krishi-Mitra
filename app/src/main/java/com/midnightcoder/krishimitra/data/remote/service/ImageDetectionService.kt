package com.midnightcoder.krishimitra.data.remote.service


import com.midnightcoder.krishimitra.data.remote.dto.geminiimage.FileInfoResponse
import com.midnightcoder.krishimitra.data.remote.dto.geminiimage.GenerateContentRequest
import com.midnightcoder.krishimitra.data.remote.dto.geminitext.GeminiResponse
import retrofit2.http.POST
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ImageDetectionService {


        @POST("upload/v1beta/files")
        @Headers(
            "X-Goog-Upload-Protocol: resumable",
            "Content-Type: application/json"
        )
        suspend fun startUpload(
            @Header("x-goog-api-key") apiKey: String,
            @Header("X-Goog-Upload-Header-Content-Length") numBytes: Long,
            @Header("X-Goog-Upload-Header-Content-Type") mimeType: String,
            @Body metadata: Map<String, Any>
        ): retrofit2.Response<Unit>

        @PUT
        suspend fun uploadBytes(
            @Url uploadUrl: String,
            @Header("x-goog-api-key") apiKey: String,
            @Header("Content-Length") numBytes: Long,
            @Header("X-Goog-Upload-Offset") offset: Long = 0,
            @Header("X-Goog-Upload-Command") command: String = "upload, finalize",
            @Body file: RequestBody
        ): FileInfoResponse


    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun uploadFile(
        @Header("x-goog-api-key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GeminiResponse


}