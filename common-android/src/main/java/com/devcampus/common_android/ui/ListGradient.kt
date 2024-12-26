package com.devcampus.common_android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.devcampus.common_android.ui.theme.colorsScheme

@Composable
fun Modifier.drawListGradient() = composed {

    val scrimColor = colorsScheme().surfaceContainerLowest

    drawWithCache {
        val selectionGradient = Brush.linearGradient(
            colors = listOf(scrimColor, Color.Transparent),
            start = Offset(0f, size.height),
            end = Offset(0f, size.height - size.height / 6f)
        )

        onDrawWithContent {
            drawContent()
            drawRect(selectionGradient, blendMode = BlendMode.Multiply)
        }
    }
}

