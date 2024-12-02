package com.devcampus.memes_list.ui.compose

import android.content.Context
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devcampus.memes_list.R
import com.devcampus.memes_list.ui.DataState
import com.devcampus.memes_list.ui.EmptyState
import com.devcampus.memes_list.ui.Error
import com.devcampus.memes_list.ui.Intent
import com.devcampus.memes_list.ui.Loading
import com.devcampus.memes_list.ui.MemeListViewModel
import com.devcampus.memes_list.ui.ShowDeletionConfirmation
import com.devcampus.memes_list.ui.ShowErrorMessage
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MemesScreen() {

    val viewModel: MemeListViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    val context = LocalContext.current

    BackHandler(enabled = (viewState as? DataState)?.selection != null) { sendIntent(Intent.OnBackPress) }

    var showDeleteConformation by remember { mutableStateOf(false) }

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
                        onItemFavouriteClick = { sendIntent(Intent.OnMemeFavouriteClick(it)) },
                    )

                EmptyState -> EmptyScreen()
                Loading -> LoadingScreen()
                Error -> ErrorScreen()
            }
        }
    }

    if (showDeleteConformation) {
        (viewState as? DataState)?.selection?.size?.let { count ->
            ConfirmationDialog(
                count = count,
                onConfirm = {
                    showDeleteConformation = false
                    sendIntent(Intent.OnDeleteConfirmed)
                },
                onCancel = {
                    showDeleteConformation = false
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.actions.collectLatest { action ->
            when (action) {
                ShowDeletionConfirmation -> showDeleteConformation = true
                ShowErrorMessage -> showError(context)
            }
        }
    }
}

private fun showError(context: Context) {
    Toast.makeText(context, R.string.common_error, Toast.LENGTH_SHORT).show()
}
