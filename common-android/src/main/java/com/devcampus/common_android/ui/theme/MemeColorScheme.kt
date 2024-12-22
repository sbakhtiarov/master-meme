package com.devcampus.common_android.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
class MemeColorScheme(

    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerLow: Color,
    val surfaceContainerLowest: Color,
    val surface: Color,
    val onSurface: Color,

    val secondaryFixedDim: Color,
    val fabGradientStart: Color,
    val fabGradientEnd: Color,

    val scrimColorStart: Color,
    val textOutline: Color,

    val onSurfaceSelection: Color,

) {
    fun toColorScheme() = darkColorScheme(
        primary = primary, onPrimary = onPrimary,
        surface = surface,
        onSurface = onSurface,
        primaryContainer = primaryContainer, onPrimaryContainer = onPrimaryContainer,
        surfaceContainerHigh = surfaceContainerHigh,
        surfaceContainerLow = surfaceContainerLow,
        surfaceContainerLowest = surfaceContainerLowest,
    )
}

val DefaultMemeColorScheme = MemeColorScheme(
    primary = Primary,
    onPrimary = OnPrimary,
    surface = Surface, onSurface = OnSurface,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = OnPrimaryContainer,
    surfaceContainerHigh = SurfaceContainerHigh,
    surfaceContainerLow = SurfaceContainerLow,
    surfaceContainerLowest = SurfaceContainerLowest,
    secondaryFixedDim = SecondaryFixedDim,
    fabGradientStart = FabGradientStart,
    fabGradientEnd = FabGradientEnd,
    scrimColorStart = ScrimColorStart,
    textOutline = TextOutline,
    onSurfaceSelection = TextColorSelection,
)
