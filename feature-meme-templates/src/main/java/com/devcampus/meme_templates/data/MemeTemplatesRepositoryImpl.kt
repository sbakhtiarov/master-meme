package com.devcampus.meme_templates.data

import android.content.Context
import com.devcampus.meme_templates.domain.MemeTemplatesRepository
import com.devcampus.meme_templates.domain.model.MemeTemplate
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MemeTemplatesRepositoryImpl @Inject constructor (
    @ApplicationContext private val context: Context
): MemeTemplatesRepository {

    private companion object {
        private const val TEMPLATES_DIR = "templates"
    }

    override suspend fun getTemplates(): List<MemeTemplate> {
        return withContext(Dispatchers.IO) {
            context.assets.list("$TEMPLATES_DIR/")?.map { name ->
                MemeTemplate("file:///android_asset/$TEMPLATES_DIR/$name")
            } ?: emptyList()
        }
    }
}
