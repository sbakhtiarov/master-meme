package com.devcampus.memes_list.ui

import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.memes_list.domain.FavouriteMemesRepository
import com.devcampus.memes_list.domain.GetMemesUseCase
import com.devcampus.memes_list.domain.MemesRepository
import com.devcampus.memes_list.domain.SortModeRepository
import com.devcampus.memes_list.domain.model.Meme
import com.devcampus.memes_list.domain.model.MemeFile
import com.devcampus.memes_list.domain.model.SortMode
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MemeListViewModel @Inject constructor(
    private val getMemesUseCase: GetMemesUseCase,
    private val memesRepository: MemesRepository,
    private val favouritesRepository: FavouriteMemesRepository,
    private val sortModeRepository: SortModeRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ViewState>(Loading)
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val intents = MutableSharedFlow<Intent>()

    private val _actions = Channel<Action>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val actions = _actions.receiveAsFlow()

    val sortMode: StateFlow<SortMode> = sortModeRepository.getSortMode()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), SortMode.NEWEST_FIRST)

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
                is Intent.OnSortModeSelected -> handleSortModeSelection(intent.selection)
            }
        }.launchIn(viewModelScope)
    }

    private fun handleSortModeSelection(selection: Int) {
        viewModelScope.launch {
            sortModeRepository.setSortMode(SortMode.entries[selection])
        }
    }

    private fun handleOnFavouriteClick(meme: Meme) {
        viewModelScope.launch {
            favouritesRepository.setFavourite(
                memeFilePath = meme.path,
                isFavourite = !meme.isFavourite
            )
        }
    }

    private fun handleShareSelection() {
        withSelection { selection ->
            if (selection.isNotEmpty()) {
                sendAction(Share(paths = selection.map { it.path }))
            }
        }
    }

    private fun handleDeleteConfirmation() {
        withSelection { selection ->
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
        if (isInSelectionMode()) {
            withSelection { selection ->
                updateState<DataState> {
                    val newSelection = if (selection.contains(meme)) {
                        (selection - meme).ifEmpty { null }
                    } else {
                        selection + meme
                    }

                    copy(selection = newSelection)
                }
            }
        } else {
            sendAction(ShowMeme(meme))
        }
    }

    private fun handleMemeLongClick(meme: Meme) {
        if (!isInSelectionMode()) {
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

    private fun withSelection(block: (List<Meme>) -> Unit) {
        (_state.value as? DataState)?.selection?.let { block(it) }
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

    private fun isInSelectionMode() = _state.value.isInSelectionMode()
}

@Immutable
internal sealed interface ViewState
data object Loading : ViewState
data object Error : ViewState
data object EmptyState : ViewState

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
    data class OnSortModeSelected(val selection: Int) : Intent
}

internal sealed interface Action
data object ShowErrorMessage : Action
data object ShowDeletionConfirmation : Action
data class Share(val paths: List<String>) : Action
data class ShowMeme(val meme: Meme) : Action

internal fun ViewState.isInSelectionMode() = (this as? DataState)?.selection != null