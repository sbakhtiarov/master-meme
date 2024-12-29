package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.create_meme.R

@Composable
fun SaveProgressBottomBar() {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                space = 16.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp)
            )
            Text(
                text = stringResource(R.string.preparing_your_meme_image),
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDefaultBottomBar() {
    MasterMemeTheme {
        SaveProgressBottomBar()
    }
}
