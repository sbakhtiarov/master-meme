package com.devcampus.memes_list.ui.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.devcampus.common_android.ui.theme.Primary
import com.devcampus.common_android.ui.theme.ScrimColorStart
import com.devcampus.memes_list.domain.model.Meme

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Composable
internal fun MemeGridItem(
    meme: Meme,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onFavouriteClick: () -> Unit,
    isSelected: () -> Boolean?,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .aspectRatio(1f)
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
    ) {

        val context = LocalContext.current

        val model by remember {
            mutableStateOf(
                ImageRequest.Builder(context)
                    .data(meme.path)
                    .transformations(RoundedCornersTransformation(32f))
                    .build()
            )
        }

        AsyncImage(
            modifier = Modifier
                .fillMaxSize()
                .drawWithCache {
                    val selectionGradient = Brush.radialGradient(
                        colors = listOf(ScrimColorStart, Color.Transparent),
                        center = Offset(size.width, 0f),
                        radius = size.width / 2f,
                    )
                    val favouriteGradient = Brush.radialGradient(
                        colors = listOf(ScrimColorStart, Color.Transparent),
                        center = Offset(size.width, size.height),
                        radius = size.width / 2f,
                    )
                    onDrawWithContent {
                        drawContent()
                        if (isSelected() != null) {
                            drawRect(selectionGradient, blendMode = BlendMode.Multiply)
                        } else {
                            drawRect(favouriteGradient, blendMode = BlendMode.Multiply)
                        }
                    }
                },
            model = model,
            contentScale = ContentScale.Crop,
            contentDescription = null,
        )

        AnimatedContent(
            modifier = Modifier.align(Alignment.TopEnd),
            targetState = isSelected(),
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(220)
                ) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "Select animation"
        ) { selected ->
            when (selected) {
                true -> IconSelected()
                false -> IconUnselected()
                else -> IconPlaceholder()
            }
        }

        AnimatedContent(
            modifier = Modifier.align(Alignment.BottomEnd),
            targetState = isSelected() != null,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(220)
                ) togetherWith fadeOut(animationSpec = tween(220))
            },
            label = "Select animation"
        ) { isInSelectionMode ->
            if (isInSelectionMode) {
                IconPlaceholder()
            } else {
                if (meme.isFavourite) {
                    IconFavoriteFilled(
                        Modifier.clickable { onFavouriteClick() }
                    )
                } else {
                    IconFavoriteOutline(
                        Modifier.clickable { onFavouriteClick() }
                    )
                }
            }
        }

    }
}

@Composable
private fun IconPlaceholder() {
    Box(
        modifier = Modifier
            .padding(14.dp)
            .size(20.dp),
    )
}

@Composable
private fun IconUnselected() {
    Box(
        modifier = Modifier
            .padding(14.dp)
            .size(20.dp)
            .alpha(0.7f)
            .border(
                width = 2.dp,
                shape = CircleShape,
                color = Primary,
            ),
    )
}

@Composable
private fun IconSelected() {
    Icon(
        modifier = Modifier
            .padding(12.dp)
            .size(24.dp),
        imageVector = Icons.Filled.CheckCircle,
        tint = Primary,
        contentDescription = null
    )
}

@Composable
private fun IconFavoriteOutline(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier
            .padding(12.dp)
            .size(24.dp),
        imageVector = Icons.Filled.FavoriteBorder,
        tint = Primary,
        contentDescription = null
    )
}

@Composable
private fun IconFavoriteFilled(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier
            .padding(12.dp)
            .size(24.dp),
        imageVector = Icons.Filled.Favorite,
        tint = Primary,
        contentDescription = null
    )
}

//@Composable
//@Preview
//private fun MemeGridItemPreview() {
//    MasterMemeTheme {
//        Box(modifier = Modifier.padding(22.dp)) {
//            MemeGridItem(
//                meme = Meme(
//                    path = "android.resource://" +
//                            "${LocalContext.current.packageName}/${R.drawable.memes_empty}",
//                    isFavourite = false
//                ),
//                onClick = {},
//                onLongClick = {},
//                onFavouriteClick = {},
//                isSelected = { false },
//            )
//        }
//    }
//}
