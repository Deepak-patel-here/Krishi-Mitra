package com.midnightcoder.krishimitra.data.remote.dto.geminiimage

data class FileInfoResponse(
    val file: FileInfo
)

data class FileInfo(
    val name: String,
    val display_name: String,
    val mime_type: String,
    val size_bytes: String,
    val create_time: String,
    val update_time: String,
    val uri: String
)
