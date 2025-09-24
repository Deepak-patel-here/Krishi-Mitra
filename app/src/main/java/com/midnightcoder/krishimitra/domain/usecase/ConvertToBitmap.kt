package com.midnightcoder.krishimitra.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

object ConvertToBitmap {

    fun toBitMap(uri: Uri, context: Context): Bitmap {
        val inputStream = context.contentResolver.openInputStream(uri)
        val bitmap = BitmapFactory.decodeStream(inputStream)
        return bitmap
    }

    fun toByteArray(uri: Uri,context: Context): ByteArray?{
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArray= inputStream?.readBytes()
        return byteArray
    }
}