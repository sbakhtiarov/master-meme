package com.devcampus.create_meme.ui.model

sealed interface EditorAction {
    data class DecorAdded(val decor: MemeDecor) : EditorAction
    data class DecorRemoved(val decor: MemeDecor) : EditorAction
    data class DecorMoved(val decor: MemeDecor) : EditorAction
    data class DecorUpdated(val decor: MemeDecor) : EditorAction
}