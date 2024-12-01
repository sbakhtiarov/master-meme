package com.devcampus.memes_list.domain.model

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
internal value class MemeFile(val path: String)
