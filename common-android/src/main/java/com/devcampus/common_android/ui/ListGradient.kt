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

/**
 * Draw gradients on top of composable.
 * @param height optional height of the target view. Used when gradient needs to be placed higher than the view bottom
 * (e.g. when drawing on partially expanded bottom sheet)
 */
@Composable
fun Modifier.drawListGradient(height: Float? = null) = composed {

    val scrimColor = colorsScheme().surface

    drawWithCache {

        val y = height ?: size.height

        val selectionGradient = Brush.linearGradient(
            colors = listOf(scrimColor, Color.Transparent),
            start = Offset(0f, y),
            end = Offset(0f, y - size.height / 6f)
        )

        onDrawWithContent {
            drawContent()
            drawRect(selectionGradient, blendMode = BlendMode.Multiply)
        }
    }
}

