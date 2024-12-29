package com.devcampus.create_meme.ui.compose

import android.content.Context
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import com.devcampus.common_android.ui.BottomSheetScaffoldWithScrim
import com.devcampus.common_android.ui.MemeShare.showShareDialog
import com.devcampus.common_android.ui.rememberSheetState
import com.devcampus.create_meme.ui.CloseScreen
import com.devcampus.create_meme.ui.CreateMemeViewModel
import com.devcampus.create_meme.ui.Intent
import com.devcampus.create_meme.ui.Intent.OnBackPress
import com.devcampus.create_meme.ui.Share
import com.devcampus.create_meme.ui.ShowLeaveConfirmation
import com.devcampus.create_meme.ui.ShowMemeCreateError
import com.devcampus.create_meme.ui.common.LockScreenOrientation
import com.devcampus.create_meme.ui.compose.dialog.ExitConfirmationDialog
import com.devcampus.create_meme.ui.compose.dialog.SaveAndShareDialog
import com.devcampus.create_meme.ui.editor.rememberEditorProperties
import com.devcampus.create_meme.ui.editor.rememberMemeEditorState
import kotlinx.coroutines.launch
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
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    val viewModel : CreateMemeViewModel = hiltViewModel()
    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    var showLeaveConfirmation by remember { mutableStateOf(false) }

    val density = LocalDensity.current

    val properties = rememberEditorProperties()

    val editorState = rememberMemeEditorState(
        decorItems = viewModel.decorItems,
        properties = properties,
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

    BackHandler { sendIntent(OnBackPress) }

    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val scope = rememberCoroutineScope()
    val bottomSheetState = rememberSheetState(
        skipPartiallyExpanded = true
    )

    BottomSheetScaffoldWithScrim(
        bottomSheetState = bottomSheetState,
        sheetContent = {
            SaveAndShareDialog(
                onSaveSelected = {
                    scope.launch { bottomSheetState.hide() }
                    sendIntent(
                        Intent.OnSaveMeme(
                            assetPath = templateAsset,
                            canvasSize = editorState.canvasSize,
                            density = density,
                        )
                    )
                },
                onShareSelected = {
                    scope.launch { bottomSheetState.hide() }
                    sendIntent(
                        Intent.OnShareMeme(
                            assetPath = templateAsset,
                            canvasSize = editorState.canvasSize,
                            density = density,
                        )
                    )
                },
            )
        },
        content = {
            CreateMemeScreenContent(
                memeTemplatePath = templateAsset,
                memeFilePath = viewModel.memePath.value ?: templateAsset,
                editorState = editorState,
                isUndoAvailable = viewModel.undoActions.isNotEmpty(),
                isRedoAvailable = viewModel.redoActions.isNotEmpty(),
                isSaveInProgress = viewModel.isSaveInProgress.value,
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onBackClick = { sendIntent(OnBackPress) },
                onSaveClick = { scope.launch { bottomSheetState.expand() } },
                onUndoClick = { sendIntent(Intent.Undo) },
                onRedoClick = { sendIntent(Intent.Redo) }
            )
        }
    )

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

    LaunchedEffect(Unit) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.actions.collect { action ->
                when (action) {
                    ShowLeaveConfirmation -> showLeaveConfirmation = true
                    CloseScreen -> onClickUp()
                    ShowMemeCreateError -> showError(context)
                    is Share -> showShareDialog(context, listOf(action.path))
                }
            }
        }
    }
}

private fun showError(context: Context) {
    Toast.makeText(context, CommonR.string.common_error, Toast.LENGTH_SHORT).show()
}
