package com.devcampus.memes_list.ui

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.memes_list.domain.FavouriteMemesRepository
import com.devcampus.memes_list.domain.GetMemesUseCase
import com.devcampus.memes_list.domain.MemesRepository
import com.devcampus.memes_list.domain.model.Meme
import com.devcampus.memes_list.domain.model.MemeFile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MemeListViewModel @Inject constructor(
    private val getMemesUseCase: GetMemesUseCase,
    private val memesRepository: MemesRepository,
    private val favouritesRepository: FavouriteMemesRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ViewState>(Loading)
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val intents = MutableSharedFlow<Intent>()

    private val _actions = Channel<Action>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val actions = _actions.receiveAsFlow()

    init {
        handleIntents()
        loadMemes()
    }

    private fun handleIntents() {
        intents.onEach { intent ->
            when (intent) {
                is Intent.OnMemeClick -> handleMemeClick(intent.meme)
                is Intent.OnMemeLongClick -> handleMemeLongClick(intent.meme)
                is Intent.OnBackPress -> handleBackPress()
                is Intent.OnSelectionDelete -> sendAction(ShowDeletionConfirmation)
                is Intent.OnSelectionShare -> handleShareSelection()
                is Intent.OnMemeFavouriteClick -> handleOnFavouriteClick(intent.meme)
                is Intent.OnDeleteConfirmed -> handleDeleteConfirmation()
            }
        }.launchIn(viewModelScope)
    }

    private fun handleOnFavouriteClick(meme: Meme) {
        viewModelScope.launch {
            favouritesRepository.setFavourite(
                MemeFile(meme.path),
                !meme.isFavourite
            )
        }
    }

    private fun handleShareSelection() {

    }

    private fun handleDeleteConfirmation() {
        (_state.value as? DataState)?.selection?.let { selection ->
            viewModelScope.launch {
                memesRepository.delete(selection.map { MemeFile(it.path) })
                    .onSuccess {
                        updateState<DataState> { copy(selection = null) }
                    }
                    .onFailure { error ->
                        Log.e("MemeListViewModel", "Failed to delete files", error)
                        sendAction(ShowErrorMessage)
                    }
            }
        }
    }

    private fun handleBackPress() {
        updateState<DataState> {
            copy(selection = null)
        }
    }

    private fun handleMemeClick(meme: Meme) {
        (_state.value as? DataState)?.selection?.let { selection ->
            updateState<DataState> {
                val newSelection = if (selection.contains(meme)) {
                    (selection - meme).ifEmpty { null }
                } else {
                    selection + meme
                }

                copy(selection = newSelection)
            }
        }
    }

    private fun handleMemeLongClick(meme: Meme) {
        val viewState = _state.value as? DataState

        if (viewState?.selection == null) {
            updateState<DataState> {
                copy(selection = listOf(meme))
            }
        }
    }

    fun onIntent(intent: Intent) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    private fun loadMemes() {
        getMemesUseCase.getMemes()
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

    private fun sendAction(action: Action) {
        viewModelScope.launch { _actions.send(action) }
    }
}

internal sealed interface ViewState
data object Loading : ViewState
data object Error : ViewState
data object EmptyState : ViewState

@Immutable
internal data class DataState(
    val memes: List<Meme> = emptyList(),
    val selection: List<Meme>? = null,
) : ViewState

internal sealed interface Intent {
    data class OnMemeClick(val meme: Meme) : Intent
    data class OnMemeLongClick(val meme: Meme) : Intent
    data class OnMemeFavouriteClick(val meme: Meme) : Intent
    data object OnBackPress : Intent
    data object OnSelectionShare : Intent
    data object OnSelectionDelete : Intent
    data object OnDeleteConfirmed : Intent
}

internal sealed interface Action
data object ShowErrorMessage : Action
data object ShowDeletionConfirmation : Action
