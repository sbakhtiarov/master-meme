package com.devcampus.memes_list.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.devcampus.common_android.ui.theme.SurfaceContainer
import com.devcampus.memes_list.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DefaultAppBar() {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.your_memes),
                fontSize = 24.sp,
            )
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = SurfaceContainer
        ),
        actions = {
            SortOptionsDropdown {
                // TODO: handle sort option selection
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectionAppBar(
    selectionCount: Int,
    onClose: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit,
) {
    TopAppBar(
        navigationIcon = {
            Icon(
                modifier = Modifier
                    .clickable { onClose() }
                    .size(44.dp)
                    .padding(10.dp),
                imageVector = Icons.Default.Close,
                contentDescription = null
            )
        },
        title = {
            if (selectionCount > 0) {
                Text(
                    modifier = Modifier.padding(start = 16.dp),
                    text = selectionCount.toString(),
                    fontSize = 24.sp,
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(
            containerColor = SurfaceContainer
        ),
        actions = {
            Icon(
                modifier = Modifier
                    .clickable { onShare() }
                    .size(44.dp)
                    .padding(10.dp),
                imageVector = Icons.Default.Share,
                contentDescription = null
            )
            Icon(
                modifier = Modifier
                    .clickable { onDelete() }
                    .size(44.dp)
                    .padding(10.dp),
                imageVector = Icons.Outlined.Delete,
                contentDescription = null
            )
        }
    )
}