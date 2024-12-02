package com.devcampus.memes_list.domain.model

import kotlinx.serialization.Serializable

@Serializable
internal data class MemeFile(
    val path: String,
    val lastModified: Long = 0,
)

