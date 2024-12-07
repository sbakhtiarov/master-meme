package com.devcampus.create_meme.ui.compose.editor

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.toSize
import coil3.compose.AsyncImage

@Composable
fun MemeEditor(
    state: MemeEditorState,
    modifier: Modifier = Modifier,
) {

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .onSizeChanged { size ->
                    state.canvasSize = size.toSize()
                }
                .drawMemeDecor(state)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { position ->
                            state.onTap(position)
                        },
                        onDoubleTap = { position ->
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            state.onDragStart(offset)
                        },
                        onDragEnd = {
                            state.onDragEnd()
                        },
                        onDrag = { pointerInputChange, offset ->
                            state.onDrag(offset)
                        },
                        onDragCancel = {
                            state.onDragCancel()
                        }
                    )
                },
            model = state.memeTemplatePath,
            contentDescription = null,
        )
    }
}
