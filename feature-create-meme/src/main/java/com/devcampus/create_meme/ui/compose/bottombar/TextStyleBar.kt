package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcampus.common_android.ui.conditional
import com.devcampus.common_android.ui.theme.DefaultMemeColorScheme
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.create_meme.ui.common.MemeFontFamily
import com.devcampus.create_meme.ui.common.MemeFonts
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.MemeDecor

@Composable
fun TextStyleBar(
    decor: MemeDecor,
    onFontSelected: (MemeFontFamily) -> Unit,
) {

    val decorType: DecorType.TextDecor = decor.type as DecorType.TextDecor

    var selection by remember { mutableStateOf(decorType.fontFamily.name) }

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
            items = MemeFonts.fonts,
            key = { it.name }
        ) { font ->
            FontButton(
                font = font,
                isSelected = { selection == font.name },
                onClick = {
                    selection = font.name
                    onFontSelected(font)
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        rowState.animateScrollToItem(
            MemeFonts.fonts.indexOfFirst { it.name == selection }
        )
    }
}

@Composable
private fun FontButton(font: MemeFontFamily, isSelected: () -> Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .conditional(isSelected()) {
                Modifier.background(
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    shape = RoundedCornerShape(12.dp)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .clickable(!isSelected()) { onClick() }
            .padding(vertical = 8.dp, horizontal = 12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Good",
            fontSize = 28.sp,
            fontFamily = font.fontFamily
        )
        Text(
            text = font.name,
            fontSize = 14.sp
        )
    }
}

@Preview
@Composable
private fun PreviewTextStyleBar() {
    MasterMemeTheme {
        TextStyleBar(
            decor = MemeDecor(
                id = "",
                topLeft = Offset(0f, 0f),
                size = Size(0f, 0f),
                type = DecorType.TextDecor("")
            ),
            onFontSelected = {}
        )
    }
}
