package com.devcampus.memes_list.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.memes_list.domain.MemesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class MemeListViewModel @Inject constructor(
    private val memesRepository: MemesRepository
) : ViewModel() {

    init {
        memesRepository.getMemes()
            .onEach { memes ->
                Log.d("Memes", "$memes")
            }.catch { error ->
                Log.e("MemeListViewModel", "Failed to load memes", error)
            }
            .launchIn(viewModelScope)
    }
}