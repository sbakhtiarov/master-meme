package com.devcampus.memes_list.domain

import com.devcampus.memes_list.domain.model.MemeFile
import kotlinx.coroutines.flow.Flow

internal interface MemesRepository {
    fun getMemes(): Flow<List<MemeFile>>
    suspend fun delete(memes: List<MemeFile>): Result<Unit>
}
