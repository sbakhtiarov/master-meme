package com.devcampus.meme_templates.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.meme_templates.domain.MemeTemplatesRepository
import com.devcampus.meme_templates.domain.model.MemeTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.updateAndGet
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MemeTemplatesViewModel @Inject constructor(
    private val repository: MemeTemplatesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewState>(ViewState())
    val state: StateFlow<ViewState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.updateAndGet { s ->
                s.copy(templates = repository.getTemplates())
            }
        }
    }
}

internal data class ViewState(val templates: List<MemeTemplate> = emptyList())
