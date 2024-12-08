package com.devcampus.create_meme.ui.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.devcampus.create_meme.ui.common.MemeColors
import com.devcampus.create_meme.ui.common.MemeFontFamily
import com.devcampus.create_meme.ui.common.MemeFonts

data class MemeDecor(
    val id: String,
    val type: DecorType,
    val topLeft: Offset? = null,
    val size: Size? = null,
)

sealed interface DecorType {
    data class TextDecor(
        val text: String,
        val strokeColor: Color = Color.Black,
        val fontFamily: MemeFontFamily = DefaultFontFamily,
        val fontScale: Float = 1f,
        val fontColor: Color = MemeColors.colors[0]
    ) : DecorType {

        companion object {
            val DefaultFontFamily: MemeFontFamily = MemeFonts.fonts[0]
        }
    }
}
