package com.devcampus.create_meme.ui.model

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.AnimationVector2D
import androidx.compose.ui.geometry.Offset

data class AnimatedDecor(
    val decor: MemeDecor,
    val offset: Animatable<Offset, AnimationVector2D>? = null,
    val alpha: Animatable<Float, AnimationVector1D> = Animatable(1f)
)

fun MemeDecor.toAnimatedDecor() = AnimatedDecor(this)
