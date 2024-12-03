package com.devcampus.meme_templates.domain

import com.devcampus.meme_templates.domain.model.MemeTemplate

interface MemeTemplatesRepository {

    suspend fun getTemplates(): List<MemeTemplate>

}
