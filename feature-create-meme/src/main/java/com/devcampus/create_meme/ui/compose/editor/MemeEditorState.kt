package com.devcampus.create_meme.ui.compose.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.MemeDecor
import java.util.UUID

class MemeEditorState(
    val memeTemplatePath: String,
    val decorItems: List<MemeDecor>,
    val textMeasurer: TextMeasurer,
    val borderColor: Color,
    val borderMargin: Float,
    val borderCornerRadius: CornerRadius,
    val deleteIconPainter: VectorPainter,
    val deleteIconSize: Float,
    val onDecorAdded: (MemeDecor) -> Unit,
    val onDeleteClick: (String) -> Unit,
    val onDecorMoved: (String, Offset) -> Unit,
) {

    var dragItem by mutableStateOf<MemeDecor?>(null)
        private set

    var canvasSize: Size? = null

    fun onDragStart(offset: Offset) {
        dragItem = decorItems
            .filterPlaced()
            .find { decor ->

                requireNotNull(decor.topLeft)
                requireNotNull(decor.size)

                val rect = Rect(offset = decor.topLeft, size = decor.size).inflate(2 * borderMargin)

                rect.contains(offset)
            }
    }

    fun onDrag(dragOffset: Offset) {
        dragItem?.let { decor ->

            requireNotNull(decor.topLeft)
            requireNotNull(decor.size)

            canvasSize?.let { canvasSize ->

                val newTopLeft = decor.topLeft + dragOffset

                val left = newTopLeft.x.coerceIn(
                    minimumValue = borderMargin,
                    maximumValue = canvasSize.width.toFloat() - decor.size.width - borderMargin
                )

                val top = newTopLeft.y.coerceIn(
                    minimumValue = borderMargin,
                    maximumValue = canvasSize.height.toFloat() - decor.size.height - borderMargin
                )

                dragItem = decor.copy(topLeft = Offset(left, top))
            }
        }
    }

    fun onDragEnd() {
        dragItem?.let { decor ->

            requireNotNull(decor.topLeft)

            onDecorMoved(decor.id, decor.topLeft)
            dragItem = null
        }
    }

    fun onDragCancel() {
        dragItem = null
    }

    fun onTap(offset: Offset) {
        val decorItem = decorItems.filterPlaced().find { decor ->

            requireNotNull(decor.topLeft)
            requireNotNull(decor.size)

            val left = decor.topLeft.x + decor.size.width + borderMargin - deleteIconSize / 2f
            val top = decor.topLeft.y - borderMargin - deleteIconSize / 2f

            val deleteButtonRect: Rect = Rect(left, top, left + deleteIconSize, top + deleteIconSize)

            deleteButtonRect.contains(offset)
        }

        decorItem?.let { onDeleteClick(decorItem.id) }
    }

    fun addTextDecor(text: String) {
        canvasSize?.let { canvasSize ->

            val size = textMeasurer.measure(
                text = text,
                style = TextStyle.Default.copy(
                    fontFamily = DecorType.TextDecor.DefaultFontFamily,
                    fontSize = DecorType.TextDecor.DefaultFontSize
                )).size

            val decor = MemeDecor(
                id = UUID.randomUUID().toString(),
                type = DecorType.TextDecor(text),
                topLeft = Offset(
                    x = canvasSize.center.x - size.center.x,
                    y = canvasSize.center.y - size.center.y,
                ),
                size = size.toSize(),
            )

            onDecorAdded(decor)
        }

    }
}

@Composable
fun rememberMemeEditorState(
    memeTemplatePath: String,
    decorItems: List<MemeDecor>,
    borderColor: Color = Color.Black,
    borderMargin: Dp = 12.dp,
    borderCornerRadius: Dp = 8.dp,
    deleteIcon: ImageVector = MemeIcons.DecorDelete,
    deleteIconSize: Dp = 24.dp,
    onDecorAdded: (MemeDecor) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDecorMoved: (String, Offset) -> Unit,
): MemeEditorState {

    return MemeEditorState(
        memeTemplatePath = memeTemplatePath,
        decorItems = remember { decorItems },
        textMeasurer = rememberTextMeasurer(),
        borderColor = borderColor,
        borderMargin = borderMargin.toPx(),
        borderCornerRadius = CornerRadius(borderCornerRadius.toPx()),
        deleteIconPainter = rememberVectorPainter(deleteIcon),
        deleteIconSize = deleteIconSize.toPx(),
        onDecorAdded = remember { onDecorAdded },
        onDeleteClick = remember { onDeleteClick },
        onDecorMoved = remember { onDecorMoved },
    )
}

@Composable
private fun Dp.toPx(): Float = with(LocalDensity.current) { toPx() }

internal fun List<MemeDecor>.filterPlaced() = filter { decor ->
    decor.topLeft != null && decor.size != null
}
