package com.devcampus.memes_list.ui

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.memes_list.domain.MemesRepository
import com.devcampus.memes_list.domain.model.MemeFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MemeListViewModel @Inject constructor(
    private val memesRepository: MemesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewState>(Loading)
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val intents = MutableSharedFlow<Intent>()

    init {
        handleIntents()
        loadMemes()
    }

    private fun handleIntents() {
        intents.onEach { intent ->
            when (intent) {
                is Intent.OnMemeClick -> handleMemeClick(intent.memeFile)
                is Intent.OnMemeLongClick -> handleMemeLongClick(intent.memeFile)
                Intent.OnBackPress -> handleBackPress()
                Intent.OnSelectionDelete -> handleDeleteSelection()
                Intent.OnSelectionShare -> handleShareSelection()
            }
        }.launchIn(viewModelScope)
    }

    private fun handleShareSelection() {

    }

    private fun handleDeleteSelection() {
        (_state.value as? DataState)?.selection?.let { selection ->
            viewModelScope.launch {
                memesRepository.delete(selection)
                    .onSuccess {
                        updateState<DataState> { copy(selection = null) }
                    }
                    .onFailure { error ->
                        // TODO: Show error message
                        Log.e("MemeListViewModel", "Failed to delete files", error)
                    }
            }
        }
    }

    private fun handleBackPress() {
        updateState<DataState> {
            copy(selection = null)
        }
    }

    private fun handleMemeClick(memeFile: MemeFile) {
        (_state.value as? DataState)?.selection?.let { selection ->
            updateState<DataState> {
                val newSelection = if (selection.contains(memeFile)) {
                    (selection - memeFile).ifEmpty { null }
                } else {
                    selection + memeFile
                }

                copy(selection = newSelection)
            }
        }
    }

    private fun handleMemeLongClick(memeFile: MemeFile) {
        val viewState = _state.value as? DataState

        if (viewState?.selection == null) {
            updateState<DataState> {
                copy(selection = listOf(memeFile))
            }
        }
    }

    fun onIntent(intent: Intent) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    private fun loadMemes() {
        memesRepository.getMemes()
            .onEach { memes ->
                _state.update {
                    if (memes.isEmpty()) {
                        EmptyState
                    } else {
                        DataState(memes)
                    }
                }
            }.catch { error ->
                Log.e("MemeListViewModel", "Failed to load memes", error)
                _state.update { Error }
            }
            .launchIn(viewModelScope)
    }

    private inline fun <reified S : ViewState> updateState(block: S.() -> S) {
        val currentState = _state.value
        if (currentState is S) {
            _state.update {
                block(currentState)
            }
        }
    }
}

internal sealed interface ViewState
data object Loading : ViewState
data object Error : ViewState
data object EmptyState : ViewState

@Immutable
internal data class DataState(
    val memes: List<MemeFile> = emptyList(),
    val selection: List<MemeFile>? = null,
) : ViewState

internal sealed interface Intent {
    data class OnMemeClick(val memeFile: MemeFile) : Intent
    data class OnMemeLongClick(val memeFile: MemeFile) : Intent
    data object OnBackPress : Intent
    data object OnSelectionShare : Intent
    data object OnSelectionDelete : Intent
}