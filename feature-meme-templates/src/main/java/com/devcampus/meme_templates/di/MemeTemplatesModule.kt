package com.devcampus.meme_templates.di

import com.devcampus.meme_templates.data.MemeTemplatesRepositoryImpl
import com.devcampus.meme_templates.domain.MemeTemplatesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class MemeTemplatesModule {

    @Binds
    internal abstract fun bindTemplatesRepository(impl: MemeTemplatesRepositoryImpl): MemeTemplatesRepository

}