package com.devcampus.memes_list.ui.compose

import android.content.res.Configuration
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.devcampus.common_android.ui.drawListGradient
import com.devcampus.memes_list.domain.model.Meme

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
internal fun MemeContentScreen(
    memes: List<Meme>,
    selection: List<Meme>?,
    sharedTransitionScope: SharedTransitionScope,
    animatedContentScope: AnimatedContentScope,
    onItemClick: (Meme) -> Unit,
    onItemLongClick: (Meme) -> Unit,
    onItemFavouriteClick: (Meme) -> Unit,
) {

    val configuration = LocalConfiguration.current

    val columns = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> 4
        else -> 2
    }

    LazyVerticalGrid(
        modifier = Modifier.fillMaxSize().drawListGradient(),
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(start = 22.dp, top = 22.dp, end = 22.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(
            items = memes,
            key = { it.path }
        ) { meme ->
            with (sharedTransitionScope) {
                MemeGridItem(
                    modifier = Modifier
                        .animateItem()
                        .sharedElement(
                            sharedTransitionScope.rememberSharedContentState(key = "meme-${meme.path}"),
                            animatedVisibilityScope = animatedContentScope
                        ),
                    meme = meme,
                    isSelected = { selection?.contains(meme) },
                    onClick = { onItemClick(meme) },
                    onLongClick = { onItemLongClick(meme) },
                    onFavouriteClick = { onItemFavouriteClick(meme) }
                )
            }
        }
    }
}
