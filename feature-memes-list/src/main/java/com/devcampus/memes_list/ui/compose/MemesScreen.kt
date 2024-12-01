package com.devcampus.memes_list.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devcampus.memes_list.ui.DataState
import com.devcampus.memes_list.ui.EmptyState
import com.devcampus.memes_list.ui.Error
import com.devcampus.memes_list.ui.Intent
import com.devcampus.memes_list.ui.Loading
import com.devcampus.memes_list.ui.MemeListViewModel

@Composable
fun MemesScreen() {

    val viewModel: MemeListViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    BackHandler(enabled = (viewState as? DataState)?.selection != null) { sendIntent(Intent.OnBackPress) }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = (viewState as? DataState)?.selection != null,
                label = "AppBarAnimation"
            ) { isSelectionMode ->
                if (isSelectionMode) {
                    SelectionAppBar(
                        selectionCount = (viewState as? DataState)?.selection?.size ?: 0,
                        onClose = { sendIntent(Intent.OnBackPress) },
                        onShare = { sendIntent(Intent.OnSelectionShare) },
                        onDelete = { sendIntent(Intent.OnSelectionDelete) },
                    )
                } else {
                    DefaultAppBar()
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {},
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.padding(innerPadding)
        ) {
            when (val state = viewState) {
                is DataState ->
                    MemeContentScreen(
                        memes = state.memes,
                        selection = state.selection,
                        onItemClick = { sendIntent(Intent.OnMemeClick(it)) },
                        onItemLongClick = { sendIntent(Intent.OnMemeLongClick(it)) },
                    )

                EmptyState -> EmptyScreen()
                Loading -> LoadingScreen()
                Error -> ErrorScreen()
            }
        }
    }
}
