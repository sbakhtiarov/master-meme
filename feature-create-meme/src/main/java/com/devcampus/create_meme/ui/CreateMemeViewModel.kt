package com.devcampus.create_meme.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.create_meme.domain.MemeFileSaver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class CreateMemeViewModel @Inject constructor(
    private val memeFileSaver: MemeFileSaver,
): ViewModel() {

    private val intents = MutableSharedFlow<Intent>()

    private val _actions = Channel<Action>(
        capacity = Channel.BUFFERED,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    val actions = _actions.receiveAsFlow()

    init {
        handleIntents()
    }

    fun onIntent(intent: Intent) {
        viewModelScope.launch {
            intents.emit(intent)
        }
    }

    private fun handleIntents() {
        intents.onEach { intent ->
            when (intent) {
                Intent.OnBackPress -> sendAction(ShowLeaveConfirmation)
                is Intent.OnSaveMeme -> saveMeme(intent.assetPath)
            }
        }.launchIn(viewModelScope)
    }

    private fun saveMeme(assetPath: String) {
        viewModelScope.launch {
            memeFileSaver.copyMemeAsset(assetPath)
            sendAction(CloseScreen)
        }
    }

    private fun sendAction(action: Action) {
        viewModelScope.launch { _actions.send(action) }
    }

}

internal sealed interface Action
data object ShowLeaveConfirmation : Action
data object CloseScreen : Action

internal sealed interface Intent {
    data object OnBackPress : Intent
    data class OnSaveMeme(val assetPath: String) : Intent
}
