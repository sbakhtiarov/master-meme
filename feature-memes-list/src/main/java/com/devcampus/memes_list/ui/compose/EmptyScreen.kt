package com.devcampus.memes_list.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.memes_list.R

@Composable
internal fun EmptyScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        Image(painter = painterResource(R.drawable.memes_empty), contentDescription = null)
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.empty_screen_message),
            color = colorsScheme().textOutline,
        )
    }
}

@Composable
@Preview
private fun EmptyScreenPreview() {
    MasterMemeTheme {
        EmptyScreen()
    }
}