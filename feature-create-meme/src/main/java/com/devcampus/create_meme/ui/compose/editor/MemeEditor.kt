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
import com.devcampus.create_meme.ui.model.DecorType

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MemeEditor(
    templatePath: String,
    state: MemeEditorState,
    savedMemePath: String,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
) {

    var imagePosition by remember { mutableStateOf<LayoutCoordinates?>(null) }

    val touchAndDragHandler = rememberTouchAndDragHandler(state)

    Box(
        modifier = modifier,
    ) {
        with (sharedTransitionScope) {
            AsyncImage(
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "meme-${savedMemePath}"),
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
            state.selectedItem?.let { decor ->

                requireNotNull(decor.topLeft)
                requireNotNull(decor.size)

                val textDecor = decor.type as DecorType.TextDecor

                var value by remember {
                    mutableStateOf(
                        TextFieldValue(
                            text = textDecor.text,
                            selection = TextRange(0, textDecor.text.length)
                        )
                    )
                }

                val focusRequester = remember { FocusRequester() }

                val fieldOffset = with(LocalDensity.current) { 16.dp.toPx() }.toInt()

                val size = with(LocalDensity.current) {
                    DpSize(
                        width = decor.size.width.toDp() + 32.dp,
                        height = decor.size.height.toDp() + 32.dp
                    )
                }

                TextField(
                    modifier = Modifier
                        .focusRequester(focusRequester)
                        .offset {
                            val x = decor.topLeft.x.toInt()
                            val y = decor.topLeft.y.toInt()

                            IntOffset(
                                x = x - fieldOffset,
                                y = y + (imagePosition?.positionInParent()?.y?.toInt() ?: 0) - fieldOffset
                            )
                        }
                        .size(size),
                    value = value,
                    onValueChange = {
                        if (state.onTextChange(it.text)) {
                            value = it
                        }
                    },
                    keyboardActions = KeyboardActions(
                        onDone = {
                            state.finishEditMode()
                        }
                    ),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        capitalization = KeyboardCapitalization.Characters
                    ),
                    singleLine = true,
                    textStyle = TextStyle.Default.copy(
                        fontFamily = decor.type.fontFamily.fontFamily,
                        fontSize = decor.type.fontFamily.baseFontSize * decor.type.fontScale,
                        color = Color.Transparent,
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = Color.Transparent,
                        unfocusedTextColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        errorContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    )
                )

                LaunchedEffect(Unit) { focusRequester.requestFocus() }
            }
        }
    }
}
