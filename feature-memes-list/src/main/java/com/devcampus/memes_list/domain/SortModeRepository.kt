package com.devcampus.memes_list.domain

import com.devcampus.memes_list.domain.model.SortMode
import kotlinx.coroutines.flow.Flow

interface SortModeRepository {
    fun getSortMode(): Flow<SortMode>
    suspend fun setSortMode(sortMode: SortMode)
}
