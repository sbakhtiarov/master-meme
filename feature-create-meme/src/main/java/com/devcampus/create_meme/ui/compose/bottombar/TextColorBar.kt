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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.Selection
import com.devcampus.create_meme.ui.common.MemeColors
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.MemeDecor

@Composable
fun TextColorBar(
    decor: MemeDecor,
    onColorSelected: (Color) -> Unit
) {

    val textDecor: DecorType.TextDecor = decor.type as DecorType.TextDecor

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
                            .background(color = Selection, shape = CircleShape)
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