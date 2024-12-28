package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.create_meme.ui.common.MemeColors
import com.devcampus.create_meme.ui.model.UiDecorType
import com.devcampus.create_meme.ui.model.UiDecor

@Composable
fun TextColorBar(
    decor: UiDecor,
    onColorSelected: (Color) -> Unit
) {

    val textDecor: UiDecorType.TextUiDecor = decor.type as UiDecorType.TextUiDecor

    val rowState = rememberLazyListState()

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 16.dp),
        state = rowState,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        items(
            items = MemeColors.colors,
        ) { color ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable(
                        enabled = color != textDecor.fontColor,
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                    ) {
                        onColorSelected(color)
                    },
                contentAlignment = Alignment.Center,
            ) {

                androidx.compose.animation.AnimatedVisibility(
                    visible = color == textDecor.fontColor,
                    enter = scaleIn(),
                    exit = scaleOut(),
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                            .background(color = colorsScheme().onSurfaceSelection, shape = CircleShape)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(color = color, shape = CircleShape)
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        rowState.animateScrollToItem(
            MemeColors.colors.indexOfFirst { it == textDecor.fontColor }
        )
    }
}

@Preview
@Composable
private fun PreviewTextColorBar() {
    MasterMemeTheme {
        TextColorBar(
            decor = UiDecor(
                id = "",
                topLeft = Offset(0f, 0f),
                size = Size(0f, 0f),
                type = UiDecorType.TextUiDecor("")
            ),
            onColorSelected = {}
        )
    }
}