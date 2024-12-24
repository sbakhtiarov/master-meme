package com.devcampus.memes_list.ui.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.R
import com.devcampus.common_android.ui.theme.MasterMemeTheme

@Composable
internal fun ErrorScreen() {
    Box(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            stringResource(R.string.common_error),
            textAlign = TextAlign.Center,
        )
    }
}


@Composable
@Preview
private fun ErrorScreenPreview() {
    MasterMemeTheme {
        ErrorScreen()
    }
}
