package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.create_meme.R

@Composable
fun DefaultBottomBar(
    isUndoAvailable: Boolean,
    isRedoAvailable: Boolean,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onAddClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                Icon(
                    modifier = Modifier
                        .alpha(alpha = if (isUndoAvailable) 1f else 0.3f)
                        .clickable(isUndoAvailable) { onUndoClick() },
                    painter = painterResource(R.drawable.ic_undo),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                )
                Icon(
                    modifier = Modifier
                        .alpha(alpha = if (isRedoAvailable) 1f else 0.3f)
                        .clickable(isRedoAvailable) { onRedoClick() },
                    painter = painterResource(R.drawable.ic_redo),
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null
                )
            }

            OutlinedButton(onClick = onAddClick) {
                Text(text = stringResource(R.string.add_text))
            }

            Button(onClick = onSaveClick) {
                Text(text = stringResource(R.string.save_meme))
            }
        }
    }
}

@Preview
@Composable
private fun PreviewDefaultBottomBar() {
    MasterMemeTheme {
        DefaultBottomBar(
            isUndoAvailable = true,
            isRedoAvailable = false,
            onUndoClick = {},
            onRedoClick = {},
            onAddClick = {},
            onSaveClick = {},
        )
    }
}
