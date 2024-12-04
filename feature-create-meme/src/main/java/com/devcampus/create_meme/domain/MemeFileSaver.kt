package com.devcampus.create_meme.domain

interface MemeFileSaver {

    suspend fun copyMemeAsset(assetPath: String)

}
