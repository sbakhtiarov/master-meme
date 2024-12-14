package com.devcampus.create_meme.domain

import androidx.compose.ui.geometry.Offset

sealed class Decor(val topLeft: Offset) {
    class TextDecor(
        topLeft: Offset,
        val text: String,
        val fontResId: Int,
        val fontSize: Float,
        val color: Int,
        val isStroke: Boolean,
    ) : Decor(topLeft)

}
