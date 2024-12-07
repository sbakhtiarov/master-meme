package com.devcampus.create_meme.ui.compose

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.devcampus.common_android.ui.theme.SurfaceContainer
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.CloseScreen
import com.devcampus.create_meme.ui.CreateMemeViewModel
import com.devcampus.create_meme.ui.Intent
import com.devcampus.create_meme.ui.Intent.OnBackPress
import com.devcampus.create_meme.ui.ShowLeaveConfirmation
import com.devcampus.create_meme.ui.compose.bottombar.DefaultBottomBar
import com.devcampus.create_meme.ui.compose.bottombar.TextOptionsBottomBar
import com.devcampus.create_meme.ui.compose.editor.MemeEditor
import com.devcampus.create_meme.ui.compose.editor.rememberMemeEditorState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateMemeScreen(
    templateAsset: String,
    onClickUp: () -> Unit,
) {

    val viewModel : CreateMemeViewModel = hiltViewModel()
    val sendIntent: (Intent) -> Unit = remember { { viewModel.onIntent(it) } }

    var showLeaveConfirmation by remember { mutableStateOf(false) }

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
    )

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
                    containerColor = SurfaceContainer
                ),
            )
        },
        bottomBar = {
            AnimatedContent(
                targetState = editorState.selectedItem,
                label = "bottom bar",
            ) { selectedItem ->
                if (selectedItem == null) {
                    DefaultBottomBar(
                        onAddClick = { editorState.addTextDecor(text = "TAP TWICE TO EDIT") },
                        onSaveClick = { sendIntent(Intent.OnSaveMeme(templateAsset)) }
                    )
                } else {
                    TextOptionsBottomBar(
                        decor = selectedItem,
                        onCancel = { editorState.cancelChanges() },
                        onConfirm = { editorState.confirmChanges() }
                    )
                }
            }
        }
    ) { innerPadding ->
        MemeEditor(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            state = editorState,
        )
    }

    if (showLeaveConfirmation) {
        ConfirmationDialog(
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
        viewModel.actions.collectLatest { action ->
            when (action) {
                ShowLeaveConfirmation -> showLeaveConfirmation = true
                CloseScreen -> onClickUp()
            }
        }
    }
}