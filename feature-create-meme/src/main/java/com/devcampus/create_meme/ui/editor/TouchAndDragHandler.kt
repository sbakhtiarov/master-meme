package com.devcampus.create_meme.ui.editor

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import com.devcampus.create_meme.ui.model.MemeDecor

class TouchAndDragHandler(
    private val editor: MemeEditorState,
) {

    private val properties = editor.properties

    var dragItem by mutableStateOf<MemeDecor?>(null)
        private set

    fun onDragStart(offset: Offset) {
        with (editor) {
            dragItem = selectedItem?.takeIf { it.containsPosition(offset) }
                ?: editor.decorItems.find { it.containsPosition(offset) }
        }
    }

    fun onDrag(dragOffset: Offset) {

        val canvasSize = editor.canvasSize

        dragItem?.let { dragItem ->

            with (properties) {

                val newTopLeft = dragItem.topLeft + dragOffset

                val left = newTopLeft.x.coerceIn(
                    minimumValue = borderMargin,
                    maximumValue = (canvasSize.width.toFloat() - dragItem.size.width - borderMargin).coerceAtLeast(borderMargin)
                )

                val top = newTopLeft.y.coerceIn(
                    minimumValue = borderMargin,
                    maximumValue = canvasSize.height.toFloat() - dragItem.size.height - borderMargin
                )

                this@TouchAndDragHandler.dragItem = dragItem.copy(topLeft = Offset(left, top))

                editor.selectedItem?.let { selectedItem ->
                    if (selectedItem.id == dragItem.id) {
                        // Update selected item if dragging it
                        editor.selectedItem = selectedItem.copy(
                            topLeft = Offset(left, top)
                        )
                    }
                }
            }
        }
    }

    fun onDragEnd() {
        dragItem?.let { decor ->
            editor.onDecorMoved(decor.id, decor.topLeft)
            dragItem = null
        }
    }

    fun onDragCancel() {
        dragItem = null
    }

    fun onTap(offset: Offset) {
        with (editor) {
            selectedItem?.onTapDelete(offset) { decor ->
                onDeleteClick(decor.id)
                editor.selectedItem = null
                return
            }

            selectedItem?.containsPosition(offset) { return }

            selectedItem?.let { confirmChanges() }

            val decor = decorItems.find { it.containsPosition(offset) }

            if (decor != null) {
                editor.selectedItem = decor
            }

            isInTextEditMode = false
        }
    }

    fun onDoubleTap(offset: Offset) {
        with (editor) {
            if (!isInTextEditMode) {

                selectedItem?.containsPosition(offset) { decor ->
                    selectedItem?.let { confirmChanges() }
                    editor.selectedItem = decor
                    isInTextEditMode = true
                    return
                }

                decorItems.find { it.containsPosition(offset) }?.let { decor ->
                    editor.selectedItem = decor
                    isInTextEditMode = true
                }
            }
        }
    }

    private fun MemeDecor.isTapOnDeleteButton(offset: Offset): Boolean {
        with (properties) {
            val left = topLeft.x + size.width + borderMargin - deleteIconSize / 2f
            val top = topLeft.y - borderMargin - deleteIconSize / 2f

            val deleteButtonRect: Rect = Rect(left, top, left + deleteIconSize, top + deleteIconSize)

            return deleteButtonRect.contains(offset)
        }
    }

    private inline fun MemeDecor.onTapDelete(offset: Offset, block: (MemeDecor) -> Unit) {
        if (isTapOnDeleteButton(offset)) {
            block(this)
        }
    }

    private fun MemeDecor.containsPosition(offset: Offset): Boolean {
        val decorRect = Rect(offset = topLeft, size = size).inflate(2 * properties.borderMargin)
        return decorRect.contains(offset)
    }

    private inline fun MemeDecor.containsPosition(offset: Offset, block: (MemeDecor) -> Unit) {
        if(containsPosition(offset)) {
            block(this)
        }
    }
}

@Composable
fun rememberTouchAndDragHandler(
    editorState: MemeEditorState
): TouchAndDragHandler {
    return remember(editorState) {
        TouchAndDragHandler(
            editor = editorState,
        )
    }
}

fun Modifier.handleTapEvents(handler: TouchAndDragHandler) = composed {
    pointerInput(Unit) {
        detectTapGestures(
            onTap = { position -> handler.onTap(position) },
            onDoubleTap = { position -> handler.onDoubleTap(position) }
        )
    }
}

fun Modifier.handleDragEvents(handler: TouchAndDragHandler) = composed {
    pointerInput(Unit) {
        detectDragGestures(
            onDragStart = { offset ->
                handler.onDragStart(offset)
            },
            onDragEnd = {
                handler.onDragEnd()
            },
            onDrag = { pointerInputChange, offset ->
                handler.onDrag(offset)
            },
            onDragCancel = {
                handler.onDragCancel()
            }
        )
    }
}
