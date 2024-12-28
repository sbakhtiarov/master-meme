package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.model.UiDecorType
import com.devcampus.create_meme.ui.model.UiDecor

@Composable
fun TextSizeBar(
    decor: UiDecor,
    onFontScaleChanged: (Float) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(color = MaterialTheme.colorScheme.surfaceContainer)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val textDecor: UiDecorType.TextUiDecor = decor.type as UiDecorType.TextUiDecor

        Icon(
            modifier = Modifier.size(44.dp).padding(8.dp),
            painter = painterResource(R.drawable.ic_font),
            contentDescription = null)

        Slider(
            modifier = Modifier.fillMaxWidth().weight(1f),
            value = textDecor.fontScale,
            valueRange = 0.5f..1.5f,
            onValueChange = {
                onFontScaleChanged(it)
            }
        )

        Icon(
            modifier = Modifier.size(44.dp),
            painter = painterResource(R.drawable.ic_font),
            contentDescription = null
        )
    }
}

@Preview
@Composable
private fun PreviewTextSizeBar() {
    MasterMemeTheme {
        TextSizeBar(
            decor = UiDecor(
                id = "",
                topLeft = Offset(0f, 0f),
                size = Size(0f, 0f),
                type = UiDecorType.TextUiDecor("")
            ),
            onFontScaleChanged = {},
        )
    }
}