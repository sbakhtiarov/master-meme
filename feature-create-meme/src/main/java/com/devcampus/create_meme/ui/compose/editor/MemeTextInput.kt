package com.devcampus.create_meme.ui.compose.editor

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.devcampus.create_meme.ui.editor.MemeEditorState
import com.devcampus.create_meme.ui.model.UiDecorType

/**
 * Place TextField on top of the decoration for handling text input.
 * Text is passed to the [MemeEditorState].
 */
@Composable
internal fun MemeTextInput(
    editor: MemeEditorState,
    imagePosition: LayoutCoordinates?
) {
    editor.selectedItem?.let { decor ->

        val textDecor = decor.type as UiDecorType.TextUiDecor

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
                // Only update value if editor allows adding more characters
                if (editor.onTextChange(it.text)) {
                    value = it
                }
            },
            keyboardActions = KeyboardActions(
                onDone = {
                    editor.finishEditMode()
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

        // Focus on the TextField to open keyboard
        LaunchedEffect(Unit) { focusRequester.requestFocus() }
    }
}
