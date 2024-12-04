package com.devcampus.create_meme.data

import android.content.Context
import com.devcampus.create_meme.domain.MemeFileSaver
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

class MemeFileSaverImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : MemeFileSaver {

    private companion object {
        private const val MEMES_FOLDER = "memes"
    }

    override suspend fun copyMemeAsset(assetPath: String) {
        withContext(Dispatchers.IO) {

            val assetName = assetPath.split("/").last()

            val memesDirectory = File(context.getExternalFilesDir(null), MEMES_FOLDER)
            val destFile = File(memesDirectory, "meme_${UUID.randomUUID()}_$assetName")

            memesDirectory.mkdirs()

            context.assets.open("templates/$assetName").use { input ->
                destFile.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }
    }
}
