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
import androidx.compose.ui.unit.toSize
import com.devcampus.create_meme.ui.common.MemeFontFamily
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.MemeDecor
import com.devcampus.create_meme.ui.model.textDecor
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
    val onDecorUpdated: (MemeDecor) -> Unit,
) {

    var selectedItem by mutableStateOf<MemeDecor?>(null)
        private set

    var dragItem by mutableStateOf<MemeDecor?>(null)
        private set

    var isInTextEditMode by mutableStateOf<Boolean>(false)

    var canvasSize: Size? = null

    fun onDragStart(offset: Offset) {
        dragItem = selectedItem?.takeIf { it.containsPosition(offset) }
            ?: decorItems.find { it.containsPosition(offset) }
    }

    fun onDrag(dragOffset: Offset) {
        dragItem?.let { dragItem ->
            canvasSize?.let { canvasSize ->

                val newTopLeft = dragItem.topLeft + dragOffset

                val left = newTopLeft.x.coerceIn(
                    minimumValue = borderMargin,
                    maximumValue = (canvasSize.width.toFloat() - dragItem.size.width - borderMargin).coerceAtLeast(borderMargin)
                )

                val top = newTopLeft.y.coerceIn(
                    minimumValue = borderMargin,
                    maximumValue = canvasSize.height.toFloat() - dragItem.size.height - borderMargin
                )

                this@MemeEditorState.dragItem = dragItem.copy(topLeft = Offset(left, top))

                selectedItem?.let { selectedItem ->
                    if (selectedItem.id == dragItem.id) {
                        // Update selected item if dragging selected item
                        this@MemeEditorState.selectedItem = selectedItem.copy(
                            topLeft = Offset(left, top)
                        )
                    }
                }
            }
        }
    }

    fun onDragEnd() {
        dragItem?.let { decor ->
            onDecorMoved(decor.id, decor.topLeft)
            dragItem = null
        }
    }

    fun onDragCancel() {
        dragItem = null
    }

    fun onTap(offset: Offset) {

        selectedItem?.onTapDelete(offset) { decor ->
            onDeleteClick(decor.id)
            selectedItem = null
            return
        }

        selectedItem?.containsPosition(offset) { return }

        selectedItem?.let { confirmChanges() }

        val decor = decorItems.find { it.containsPosition(offset) }

        if (decor != null) {
            selectedItem = decor
        }

        isInTextEditMode = false
    }

    fun onDoubleTap(offset: Offset) {
        if (!isInTextEditMode) {
            decorItems.find { it.containsPosition(offset) }?.let { decor ->

                if (selectedItem != decor) {
                    selectedItem?.let { confirmChanges() }
                    selectedItem = decor
                }

                isInTextEditMode = true
            }
        }
    }

    /**
     * Add new text decor
     * Measure size and set position for new decor before adding.
     */
    fun addTextDecor(text: String) {
        canvasSize?.let { canvasSize ->

            val size = textMeasurer.measure(
                text = text,
                style = TextStyle.Default.copy(
                    fontFamily = DecorType.TextDecor.DefaultFontFamily.fontFamily,
                    fontSize = DecorType.TextDecor.DefaultFontFamily.baseFontSize,
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

            selectedItem = decor

            onDecorAdded(decor)
        }
    }

    fun cancelChanges() {
        selectedItem = null
    }

    fun confirmChanges() {
        withSelection { selectedItem ->

            decorItems.find { it.id == selectedItem.id }?.let { currentItem ->
                if (currentItem.type != selectedItem.type) {
                    onDecorUpdated(selectedItem)
                }
            }

            this@MemeEditorState.selectedItem = null
            isInTextEditMode = false
        }
    }

    /**
     * Set new font.
     * Update decor size and position for new font selection
     */
    fun setFont(font: MemeFontFamily) {
        withTextSelection { decor, textDecor ->

            val oldSize = decor.size
            val newSize = decor.measure(withFont = font)

            val topLeft = Offset(
                x = decor.topLeft.x - (newSize.width - oldSize.width) / 2f,
                y = decor.topLeft.y - (newSize.height - oldSize.height) / 2f,
            )

            selectedItem = decor.copy(
                type = textDecor.copy(fontFamily = font),
                topLeft = topLeft,
                size = newSize,
            )
        }
    }

    fun setFontScale(scale: Float) {
        withTextSelection { decor, textDecor ->

            val oldSize = decor.size
            val newSize = decor.measure(withFontScale = scale)

            val topLeft = Offset(
                x = decor.topLeft.x - (newSize.width - oldSize.width) / 2f,
                y = decor.topLeft.y - (newSize.height - oldSize.height) / 2f,
            )

            if (topLeft.x < borderMargin || topLeft.y < borderMargin) return
            if (topLeft.x + newSize.width > canvasSize!!.width - borderMargin) return

            selectedItem = decor.copy(
                type = textDecor.copy(fontScale = scale),
                topLeft = topLeft,
                size = newSize,
            )
        }
    }

    fun setFontColor(color: Color) {
        withTextSelection { decor, textDecor ->
            selectedItem = decor.copy(
                type = textDecor.copy(fontColor = color),
            )
        }
    }

    fun onTextChange(newText: String): Boolean {

        if (isInTextEditMode.not()) return false

        withTextSelection { decor, textDecor ->

            val oldSize = decor.size
            val newSize = decor.measure(withText = newText)

            val topLeft = Offset(
                x = decor.topLeft.x - (newSize.width - oldSize.width) / 2f,
                y = decor.topLeft.y - (newSize.height - oldSize.height) / 2f,
            )

            if (topLeft.x < borderMargin || topLeft.y < borderMargin) return false
            if (topLeft.x + newSize.width > canvasSize!!.width - borderMargin) return false

            selectedItem = decor.copy(
                type = textDecor.copy(text = newText),
                topLeft = topLeft,
                size = newSize,
            )

            return true
        }

        return false
    }

    fun finishEditMode() {
        withTextSelection { decor, textDecor ->

            if (textDecor.text.isEmpty()) {
                onDeleteClick(decor.id)
                selectedItem = null
                isInTextEditMode = false
                return
            }

            confirmChanges()
            isInTextEditMode = false
        }
    }

    private fun MemeDecor.measure(
        withText: String? = null,
        withFont: MemeFontFamily? = null,
        withFontScale: Float? = null,
    ): Size {

        val textDecor = textDecor() ?: error("Can only measure text")

        return textMeasurer.measure(
            text = withText ?: textDecor.text,
            style = TextStyle.Default.copy(
                fontFamily = withFont?.fontFamily ?: textDecor.fontFamily.fontFamily,
                fontSize = textDecor.fontFamily.baseFontSize * (withFontScale ?: textDecor.fontScale),
            )).size.toSize()
    }

    private inline fun withTextSelection(block: (MemeDecor, DecorType.TextDecor) -> Unit) {
        selectedItem?.let { decor ->
            decor.textDecor()?.let { textDecor->
                block(decor, textDecor)
            }
        }
    }

    private inline fun withSelection(block: (MemeDecor) -> Unit) { selectedItem?.let { block(it) } }

    private fun MemeDecor.onTapDelete(offset: Offset): Boolean {

        val left = topLeft.x + size.width + borderMargin - deleteIconSize / 2f
        val top = topLeft.y - borderMargin - deleteIconSize / 2f

        val deleteButtonRect: Rect = Rect(left, top, left + deleteIconSize, top + deleteIconSize)

        return deleteButtonRect.contains(offset)
    }

    private inline fun MemeDecor.onTapDelete(offset: Offset, block: (MemeDecor) -> Unit) {
        if(onTapDelete(offset)) {
            block(this)
        }
    }

    private fun MemeDecor.containsPosition(offset: Offset): Boolean {
        val decorRect = Rect(offset = topLeft, size = size).inflate(2 * borderMargin)
        return decorRect.contains(offset)
    }

    private inline fun MemeDecor.containsPosition(offset: Offset, block: (MemeDecor) -> Unit) {
        if(containsPosition(offset)) {
            block(this)
        }
    }
}

@Composable
fun rememberMemeEditorState(
    memeTemplatePath: String,
    decorItems: List<MemeDecor>,
    borderColor: Color = Color.White,
    borderMargin: Dp = 4.dp,
    borderCornerRadius: Dp = 8.dp,
    deleteIcon: ImageVector = MemeIcons.DecorDelete,
    deleteIconSize: Dp = 24.dp,
    onDecorAdded: (MemeDecor) -> Unit,
    onDeleteClick: (String) -> Unit,
    onDecorMoved: (String, Offset) -> Unit,
    onDecorUpdated: (MemeDecor) -> Unit,
): MemeEditorState {

    val textMeasurer = rememberTextMeasurer()
    val deleteIconPainter = rememberVectorPainter(deleteIcon)
    val borderMargin = borderMargin.toPx()
    val borderCornerRadius = CornerRadius(borderCornerRadius.toPx())
    val deleteIconSize = deleteIconSize.toPx()

    return remember(
        memeTemplatePath,
        decorItems,
        textMeasurer,
        borderColor,
        borderMargin,
        borderCornerRadius,
        deleteIconPainter,
        deleteIconSize
    ) {
        MemeEditorState(
            memeTemplatePath = memeTemplatePath,
            decorItems = decorItems,
            textMeasurer = textMeasurer,
            borderColor = borderColor,
            borderMargin = borderMargin,
            borderCornerRadius = borderCornerRadius,
            deleteIconPainter = deleteIconPainter,
            deleteIconSize = deleteIconSize,
            onDecorAdded = onDecorAdded,
            onDeleteClick = onDeleteClick,
            onDecorMoved = onDecorMoved,
            onDecorUpdated = onDecorUpdated,
        )
    }
}

@Composable
private fun Dp.toPx(): Float = with(LocalDensity.current) { toPx() }
