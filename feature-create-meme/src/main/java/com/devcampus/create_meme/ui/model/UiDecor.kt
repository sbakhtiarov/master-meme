package com.devcampus.create_meme.ui.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import com.devcampus.create_meme.ui.common.MemeColors
import com.devcampus.create_meme.ui.common.MemeFontFamily
import com.devcampus.create_meme.ui.common.MemeFonts

data class UiDecor(
    val id: String,
    val type: UiDecorType,
    val topLeft: Offset,
    val size: Size,

    // For animations
    val animatedOffset: Animatable<Offset, AnimationVector2D>? = null,
    val animatedAlpha: Animatable<Float, AnimationVector1D> = Animatable(1f),
)

sealed interface UiDecorType {
    data class TextUiDecor(
        val text: String,
        val strokeColor: Color = Color.Black,
        val fontFamily: MemeFontFamily = DefaultFontFamily,
        val fontScale: Float = 1f,
        val fontColor: Color = MemeColors.colors[0]
    ) : UiDecorType {

        companion object {
            val DefaultFontFamily: MemeFontFamily = MemeFonts.fonts[0]
        }
    }
}

fun UiDecor.textDecor(): UiDecorType.TextUiDecor? = this.type as? UiDecorType.TextUiDecor