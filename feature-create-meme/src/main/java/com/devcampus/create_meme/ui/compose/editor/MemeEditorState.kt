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

        // Check if dragging selected item
        selectedItem?.containsPosition(offset) { decor ->
            dragItem = selectedItem
            return
        }

        decorItems.filterPlaced().onEach { decor ->
            decor.containsPosition(offset) {
                dragItem = decor
                return
            }
        }
    }

    fun onDrag(dragOffset: Offset) {
        dragItem?.let { dragItem ->

            requireNotNull(dragItem.topLeft)
            requireNotNull(dragItem.size)

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

            requireNotNull(decor.topLeft)

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

        decorItems
            .filterPlaced()
            .filter { it.id != selectedItem?.id }
            .forEach { decor ->
                decor.containsPosition(offset) {
                    // Other item selected. Save changes and switch to new item
                    selectedItem?.let { confirmChanges() }
                    isInTextEditMode = false
                    selectedItem = decor
                    return
                }
            }

        // Tap outside. Save changes and clear selection
        selectedItem?.let { confirmChanges() }

        // Close text edit mode
        isInTextEditMode = false
    }

    fun onDoubleTap(offset: Offset) {
        if (!isInTextEditMode) {
            decorItems
                .filterPlaced()
                .forEach { decor ->
                    decor.containsPosition(offset) {

                        if (selectedItem != decor) {
                            selectedItem?.let { confirmChanges() }
                            selectedItem = decor
                        }

                        isInTextEditMode = true

                        return
                    }
                }
        }
    }
    /**
     * Call 'block' if delete button is tapped
     */
    private inline fun MemeDecor.onTapDelete(offset: Offset, block: (MemeDecor) -> Unit) {

        if (topLeft == null || size == null) return

        val left = topLeft.x + size.width + borderMargin - deleteIconSize / 2f
        val top = topLeft.y - borderMargin - deleteIconSize / 2f

        val deleteButtonRect: Rect = Rect(left, top, left + deleteIconSize, top + deleteIconSize)

        if(deleteButtonRect.contains(offset)) {
            block(this)
        }
    }

    /**
     * Call 'block' if item is tapped
     */
    private inline fun MemeDecor.containsPosition(offset: Offset, block: (MemeDecor) -> Unit) {

        if (topLeft == null || size == null) return

        val decorRect = Rect(offset = topLeft, size = size).inflate(2 * borderMargin)

        if(decorRect.contains(offset)) {
            block(this)
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
        selectedItem?.let { selectedItem ->

            decorItems.find { it.id == selectedItem.id }?.let { currentItem ->
                if (currentItem.type != selectedItem.type) {
                    onDecorUpdated(selectedItem)
                }
            }

            this@MemeEditorState.selectedItem = null
        }
    }

    /**
     * Set new font.
     * Update decor size and position for new font selection
     */
    fun setFont(font: MemeFontFamily) {
        selectedItem?.let { decor ->

            requireNotNull(decor.topLeft)
            requireNotNull(decor.size)

            val decorType = (decor.type as? DecorType.TextDecor) ?: return

            val oldSize = decor.size
            val newSize = textMeasurer.measure(
                text = decorType.text,
                style = TextStyle.Default.copy(
                    fontFamily = font.fontFamily,
                    fontSize = font.baseFontSize * decorType.fontScale
                )
            ).size.toSize()

            val topLeft = Offset(
                x = decor.topLeft.x - (newSize.width - oldSize.width) / 2f,
                y = decor.topLeft.y - (newSize.height - oldSize.height) / 2f,
            )

            selectedItem = decor.copy(
                type = decorType.copy(fontFamily = font),
                topLeft = topLeft,
                size = newSize,
            )
        }
    }

    fun setFontScale(scale: Float) {
        selectedItem?.let { decor ->

            requireNotNull(decor.topLeft)
            requireNotNull(decor.size)
            requireNotNull(canvasSize)

            val decorType = (decor.type as? DecorType.TextDecor) ?: return

            val oldSize = decor.size
            val newSize = textMeasurer.measure(
                text = decorType.text,
                style = TextStyle.Default.copy(
                    fontFamily = decorType.fontFamily.fontFamily,
                    fontSize = decorType.fontFamily.baseFontSize * scale
                )
            ).size.toSize()

            val topLeft = Offset(
                x = decor.topLeft.x - (newSize.width - oldSize.width) / 2f,
                y = decor.topLeft.y - (newSize.height - oldSize.height) / 2f,
            )

            if (topLeft.x < borderMargin || topLeft.y < borderMargin) return
            if (topLeft.x + newSize.width > canvasSize!!.width - borderMargin) return

            selectedItem = decor.copy(
                type = decorType.copy(fontScale = scale),
                topLeft = topLeft,
                size = newSize,
            )
        }
    }

    fun setFontColor(color: Color) {
        selectedItem?.let { decor ->

            val decorType = (decor.type as? DecorType.TextDecor) ?: return

            selectedItem = decor.copy(
                type = decorType.copy(fontColor = color),
            )
        }
    }

    fun onTextChange(string: String): Boolean {
        selectedItem?.let { decor ->

            val textDecor = (decor.type as? DecorType.TextDecor) ?: return false

            requireNotNull(decor.topLeft)
            requireNotNull(decor.size)
            requireNotNull(canvasSize)

            val oldSize = decor.size
            val newSize = textMeasurer.measure(
                text = string,
                style = TextStyle.Default.copy(
                    fontFamily = textDecor.fontFamily.fontFamily,
                    fontSize = textDecor.fontFamily.baseFontSize * textDecor.fontScale
                )
            ).size.toSize()

            val topLeft = Offset(
                x = decor.topLeft.x - (newSize.width - oldSize.width) / 2f,
                y = decor.topLeft.y - (newSize.height - oldSize.height) / 2f,
            )

            if (topLeft.x < borderMargin || topLeft.y < borderMargin) return false
            if (topLeft.x + newSize.width > canvasSize!!.width - borderMargin) return false

            selectedItem = decor.copy(
                type = textDecor.copy(text = string),
                topLeft = topLeft,
                size = newSize,
            )

            return true
        }

        return false
    }

    fun finishEditMode() {
        selectedItem?.let { decor ->

            val textDecor = (decor.type as? DecorType.TextDecor) ?: return

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

internal fun List<MemeDecor>.filterPlaced() = filter { decor ->
    decor.topLeft != null && decor.size != null
}
