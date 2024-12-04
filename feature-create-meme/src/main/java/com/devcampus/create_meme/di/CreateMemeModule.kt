package com.devcampus.create_meme.di

import com.devcampus.create_meme.data.MemeFileSaverImpl
import com.devcampus.create_meme.domain.MemeFileSaver
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class CreateMemeModule {

    @Binds
    internal abstract fun bindMemeSaver(impl: MemeFileSaverImpl) : MemeFileSaver

}
