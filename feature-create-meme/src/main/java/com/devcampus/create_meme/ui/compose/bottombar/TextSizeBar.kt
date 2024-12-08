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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.MemeDecor

@Composable
fun TextSizeBar(
    decor: MemeDecor,
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
        val textDecor: DecorType.TextDecor = decor.type as DecorType.TextDecor

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
