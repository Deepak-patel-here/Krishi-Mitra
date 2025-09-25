package com.midnightcoder.krishimitra.data.remote.dto.geminiimage

data class GenerateContentRequest(
    val contents: List<Content>
)

data class Content(
    val parts: List<Part>
)

sealed class Part {
    data class FileData(
        val file_data: FileDataDetail
    ) : Part()

    data class Text(
        val text: String
    ) : Part()
}

data class FileDataDetail(
    val mime_type: String,
    val file_uri: String
)
