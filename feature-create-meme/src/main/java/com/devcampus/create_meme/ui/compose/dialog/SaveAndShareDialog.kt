package com.devcampus.create_meme.ui.compose.dialog

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcampus.common_android.ui.theme.MasterMemeTheme
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.create_meme.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaveAndShareDialog(
    onDismissed: () -> Unit,
    onSaveSelected: () -> Unit,
    onShareSelected: () -> Unit,
) {

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false
    )

    ModalBottomSheet(
        modifier = Modifier,
        sheetState = sheetState,
        onDismissRequest = {
            onDismissed()
        }
    ) {
        Column {
            DialogButton(
                icon = MemeIcons.SimCardDownload,
                title = stringResource(R.string.save_to_device),
                subtitle = stringResource(R.string.save_to_device_message),
                onClick = onSaveSelected
            )
            DialogButton(
                icon = Icons.Default.Share,
                title = stringResource(R.string.share_the_meme),
                subtitle = stringResource(R.string.share_the_meme_message),
                onClick = onShareSelected
            )
        }
    }
}

@Composable
private fun DialogButton(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {

        Icon(
            modifier = Modifier
                .size(28.dp)
                .align(Alignment.CenterVertically),
            imageVector = icon,
            tint = colorsScheme().secondaryFixedDim,
            contentDescription = null
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = colorsScheme().secondaryFixedDim,
            )
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = colorsScheme().textOutline,
            )
        }
    }
}

@Preview
@Composable
private fun PreviewDialogButton() {
    MasterMemeTheme {
        DialogButton(
            icon = MemeIcons.SimCardDownload,
            title = stringResource(R.string.save_to_device),
            subtitle = stringResource(R.string.save_to_device_message),
        ) { }
    }
}
