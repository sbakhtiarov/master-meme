package com.devcampus.memes_list.di

import com.devcampus.memes_list.data.FavouriteMemesRepositoryImpl
import com.devcampus.memes_list.data.MemesRepositoryImpl
import com.devcampus.memes_list.data.SortModeRepositoryImpl
import com.devcampus.memes_list.domain.FavouriteMemesRepository
import com.devcampus.memes_list.domain.MemesRepository
import com.devcampus.memes_list.domain.SortModeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class MemeListModule {

    @Binds
    internal abstract fun bindMemeRepository(impl: MemesRepositoryImpl): MemesRepository

    @Binds
    internal abstract fun bindFavouritesRepository(
        impl: FavouriteMemesRepositoryImpl
    ): FavouriteMemesRepository

    @Binds
    internal abstract fun bindSortModeRepository(
        impl: SortModeRepositoryImpl
    ): SortModeRepository

}
