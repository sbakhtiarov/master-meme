package com.devcampus.meme_templates.ui.compose

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.devcampus.common_android.ui.drawListGradient
import com.devcampus.meme_templates.ui.MemeTemplatesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MemeTemplatesBottomSheet(
    onSelected: (String) -> Unit,
    onDismissed: () -> Unit,
) {

    val viewModel: MemeTemplatesViewModel = hiltViewModel()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    var isInSearchMode by remember { mutableStateOf(false) }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight()
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets(0.dp))
            .drawListGradient(),
        sheetState = sheetState,
        onDismissRequest = {
            onDismissed()
        }
    ) {

        val configuration = LocalConfiguration.current

        val columns = when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> 4
            else -> 2
        }

        Column {
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
                            isInSearchMode = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                contentPadding = PaddingValues(22.dp),
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
                                onSelected(template.path)
                            }
                    ) {
                        AsyncImage(
                            modifier = Modifier.fillMaxSize(),
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
