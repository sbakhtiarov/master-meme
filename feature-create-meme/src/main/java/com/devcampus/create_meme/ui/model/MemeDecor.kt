package com.devcampus.create_meme.ui.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

data class MemeDecor(
    val id: String,
    val type: DecorType,
    val topLeft: Offset? = null,
    val size: Size? = null,
)

sealed interface DecorType {
    data class TextDecor(val text: String, val fontSize: TextUnit = 20.sp) : DecorType
}
