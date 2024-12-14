package com.devcampus.create_meme.domain

import android.util.SizeF

interface MemeFileSaver {
    suspend fun prepareMemeImage(
        assetPath: String,
        editorCanvasSize: SizeF,
        decorList: List<Decor>,
        saveToCache: Boolean,
    ): Result<String>
}
