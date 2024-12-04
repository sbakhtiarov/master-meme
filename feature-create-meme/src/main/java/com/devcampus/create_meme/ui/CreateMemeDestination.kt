package com.devcampus.create_meme.ui

import kotlinx.serialization.Serializable

@Serializable
data class CreateMemeDestination(
    val templateAssetPath: String
)
