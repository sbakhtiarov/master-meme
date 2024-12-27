package com.devcampus.memes_list.ui.compose

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberStandardBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.devcampus.common_android.ui.MemeShare.showShareDialog
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.meme_templates.ui.compose.MemeTemplatesBottomSheetScaffold
import com.devcampus.memes_list.ui.DataState
import com.devcampus.memes_list.ui.EmptyState
import com.devcampus.memes_list.ui.Error
import com.devcampus.memes_list.ui.Intent
import com.devcampus.memes_list.ui.Loading
import com.devcampus.memes_list.ui.MemeListViewModel
import com.devcampus.memes_list.ui.Share
import com.devcampus.memes_list.ui.ShowDeletionConfirmation
import com.devcampus.memes_list.ui.ShowErrorMessage
import com.devcampus.memes_list.ui.ShowMeme
import com.devcampus.memes_list.ui.isInSelectionMode
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.devcampus.common_android.R as CommonR

@OptIn(ExperimentalSharedTransitionApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MemesScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    showMemePreview: (String) -> Unit,
    showMemeEditor: (String) -> Unit,
) {

    val viewModel: MemeListViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val sortMode by viewModel.sortMode.collectAsStateWithLifecycle()

    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    val context = LocalContext.current

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberStandardBottomSheetState(initialValue = SheetValue.Hidden, skipHiddenState = false)

    BackHandler(enabled = (viewState as? DataState)?.selection != null) { sendIntent(Intent.OnBackPress) }

    var showDeleteConfirmation by remember { mutableStateOf(false) }

    MemeTemplatesBottomSheetScaffold(
        bottomSheetState = bottomSheetState,
        onTemplateSelected = {
            showMemeEditor(it)
        }
    ) {
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
                    IconButton(
                        modifier = Modifier
                            .size(56.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(colorsScheme().fabGradientStart, colorsScheme().fabGradientEnd)
                                ),
                                shape = RoundedCornerShape(14.dp)
                            ),
                        onClick = {
                            scope.launch {
                                bottomSheetState.partialExpand()
                            }
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary
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
                            sharedTransitionScope = sharedTransitionScope,
                            animatedContentScope = animatedContentScope,
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
    }

    if (showDeleteConfirmation) {
        (viewState as? DataState)?.selection?.size?.let { count ->
            ConfirmationDialog(
                count = count,
                onConfirm = {
                    showDeleteConfirmation = false
                    sendIntent(Intent.OnDeleteConfirmed)
                },
                onCancel = {
                    showDeleteConfirmation = false
                }
            )
        }
    }

    LaunchedEffect(Unit) {
        viewModel.actions.collectLatest { action ->
            when (action) {
                is ShowDeletionConfirmation -> showDeleteConfirmation = true
                is ShowErrorMessage -> showError(context)
                is Share -> showShareDialog(context, action.paths)
                is ShowMeme -> showMemePreview(action.meme.path)
            }
        }
    }
}

private fun showError(context: Context) {
    Toast.makeText(context, CommonR.string.common_error, Toast.LENGTH_SHORT).show()
}
