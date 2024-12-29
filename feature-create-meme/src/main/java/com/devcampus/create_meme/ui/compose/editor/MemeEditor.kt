package com.devcampus.create_meme.ui.compose.editor

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import coil3.compose.AsyncImage
import com.devcampus.create_meme.ui.editor.MemeEditorState
import com.devcampus.create_meme.ui.editor.handleDragEvents
import com.devcampus.create_meme.ui.editor.handleTapEvents
import com.devcampus.create_meme.ui.editor.rememberTouchAndDragHandler
import com.devcampus.create_meme.ui.model.UiDecorType

/**
 * Draw meme template image and decorations
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MemeEditor(
    templatePath: String,
    state: MemeEditorState,
    memePath: String,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {

    // Save position of the meme image to place TextField correctly for text input
    var imagePosition by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val touchAndDragHandler = rememberTouchAndDragHandler(state)

    Box(modifier = modifier) {
        with (sharedTransitionScope) {
            AsyncImage(
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "meme-${memePath}"),
                        animatedVisibilityScope = animatedContentScope
                    )
                    .align(Alignment.Center)
                    .fillMaxWidth()
                    .onSizeChanged { size ->
                        state.canvasSize = size.toSize()
                    }
                    .onPlaced { position ->
                        imagePosition = position
                    }
                    .drawMemeDecor(
                        selectedItem = state.selectedItem,
                        dragItem = touchAndDragHandler.dragItem,
                        decorItems = state.decorItems,
                        editorProperties = state.properties,
                        isInTextEditMode = state.isInTextEditMode,
                    )
                    .handleTapEvents(touchAndDragHandler)
                    .handleDragEvents(touchAndDragHandler),
                model = templatePath,
                contentDescription = null,
            )
        }

        if (state.isInTextEditMode) {
            MemeTextInput(state, imagePosition)
        }
    }
}
