package com.devcampus.memes_list.ui.compose

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
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
import com.devcampus.memes_list.ui.MemeShare
import com.devcampus.memes_list.ui.Share
import com.devcampus.memes_list.ui.ShowDeletionConfirmation
import com.devcampus.memes_list.ui.ShowErrorMessage
import com.devcampus.memes_list.ui.isInSelectionMode
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MemesScreen(
    onAddClick: () -> Unit,
) {

    val viewModel: MemeListViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val sortMode by viewModel.sortMode.collectAsStateWithLifecycle()

    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    val context = LocalContext.current

    BackHandler(enabled = (viewState as? DataState)?.selection != null) { sendIntent(Intent.OnBackPress) }

    var showDeleteConformation by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            AnimatedContent(
                targetState = viewState.isInSelectionMode(),
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
                    DefaultAppBar(
                        sortModeSelection = sortMode.ordinal,
                        onSortModeSelected = { sendIntent(Intent.OnSortModeSelected(it)) }
                    )
                }
            }
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = viewState.isInSelectionMode().not(),
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                FloatingActionButton(
                    modifier = Modifier.padding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal).asPaddingValues()),
                    onClick = {
                        onAddClick()
                    },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                }
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
                is ShowDeletionConfirmation -> showDeleteConformation = true
                is ShowErrorMessage -> showError(context)
                is Share -> showShareDialog(context, action.paths)
            }
        }
    }
}

private fun showShareDialog(context: Context, paths: List<String>) {
    MemeShare.showShareDialog(context, paths)
}

private fun showError(context: Context) {
    Toast.makeText(context, R.string.common_error, Toast.LENGTH_SHORT).show()
}
