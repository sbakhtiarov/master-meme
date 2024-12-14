package com.devcampus.create_meme.domain

import android.util.SizeF

interface MemeFileSaver {
    suspend fun copyMemeAsset(assetPath: String, editorCanvasSize: SizeF, decorList: List<Decor>)
}
