package com.devcampus.create_meme.ui.compose.bottombar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.Primary
import com.devcampus.create_meme.R

@Composable
fun DefaultBottomBar(
    onAddClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Row {
                Icon(
                    modifier = Modifier.alpha(0.3f),
                    painter = painterResource(R.drawable.ic_undo),
                    tint = Primary,
                    contentDescription = null,
                )
                Icon(
                    modifier = Modifier.alpha(0.3f),
                    painter = painterResource(R.drawable.ic_redo),
                    tint = Primary,
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
