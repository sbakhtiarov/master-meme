package com.devcampus.meme_templates.domain

import com.devcampus.meme_templates.domain.model.MemeTemplate

interface MemeTemplatesRepository {

    /**
     * Get all template image paths from assets
     */
    suspend fun getTemplates(): List<MemeTemplate>

}
