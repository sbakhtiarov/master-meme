package com.devcampus.memes_list.domain

import com.devcampus.memes_list.domain.model.MemeFile
import kotlinx.coroutines.flow.Flow

internal interface FavouriteMemesRepository {
    fun getFavouriteMemes(): Flow<Set<MemeFile>>
    suspend fun setFavourite(memeFile: MemeFile, isFavourite: Boolean)
}
