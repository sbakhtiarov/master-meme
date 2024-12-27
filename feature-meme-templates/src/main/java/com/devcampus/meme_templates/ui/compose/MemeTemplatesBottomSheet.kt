package com.devcampus.meme_templates.ui.compose

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.devcampus.common_android.ui.BottomSheetScaffoldWithScrim
import com.devcampus.common_android.ui.drawListGradient
import com.devcampus.meme_templates.ui.MemeTemplatesViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun MemeTemplatesBottomSheetScaffold(
    bottomSheetState: SheetState,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onTemplateSelected: (String) -> Unit,
    content: @Composable () -> Unit
) {

    val scope = rememberCoroutineScope()

    BackHandler(enabled = bottomSheetState.isVisible) { scope.launch { bottomSheetState.hide() } }

    BottomSheetScaffoldWithScrim(
        bottomSheetState = bottomSheetState,
        sheetPeekHeight = 480.dp,
        sheetContent = {
            MemeTemplatesContent(
                sharedTransitionScope = sharedTransitionScope,
                animatedContentScope = animatedContentScope,
                onSelected = { template ->
                    onTemplateSelected(template)
                    scope.launch { bottomSheetState.hide() }
                },
                onEnterSearchMode = {
                    scope.launch { bottomSheetState.expand() }
                }
            )
        },
        content = content
    )
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MemeTemplatesContent(
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onSelected: (String) -> Unit,
    onEnterSearchMode: () -> Job,
) {

    val viewModel: MemeTemplatesViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    var isInSearchMode by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current

    val columns = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }

    BackHandler(enabled = isInSearchMode) { isInSearchMode = false }

    Column(
        modifier = Modifier
            .fillMaxHeight(0.95f)
            .drawListGradient(),
    ) {
        AnimatedContent(
            targetState = isInSearchMode,
            label = "Top bar animation"
        ) { searchMode ->
            if (searchMode) {
                SearchTopBar(
                    templatesCount = viewState.templates.size,
                    onDismissed = {
                        isInSearchMode = false
                        viewModel.onQueryUpdate("")
                    },
                    onQueryUpdate = {
                        viewModel.onQueryUpdate(it)
                    }
                )
            } else {
                DefaultTopBar(
                    onSearchClick = {
                        onEnterSearchMode().invokeOnCompletion {
                            isInSearchMode = true
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(columns),
            contentPadding = PaddingValues(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 72.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = viewState.templates,
                key = { it.path }
            ) { template ->

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(1f)
                        .clickable {
                            isInSearchMode = false
                            onSelected(template.path)
                            viewModel.onQueryUpdate("")
                        }
                ) {
                    with (sharedTransitionScope) {
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .sharedElement(
                                    sharedTransitionScope.rememberSharedContentState(key = "meme-${template.path}"),
                                    animatedVisibilityScope = animatedContentScope
                                ),
                            model = template.path,
                            contentScale = ContentScale.Crop,
                            contentDescription = null,
                        )
                    }
                }
            }
        }
    }
}