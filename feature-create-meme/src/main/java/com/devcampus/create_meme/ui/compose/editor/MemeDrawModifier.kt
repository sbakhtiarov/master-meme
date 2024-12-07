package com.devcampus.create_meme.ui.compose.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.MemeDecor

@Composable
fun Modifier.drawMemeDecor(
    state: MemeEditorState
): Modifier {

    return this then Modifier.drawWithContent {

        drawContent()

        state.dragItem?.let { decor ->
            if (decor.id == state.selectedItem?.id) {
                drawDecorBorder(decor, state)
                drawDeleteButton(decor, state)
            }
            drawDecor(decor, state)
        }

        state.decorItems
            .filterPlaced()
            .filter { it.id != state.dragItem?.id }
            .forEach { decor ->

                if (decor.id == state.selectedItem?.id) {
                    drawDecorBorder(decor, state)
                    drawDeleteButton(decor, state)
                }

                drawDecor(decor, state)
            }
    }
}

private fun DrawScope.drawDecorBorder(
    decor: MemeDecor,
    state: MemeEditorState,
) {
    requireNotNull(decor.topLeft)
    requireNotNull(decor.size)

    val borderRectSize = decor.size.copy(
        width = decor.size.width + 2 * state.borderMargin,
        height = decor.size.height + 2 * state.borderMargin
    )

    drawRoundRect(
        color = state.borderColor,
        topLeft = decor.topLeft.minus(Offset(state.borderMargin, state.borderMargin)),
        size = borderRectSize,
        cornerRadius = state.borderCornerRadius,
        style = Stroke(
            width = 2f,
            pathEffect = PathEffect.dashPathEffect(
                intervals = floatArrayOf(10f, 10f),
                phase = 0f
            )
        )
    )
}

private fun DrawScope.drawDeleteButton(
    decor: MemeDecor,
    state: MemeEditorState,
) {

    requireNotNull(decor.topLeft)
    requireNotNull(decor.size)

    with(state.deleteIconPainter) {
        translate(
            left = decor.topLeft.x + decor.size.width + state.borderMargin - state.deleteIconSize / 2f,
            top = decor.topLeft.y - state.borderMargin - state.deleteIconSize / 2f,
        ) {
            draw(Size(state.deleteIconSize, state.deleteIconSize))
        }
    }
}

private fun DrawScope.drawDecor(
    decor: MemeDecor,
    state: MemeEditorState,
) {
    requireNotNull(decor.topLeft)
    requireNotNull(decor.size)

    when (decor.type) {
        is DecorType.TextDecor -> {

            val style = TextStyle.Default.copy(
                fontFamily = decor.type.fontFamily,
                fontSize = decor.type.fontSize
            )

            val layoutResult = state.textMeasurer.measure(decor.type.text, style)

            drawText(
                textLayoutResult = layoutResult,
                color = decor.type.color,
                topLeft = decor.topLeft,
                drawStyle = Fill,
            )

            if (decor.type.isStroke) {
                drawText(
                    textLayoutResult = layoutResult,
                    color = decor.type.strokeColor,
                    topLeft = decor.topLeft,
                    drawStyle = Stroke(width = 4f),
                )
            }
        }
    }
}
