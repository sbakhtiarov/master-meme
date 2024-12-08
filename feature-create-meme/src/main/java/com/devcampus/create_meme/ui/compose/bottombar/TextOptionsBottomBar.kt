package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.conditional
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.compose.editor.MemeFontFamily
import com.devcampus.create_meme.ui.model.MemeDecor

@Composable
fun TextOptionsBottomBar(
    decor: MemeDecor,
    onFontSelected: (MemeFontFamily) -> Unit,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {

    var selectedButton by remember(decor.id) { mutableStateOf<EditButton?>(null) }

    fun toggleSelection(button: EditButton) {
        selectedButton = if (selectedButton == button) {
            null
        } else {
            button
        }
    }

    Column {
        AnimatedContent(
            targetState = selectedButton,
            transitionSpec = {
                (fadeIn(animationSpec = tween(220, delayMillis = 90)) +
                        slideInVertically(initialOffsetY = { it }, animationSpec = tween(220, delayMillis = 90)))
                    .togetherWith(fadeOut(animationSpec = tween(220)) +
                            slideOutVertically(targetOffsetY = { it }, animationSpec = tween(220)))
            },
            label = "text style bars"
        ) { button ->
            when (button) {
                EditButton.STYLE -> TextStyleBar(
                    decor = decor,
                    onFontSelected = onFontSelected
                )
                EditButton.SIZE -> TextSizeBar()
                EditButton.COLOR -> TextColorBar()
                null -> Box(Modifier.fillMaxWidth())
            }
        }

        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Icon(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onCancel() }
                        .padding(10.dp),
                    imageVector = Icons.Default.Close,
                    contentDescription = null
                )

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f))

                Icon(
                    modifier = Modifier
                        .optionButton(
                            isSelected = { selectedButton == EditButton.STYLE },
                            onClick = { toggleSelection(EditButton.STYLE) }
                        ),
                    painter = painterResource(R.drawable.ic_text_style),
                    contentDescription = null
                )

                Icon(
                    modifier = Modifier
                        .optionButton(
                            isSelected = { selectedButton == EditButton.SIZE },
                            onClick = { toggleSelection(EditButton.SIZE) }
                        ),
                    painter = painterResource(R.drawable.ic_text_size),
                    contentDescription = null
                )

                Icon(
                    modifier = Modifier
                        .optionButton(
                            isSelected = { selectedButton == EditButton.COLOR },
                            onClick = { toggleSelection(EditButton.COLOR) }
                        ),
                    painter = painterResource(R.drawable.ic_color_picker),
                    contentDescription = null,
                    tint = Color.Unspecified
                )

                Spacer(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f))

                Icon(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onConfirm() }
                        .padding(10.dp),
                    imageVector = Icons.Default.Check,
                    contentDescription = null
                )
            }
        }
    }
}

private enum class EditButton {
    STYLE, SIZE, COLOR
}

@Composable
private fun Modifier.optionButton(
    isSelected: () -> Boolean,
    onClick: () -> Unit,
) : Modifier {
    return this then Modifier.size(44.dp)
        .conditional(isSelected()) {
            Modifier.background(
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                shape = RoundedCornerShape(12.dp)
            )
        }
        .clip(RoundedCornerShape(12.dp))
        .clickable { onClick() }
}
