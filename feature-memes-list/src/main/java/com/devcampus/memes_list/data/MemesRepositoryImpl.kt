package com.devcampus.memes_list.data

import android.content.Context
import com.devcampus.memes_list.domain.MemesRepository
import com.devcampus.memes_list.domain.model.MemeFile
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject

internal class MemesRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : MemesRepository {

    private companion object {
        private const val MEMES_FOLDER = "memes"
        private const val MEMES_IMAGE_EXT = ".jpg"
    }

    override fun getMemes(): Flow<List<MemeFile>> =
        callbackFlow {

            val memesDirectory = File(context.getExternalFilesDir(null), MEMES_FOLDER)

            if (!memesDirectory.exists()) {
                memesDirectory.mkdir()
            }

            send(memesDirectory.allMemeFiles())

            val observer = memesDirectory.startWatching {
                trySend(memesDirectory.allMemeFiles())
            }

            awaitClose { observer.stopWatching() }
        }

    private fun File.allMemeFiles() =
        listFiles { _, path ->
            path?.endsWith(MEMES_IMAGE_EXT) ?: false
        }?.map {
            MemeFile(it.absolutePath)
        } ?: emptyList()
}

