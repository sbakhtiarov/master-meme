package com.devcampus.memes_list.domain

import com.devcampus.memes_list.domain.model.Meme
import com.devcampus.memes_list.domain.model.SortMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

internal class GetMemesUseCase @Inject constructor(
    private val memesRepository: MemesRepository,
    private val favouritesRepository: FavouriteMemesRepository,
    private val sortModeRepository: SortModeRepository,
) {

    fun getMemes(): Flow<List<Meme>> =
        combine(
            memesRepository.getMemes(),
            favouritesRepository.getFavouriteMemes(),
            sortModeRepository.getSortMode()
        ) { memeFiles, favourites, sortMode ->

            val memes = memeFiles
                .sortedByDescending { it.lastModified }
                .map { file ->
                    Meme(
                        path = file.path,
                        isFavourite = favourites.contains(file.path)
                    )
                }

            if (sortMode == SortMode.NEWEST_FIRST) {
                memes
            } else {
                memes.sortedByDescending { it.isFavourite }
            }
        }
}
