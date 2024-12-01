package com.devcampus.memes_list.domain

import com.devcampus.memes_list.domain.model.Meme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class GetMemesUseCase @Inject constructor(
    private val memesRepository: MemesRepository,
    private val favouritesRepository: FavouriteMemesRepository,
) {

    fun getMemes(): Flow<List<Meme>> =
        memesRepository.getMemes()
            .combine(favouritesRepository.getFavouriteMemes()) { memeFiles, favourites ->
                memeFiles.map {
                    Meme(
                        path = it.path,
                        isFavourite = favourites.contains(it)
                    )
                }
           }
}
