package com.devcampus.create_meme.ui.editor

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import com.devcampus.create_meme.ui.model.UiDecor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class TouchAndDragHandler(
    private val editor: MemeEditorState,
    private val animationScope: CoroutineScope,
) {

    private val properties = editor.properties

    var dragItem by mutableStateOf<UiDecor?>(null)
        private set

    fun onDragStart(offset: Offset) {

        if (dragItem != null) return

        with (editor) {
            dragItem = selectedItem?.takeIf { it.containsPosition(offset) }
                ?: editor.decorItems.find { it.containsPosition(offset) }
        }
    }

    fun onDrag(dragOffset: Offset) {
        dragItem?.let { dragItem ->

            with (properties) {

                val newTopLeft = dragItem.topLeft + dragOffset

                this@TouchAndDragHandler.dragItem = dragItem.copy(topLeft = newTopLeft)

                editor.selectedItem?.let { selectedItem ->
                    if (selectedItem.id == dragItem.id) {
                        // Update selected item if dragging it
                        editor.selectedItem = selectedItem.copy(
                            topLeft = newTopLeft
                        )
                    }
                }
            }
        }
    }

    fun onDragEnd() {
        dragItem?.let { dragItem ->

            val canvasAdjustedOffset =  dragItem.coerceInCanvas()

            if (canvasAdjustedOffset != dragItem.topLeft) {
                animateDecorPosition(dragItem, canvasAdjustedOffset) {

                    editor.selectedItem?.let { selectedItem ->
                        if (selectedItem.id == dragItem.id) {
                            editor.selectedItem = selectedItem.copy(
                                topLeft = canvasAdjustedOffset,
                                animatedOffset = null,
                            )
                        }
                    }

                    editor.onDecorMoved(dragItem.id, canvasAdjustedOffset)
                    this@TouchAndDragHandler.dragItem = null
                }
            } else {
                editor.onDecorMoved(dragItem.id, dragItem.topLeft)
                this@TouchAndDragHandler.dragItem = null
            }
        }
    }

    fun onDragCancel() {
        dragItem = null
    }


    private fun animateDecorPosition(decor: UiDecor, newOffset: Offset, onComplete: () -> Unit) {

        val offset = Animatable(decor.topLeft, Offset.VectorConverter)

        dragItem = dragItem?.copy(animatedOffset = offset)

        if (editor.selectedItem?.id == dragItem?.id) {
            editor.selectedItem = editor.selectedItem?.copy(
                animatedOffset = offset
            )
        }

        animationScope.launch {
            offset.animateTo(
                targetValue = newOffset,
                animationSpec = spring()
            )
        }.invokeOnCompletion {
            onComplete()
        }
    }

    private fun UiDecor.coerceInCanvas(): Offset {

        val left = topLeft.x.coerceIn(
            minimumValue = properties.borderMargin,
            maximumValue = (editor.canvasSize.width.toFloat() - size.width - properties.borderMargin).coerceAtLeast(properties.borderMargin)
        )

        val top = topLeft.y.coerceIn(
            minimumValue = properties.borderMargin,
            maximumValue = editor.canvasSize.height.toFloat() - size.height - properties.borderMargin
        )

        return Offset(left, top)
    }

    fun onTap(offset: Offset) {
        with (editor) {
            selectedItem?.onTapDelete(offset) { decor ->
                animationScope.launch {
                    editor.selectedItem?.animatedAlpha?.animateTo(
                        targetValue = 0f,
                        animationSpec = tween(200)
                    )
                }.invokeOnCompletion {
                    onDeleteClick(decor.id)
                    editor.selectedItem = null
                }
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

    private fun UiDecor.isTapOnDeleteButton(offset: Offset): Boolean {
        with (properties) {
            val left = topLeft.x + size.width + borderMargin - deleteIconSize / 2f
            val top = topLeft.y - borderMargin - deleteIconSize / 2f

            val deleteButtonRect: Rect = Rect(left, top, left + deleteIconSize, top + deleteIconSize)

            return deleteButtonRect.contains(offset)
        }
    }

    private inline fun UiDecor.onTapDelete(offset: Offset, block: (UiDecor) -> Unit) {
        if (isTapOnDeleteButton(offset)) {
            block(this)
        }
    }

    private fun UiDecor.containsPosition(offset: Offset): Boolean {
        val decorRect = Rect(offset = topLeft, size = size).inflate(2 * properties.borderMargin)
        return decorRect.contains(offset)
    }

    private inline fun UiDecor.containsPosition(offset: Offset, block: (UiDecor) -> Unit) {
        if(containsPosition(offset)) {
            block(this)
        }
    }
}

@Composable
fun rememberTouchAndDragHandler(
    editorState: MemeEditorState
): TouchAndDragHandler {
    val scope = rememberCoroutineScope()
    return remember(editorState) {
        TouchAndDragHandler(
            editor = editorState,
            animationScope = scope,
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

