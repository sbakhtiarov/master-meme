package com.devcampus.create_meme.ui.compose

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.theme.colorsScheme
import com.devcampus.create_meme.R
import com.devcampus.create_meme.ui.compose.bottombar.DefaultBottomBar
import com.devcampus.create_meme.ui.compose.bottombar.SaveProgressBottomBar
import com.devcampus.create_meme.ui.compose.bottombar.TextOptionsBottomBar
import com.devcampus.create_meme.ui.compose.editor.MemeEditor
import com.devcampus.create_meme.ui.editor.MemeEditorState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun CreateMemeScreenContent(
    memeTemplatePath: String,
    memeFilePath: String,
    editorState: MemeEditorState,
    isSaveInProgress: Boolean,
    isUndoAvailable: Boolean,
    isRedoAvailable: Boolean,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onSaveClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit,
    onBackClick: () -> Unit,
) {

    val bottomBarType = when {
        isSaveInProgress -> BottomBarType.SAVE_IN_PROGRESS
        editorState.selectedItem == null -> BottomBarType.DEFAULT
        else -> BottomBarType.TEXT_OPTIONS
    }

    Scaffold(
        topBar = {
            if (isSaveInProgress) {
                null
            } else {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = stringResource(R.string.new_meme))
                    },
                    navigationIcon = {
                        Icon(
                            modifier = Modifier
                                .size(44.dp)
                                .padding(8.dp)
                                .clickable { onBackClick() },
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = null
                        )
                    },
                    colors = TopAppBarDefaults.topAppBarColors().copy(
                        containerColor = colorsScheme().surfaceContainerLow
                    ),
                )
            }
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
                BottomBar(
                    bottomBarType = bottomBarType,
                    editorState = editorState,
                    isUndoAvailable = isUndoAvailable,
                    isRedoAvailable = isRedoAvailable,
                    onSaveClick = onSaveClick,
                    onUndoClick = onUndoClick,
                    onRedoClick = onRedoClick
                )
            }
        }
    ) { innerPadding ->
        MemeEditor(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            templatePath = memeTemplatePath,
            state = editorState,
            memePath = memeFilePath,
            sharedTransitionScope = sharedTransitionScope,
            animatedContentScope = animatedContentScope,
        )
    }
}

@Composable
private fun BottomBar(
    bottomBarType: BottomBarType,
    editorState: MemeEditorState,
    isUndoAvailable: Boolean,
    isRedoAvailable: Boolean,
    onSaveClick: () -> Unit,
    onUndoClick: () -> Unit,
    onRedoClick: () -> Unit
) {
    when (bottomBarType) {
        BottomBarType.DEFAULT ->
            DefaultBottomBar(
                isUndoAvailable = isUndoAvailable,
                isRedoAvailable = isRedoAvailable,
                onAddClick = { editorState.addTextDecor() },
                onSaveClick = { onSaveClick() },
                onUndoClick = { onUndoClick() },
                onRedoClick = { onRedoClick() },
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
                        .height(100.dp)
                )
            }

        BottomBarType.SAVE_IN_PROGRESS -> SaveProgressBottomBar()
    }
}

private enum class BottomBarType {
    DEFAULT, TEXT_OPTIONS, SAVE_IN_PROGRESS
}
