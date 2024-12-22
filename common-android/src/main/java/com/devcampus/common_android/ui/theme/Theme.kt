package com.devcampus.common_android.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

@Composable
fun MasterMemeTheme(
    memeColorScheme: MemeColorScheme = DefaultMemeColorScheme,
    content: @Composable () -> Unit
) {

    val view = LocalView.current
    if (!view.isInEditMode) {
        val window = (view.context as Activity).window
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }

    CompositionLocalProvider(
        LocalMemeColors provides memeColorScheme
    ) {
        MaterialTheme(
            colorScheme = memeColorScheme.toColorScheme(),
            typography = Typography,
            content = content
        )
    }

}

@Composable
fun colorsScheme() = MaterialTheme.memeColorScheme

val MaterialTheme.memeColorScheme
    @Composable
    get() = LocalMemeColors.current

private val LocalMemeColors = staticCompositionLocalOf { DefaultMemeColorScheme }
