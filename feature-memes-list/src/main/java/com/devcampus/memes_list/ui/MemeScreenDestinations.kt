package com.devcampus.memes_list.ui

import kotlinx.serialization.Serializable

@Serializable
data object MemeScreenDestination

@Serializable
data class MemePreviewScreenDestination(
    val path: String
)
