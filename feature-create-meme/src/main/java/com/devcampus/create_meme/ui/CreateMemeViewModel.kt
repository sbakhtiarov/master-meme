package com.devcampus.create_meme.ui

import android.util.SizeF
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Density
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.create_meme.domain.Decor
import com.devcampus.create_meme.domain.MemeFileSaver
import com.devcampus.create_meme.ui.model.DecorType
import com.devcampus.create_meme.ui.model.EditorAction
import com.devcampus.create_meme.ui.model.MemeDecor
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

    val decorItems = mutableStateListOf<MemeDecor>()

    val undoActions = mutableStateListOf<EditorAction>()
    val redoActions = mutableStateListOf<EditorAction>()

    val memePath = mutableStateOf<String?>(null)

    var isSaveInProgress = mutableStateOf(false)
        private set

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
                is Intent.OnSaveMeme ->
                    saveMeme(
                        assetPath = intent.assetPath,
                        canvasSize = intent.canvasSize,
                        density = intent.density,
                        saveToCache = false,
                        onSuccess = {
                            memePath.value = it
                            sendAction(CloseScreen)
                        }
                    )
                is Intent.OnShareMeme ->
                    saveMeme(
                        assetPath = intent.assetPath,
                        canvasSize = intent.canvasSize,
                        density = intent.density,
                        saveToCache = true,
                        onSuccess = { sendAction(Share(it)) }
                    )
                is Intent.OnDecorAdded -> addDecor(intent.decor)
                is Intent.OnDecorDeleted -> deleteDecor(intent.id)
                is Intent.OnDecorMoved -> moveDecor(intent.id, intent.offset)
                is Intent.OnDecorUpdated -> updateDecor(intent.decor)
                Intent.Undo -> undoAction()
                Intent.Redo -> redoAction()
            }
        }.launchIn(viewModelScope)
    }

    private fun undoAction() {
        undoActions.removeLastOrNull()?.let { action ->
            when (action) {
                is EditorAction.DecorAdded -> deleteDecor(action.decor.id, undoAction = false)
                is EditorAction.DecorRemoved -> addDecor(action.decor, undoAction = false)
                is EditorAction.DecorMoved -> moveDecor(action.decor.id, action.decor.topLeft, undoAction = false)
                is EditorAction.DecorUpdated -> updateDecor(action.decor, undoAction = false)
            }
        }
    }

    private fun redoAction() {
        redoActions.removeLastOrNull()?.let { action ->
            when (action) {
                is EditorAction.DecorAdded -> deleteDecor(action.decor.id)
                is EditorAction.DecorRemoved -> addDecor(action.decor)
                is EditorAction.DecorMoved -> moveDecor(action.decor.id, action.decor.topLeft)
                is EditorAction.DecorUpdated -> updateDecor(action.decor)
            }
        }
    }

    private fun addDecor(decor: MemeDecor, undoAction: Boolean = true) {
        decorItems.add(decor)
        updateUndoRedo(EditorAction.DecorAdded(decor), undoAction)
    }

    private fun updateDecor(newDecor: MemeDecor, undoAction: Boolean = true) {
        decorItems.find { it.id == newDecor.id }?.let { decor ->
            decorItems.set(
                index = decorItems.indexOf(decor),
                element = newDecor
            )
            if (newDecor.type != decor.type) {
                updateUndoRedo(EditorAction.DecorUpdated(decor), undoAction)
            }
        }
    }

    private fun deleteDecor(id: String, undoAction: Boolean = true) {
        decorItems.find { it.id == id }?.let { decor ->
            decorItems.remove(decor)
            updateUndoRedo(EditorAction.DecorRemoved(decor), undoAction)
        }
    }

    private fun moveDecor(id: String, offset: Offset, undoAction: Boolean = true) {
        decorItems.indexOfFirst { it.id == id }.takeIf { it >= 0 }?.let { index ->

            val decor = decorItems[index]

            decorItems.set(
                index = index,
                element = decor.copy(topLeft = offset)
            )

            updateUndoRedo(EditorAction.DecorMoved(decor), undoAction)
        }
    }

    private fun updateUndoRedo(action: EditorAction, undoAction: Boolean) {
        if (undoAction) {
            undoActions.add(action)
        } else {
            redoActions.add(action)
        }
    }

    private fun saveMeme(
        assetPath: String,
        canvasSize: Size,
        density: Density,
        saveToCache: Boolean,
        onSuccess: (String) -> Unit
    ) {

        isSaveInProgress.value = true

        viewModelScope.launch {
            memeFileSaver.prepareMemeImage(
                assetPath = assetPath,
                editorCanvasSize = SizeF(canvasSize.width, canvasSize.height),
                decorList = decorItems.map { item ->
                    when (item.type) {
                        is DecorType.TextDecor -> Decor.TextDecor(
                            topLeft = item.topLeft,
                            text = item.type.text,
                            fontResId = item.type.fontFamily.fontResId,
                            fontSize = with(density) { item.type.fontFamily.baseFontSize.toPx() * item.type.fontScale },
                            color = item.type.fontColor.toArgb(),
                            isStroke = item.type.fontFamily.isStroke,
                        )
                    }
                },
                saveToCache = saveToCache,
            )
                .onSuccess { path ->
                    isSaveInProgress.value = false
                    onSuccess(path)
                }
                .onFailure {
                    isSaveInProgress.value = false
                    sendAction(ShowMemeCreateError)
                }
        }
    }

    private fun sendAction(action: Action) {
        viewModelScope.launch { _actions.send(action) }
    }
}

internal sealed interface Action
data object ShowLeaveConfirmation : Action
data object ShowMemeCreateError : Action
data object CloseScreen : Action
data class Share(val path: String) : Action

internal sealed interface Intent {
    data object OnBackPress : Intent
    data class OnSaveMeme(val assetPath: String, val canvasSize: Size, val density: Density) : Intent
    data class OnShareMeme(val assetPath: String, val canvasSize: Size, val density: Density) : Intent
    data class OnDecorAdded(val decor: MemeDecor) : Intent
    data class OnDecorUpdated(val decor: MemeDecor) : Intent
    data class OnDecorDeleted(val id: String) : Intent
    data class OnDecorMoved(val id: String, val offset: Offset) : Intent
    data object Undo : Intent
    data object Redo : Intent
}
