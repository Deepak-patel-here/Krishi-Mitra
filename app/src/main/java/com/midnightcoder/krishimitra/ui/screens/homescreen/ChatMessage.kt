package com.midnightcoder.krishimitra.ui.screens.homescreen

import android.graphics.Bitmap

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val image: Bitmap?=null
)

