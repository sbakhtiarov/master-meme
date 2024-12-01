package com.devcampus.memes_list.di

import com.devcampus.memes_list.data.MemesRepositoryImpl
import com.devcampus.memes_list.domain.MemesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class MemeListModule {

    @Binds
    internal abstract fun bindMemeRepository(impl: MemesRepositoryImpl): MemesRepository

}