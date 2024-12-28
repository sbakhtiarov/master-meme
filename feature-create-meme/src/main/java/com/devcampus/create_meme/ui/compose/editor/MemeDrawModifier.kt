package com.devcampus.create_meme.ui.compose.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import com.devcampus.create_meme.ui.editor.EditorProperties
import com.devcampus.create_meme.ui.model.UiDecor
import com.devcampus.create_meme.ui.model.UiDecorType

@Composable
fun Modifier.drawMemeDecor(
    selectedItem: UiDecor?,
    dragItem: UiDecor?,
    decorItems: List<UiDecor>,
    editorProperties: EditorProperties,
    isInTextEditMode: Boolean,
): Modifier {

    val textMeasurer = rememberTextMeasurer()

    return this then Modifier.drawWithContent {

        drawContent()

        // Draw dragged item if it is not selected
        if (dragItem?.id != selectedItem?.id) {
            dragItem?.let { decor ->
                drawAnimatedDecor(
                    animatedDecor = decor,
                    properties = editorProperties,
                    drawDeleteButton = false,
                    drawBorder = false,
                    textMeasurer = textMeasurer
                )
            }
        }

        // Draw selected item
        selectedItem?.let { decor ->
            drawAnimatedDecor(
                animatedDecor = decor,
                properties = editorProperties,
                drawDeleteButton = isInTextEditMode.not(),
                textMeasurer = textMeasurer,
                drawBorder = true,
            )
        }

        // Draw all other items
        decorItems
            .filter { it.id != dragItem?.id }
            .filter { it.id != selectedItem?.id }
            .forEach { decor -> drawDecor(
                decor = decor,
                properties = editorProperties,
                textMeasurer = textMeasurer,
                drawDeleteButton = false,
                drawBorder = false,
            ) }
    }
}

private fun DrawScope.drawAnimatedDecor(
    animatedDecor: UiDecor,
    properties: EditorProperties,
    drawDeleteButton: Boolean = true,
    drawBorder: Boolean,
    textMeasurer: TextMeasurer,
) {

    val decor = if (animatedDecor.animatedOffset != null) {
        animatedDecor.copy(topLeft = animatedDecor.animatedOffset.value)
    } else {
        animatedDecor
    }

    drawDecor(decor, properties, drawDeleteButton, drawBorder, textMeasurer, animatedDecor.animatedAlpha.value)
}

private fun DrawScope.drawDecor(
    decor: UiDecor,
    properties: EditorProperties,
    drawDeleteButton: Boolean = true,
    drawBorder: Boolean,
    textMeasurer: TextMeasurer,
    alpha: Float = 1f
) {

    if (drawBorder) {

        drawDecorBorder(decor, properties, alpha)

        if (drawDeleteButton) {
            drawDeleteButton(decor, properties, alpha)
        }
    }

    drawDecorType(decor, textMeasurer, alpha)
}

private fun DrawScope.drawDecorBorder(
    decor: UiDecor,
    properties: EditorProperties,
    alpha: Float,
) {
    val borderRectSize = decor.size.copy(
        width = decor.size.width + 2 * properties.borderMargin,
        height = decor.size.height + 2 * properties.borderMargin
    )

    drawRoundRect(
        color = properties.borderColor.copy(alpha),
        topLeft = decor.topLeft.minus(Offset(properties.borderMargin, properties.borderMargin)),
        size = borderRectSize,
        cornerRadius = properties.borderCornerRadius,
        style = Stroke(width = 2f)
    )

    drawRoundRect(
        color = Color.Black.copy(alpha),
        topLeft = decor.topLeft.minus(Offset(properties.borderMargin, properties.borderMargin)),
        size = borderRectSize,
        cornerRadius = properties.borderCornerRadius,
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
    decor: UiDecor,
    properties: EditorProperties,
    alpha: Float,
) {
    with(properties.deleteIconPainter) {
        translate(
            left = decor.topLeft.x + decor.size.width + properties.borderMargin - properties.deleteIconSize / 2f,
            top = decor.topLeft.y - properties.borderMargin - properties.deleteIconSize / 2f,
        ) {
            draw(
                size = Size(properties.deleteIconSize, properties.deleteIconSize),
                alpha = alpha
            )
        }
    }
}

private fun DrawScope.drawDecorType(
    decor: UiDecor,
    textMeasurer: TextMeasurer,
    alpha: Float,
) {
    when (decor.type) {
        is UiDecorType.TextUiDecor -> {

            val style = TextStyle.Default.copy(
                fontFamily = decor.type.fontFamily.fontFamily,
                fontSize = decor.type.fontFamily.baseFontSize * decor.type.fontScale
            )

            val layoutResult = textMeasurer.measure(decor.type.text, style)

            drawText(
                textLayoutResult = layoutResult,
                color = decor.type.fontColor.copy(alpha = alpha),
                topLeft = decor.topLeft,
                drawStyle = Fill,
            )

            if (decor.type.fontFamily.isStroke) {
                drawText(
                    textLayoutResult = layoutResult,
                    color = decor.type.strokeColor.copy(alpha = alpha),
                    topLeft = decor.topLeft,
                    drawStyle = Stroke(width = 4f),
                )
            }
        }
    }
}
