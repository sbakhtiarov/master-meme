package com.devcampus.create_meme.ui.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.devcampus.create_meme.ui.compose.editor.Fonts

data class MemeDecor(
    val id: String,
    val type: DecorType,
    val topLeft: Offset? = null,
    val size: Size? = null,
)

sealed interface DecorType {
    data class TextDecor(
        val text: String,
        val color: Color = Color.White,
        val strokeColor: Color = Color.Black,
        val fontSize: TextUnit = DefaultFontSize,
        val fontFamily: FontFamily = DefaultFontFamily,
        val isStroke: Boolean = true,
    ) : DecorType {

        companion object {
            val DefaultFontSize: TextUnit = 38.sp
            val DefaultFontFamily: FontFamily = Fonts.impactFontFamily
        }

    }
}
