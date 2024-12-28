package com.devcampus.create_meme.ui.model

sealed interface EditorAction {
    data class DecorAdded(val decor: UiDecor) : EditorAction
    data class DecorRemoved(val decor: UiDecor) : EditorAction
    data class DecorMoved(val decor: UiDecor) : EditorAction
    data class DecorUpdated(val decor: UiDecor) : EditorAction
}