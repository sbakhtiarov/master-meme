package com.devcampus.create_meme.ui.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

class EditorProperties(
    val borderMargin: Float,
    val borderCornerRadius: CornerRadius,
    val deleteIconSize: Float,
    val borderColor: Color,
    val deleteIconPainter: VectorPainter,
)

@Composable
fun rememberEditorProperties(
    borderMargin: Dp = 4.dp,
    borderCornerRadius: Dp = 8.dp,
    deleteIconSize: Dp = 24.dp,
    borderColor: Color = Color.White,
    deleteIcon: ImageVector = MemeIcons.DecorDelete,
): EditorProperties {

    val borderMargin = borderMargin.toPx()
    val borderCornerRadius = CornerRadius(borderCornerRadius.toPx())
    val deleteIconSize = deleteIconSize.toPx()

    val deleteIconPainter = rememberVectorPainter(deleteIcon)

    return remember(borderMargin, borderCornerRadius, deleteIconSize) {
        EditorProperties(
            borderMargin = borderMargin,
            borderCornerRadius = borderCornerRadius,
            borderColor = borderColor,
            deleteIconSize = deleteIconSize,
            deleteIconPainter = deleteIconPainter,
        )
    }
}

@Composable
private fun Dp.toPx(): Float = with(LocalDensity.current) { toPx() }
