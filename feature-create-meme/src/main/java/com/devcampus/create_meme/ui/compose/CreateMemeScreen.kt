package com.devcampus.create_meme.ui.compose

import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devcampus.common_android.ui.MemeShare.showShareDialog
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.CloseScreen
import com.devcampus.create_meme.ui.CreateMemeViewModel
import com.devcampus.create_meme.ui.Intent
import com.devcampus.create_meme.ui.Intent.OnBackPress
import com.devcampus.create_meme.ui.Share
import com.devcampus.create_meme.ui.ShowLeaveConfirmation
import com.devcampus.create_meme.ui.ShowMemeCreateError
import com.devcampus.create_meme.ui.compose.bottombar.DefaultBottomBar
import com.devcampus.create_meme.ui.compose.bottombar.TextOptionsBottomBar
import com.devcampus.create_meme.ui.compose.dialog.ExitConfirmationDialog
import com.devcampus.create_meme.ui.compose.dialog.SaveAndShareDialog
import com.devcampus.create_meme.ui.compose.editor.MemeEditor
import com.devcampus.create_meme.ui.compose.editor.rememberMemeEditorState
import kotlinx.coroutines.flow.collectLatest
import com.devcampus.common_android.R as CommonR

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CreateMemeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    templateAsset: String,
    onClickUp: () -> Unit,
) {

    val context = LocalContext.current

    val viewModel : CreateMemeViewModel = hiltViewModel()
    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    var showLeaveConfirmation by remember { mutableStateOf(false) }
    var showSaveConfirmation by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    val editorState = rememberMemeEditorState(
        memeTemplatePath = templateAsset,
        decorItems = viewModel.decorItems,
        onDeleteClick = { id ->
            sendIntent(Intent.OnDecorDeleted(id))
        },
        onDecorAdded = { decor ->
            sendIntent(Intent.OnDecorAdded(decor))
        },
        onDecorMoved = { id, offset ->
            sendIntent(Intent.OnDecorMoved(id, offset))
        },
        onDecorUpdated = { decor ->
            sendIntent(Intent.OnDecorUpdated(decor))
        },
    )

    val bottomBarType by remember {
        derivedStateOf {
            if (editorState.selectedItem == null) {
                BottomBarType.DEFAULT
            } else {
                BottomBarType.TEXT_OPTIONS
            }
        }
    }

    BackHandler { sendIntent(OnBackPress) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(R.string.new_meme))
                },
                navigationIcon = {
                    Icon(
                        modifier = Modifier
                            .size(44.dp)
                            .padding(8.dp)
                            .clickable { sendIntent(OnBackPress) },
                        imageVector = Icons.AutoMirrored.Default.ArrowBack,
                        contentDescription = null
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors().copy(
                    containerColor = colorsScheme().surfaceContainerLow
                ),
            )
        },
        bottomBar = {
            AnimatedContent(
                targetState = bottomBarType,
                label = "bottom bar",
                transitionSpec = {
                    val enter = fadeIn()
                    val exit = fadeOut()
                    enter.togetherWith(exit)
                }

            ) { bottomBarType ->
                when(bottomBarType) {
                    BottomBarType.DEFAULT ->
                        DefaultBottomBar(
                            isUndoAvailable = viewModel.undoActions.isNotEmpty(),
                            isRedoAvailable = viewModel.redoActions.isNotEmpty(),
                            onAddClick = { editorState.addTextDecor() },
                            onSaveClick = { showSaveConfirmation = true },
                            onUndoClick = { sendIntent(Intent.Undo) },
                            onRedoClick = { sendIntent(Intent.Redo) },
                        )
                    BottomBarType.TEXT_OPTIONS ->
                        if (editorState.selectedItem != null) {
                            TextOptionsBottomBar(
                                decor = editorState.selectedItem ?: error("No selection"),
                                onFontSelected = { editorState.setFont(it) },
                                onFontScaleSelected = { editorState.setFontScale(it) },
                                onFontColorSelected = { editorState.setFontColor(it) },
                                onCancel = { editorState.cancelChanges() },
                                onConfirm = { editorState.confirmChanges() }
                            )
                        } else {
                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .height(100.dp))
                        }
                }
            }
        }
    ) { innerPadding ->
        MemeEditor(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = editorState,
            savedMemePath = viewModel.savedMemePath.value,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
        )
    }

    if (showLeaveConfirmation) {
        ExitConfirmationDialog(
            onConfirm = {
                showLeaveConfirmation = false
                onClickUp()
            },
            onCancel = {
                showLeaveConfirmation = false
            }
        )
    }

    if (showSaveConfirmation) {
        SaveAndShareDialog(
            onDismissed = { showSaveConfirmation = false },
            onSaveSelected = {
                showSaveConfirmation = false
                sendIntent(
                    Intent.OnSaveMeme(
                        assetPath = templateAsset,
                        canvasSize = editorState.canvasSize,
                        density = density,
                    )
                )
            },
            onShareSelected = {
                showSaveConfirmation = false
                sendIntent(
                    Intent.OnShareMeme(
                        assetPath = templateAsset,
                        canvasSize = editorState.canvasSize,
                        density = density,
                    )
                )
            },
        )
    }

    LaunchedEffect(Unit) {
        viewModel.actions.collectLatest { action ->
            when (action) {
                ShowLeaveConfirmation -> showLeaveConfirmation = true
                CloseScreen -> onClickUp()
                ShowMemeCreateError -> showError(context)
                is Share -> showShareDialog(context, listOf(action.path))
            }
        }
    }
}

private fun showError(context: Context) {
    Toast.makeText(context, CommonR.string.common_error, Toast.LENGTH_SHORT).show()
}

private enum class BottomBarType {
    DEFAULT, TEXT_OPTIONS
}
