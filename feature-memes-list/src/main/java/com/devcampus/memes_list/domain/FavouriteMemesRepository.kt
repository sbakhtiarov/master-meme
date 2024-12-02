package com.devcampus.memes_list.domain

import kotlinx.coroutines.flow.Flow

internal interface FavouriteMemesRepository {
    fun getFavouriteMemes(): Flow<List<String>>
    suspend fun setFavourite(memeFilePath: String, isFavourite: Boolean)
}
