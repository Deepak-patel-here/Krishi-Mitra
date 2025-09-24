package com.midnightcoder.krishimitra.ui.screens.homescreen

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ChatBubbleThemeAdaptive(message: ChatMessage, bitmap: Bitmap? = null) {
    val colorScheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Surface(
            color = if (message.isUser) colorScheme.primary else colorScheme.surfaceVariant,
            shape = RoundedCornerShape(20.dp),
            shadowElevation = 4.dp,
            modifier = Modifier.widthIn(max = 280.dp)
        ) {
            SelectionContainer {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    if (bitmap != null) {
                        Image(
                            painter = BitmapPainter(bitmap.asImageBitmap()),
                            contentDescription = null,
                            modifier = Modifier.size(100.dp),
                            contentScale = ContentScale.FillBounds
                        )
                        Spacer(Modifier.height(7.dp))
                    }
                    Text(
                        text = formatAgricultureResponse(message.text),
                        color = if (message.isUser) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        fontSize = 15.sp
                    )
                }
            }
        }
    }
}

fun formatAgricultureResponse(raw: String): String {
    var formatted = raw.trim()

    // 1. Headings like *1. Choosing the Right Seed & Time:* → keep number + bold-like uppercase
    formatted = formatted.replace(Regex("\\*\\s*(\\d+\\.\\s.*?):\\*")) {
        "\n\n${it.groupValues[1].uppercase()}\n"
    }

    // 2. Bullet points "-" → "• "
    formatted = formatted.replace(Regex("(?m)^\\s*-\\s")) { "• " }

    // 3. Remove extra "*" around emphasis (just plain text)
    formatted = formatted.replace(Regex("\\*(.*?)\\*")) {
        it.groupValues[1]
    }

    // 4. Collapse multiple newlines → max two
    formatted = formatted.replace(Regex("\n{3,}"), "\n\n")

    return formatted
}

