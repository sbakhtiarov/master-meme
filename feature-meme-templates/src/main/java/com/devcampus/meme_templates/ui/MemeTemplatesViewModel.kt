package com.devcampus.meme_templates.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.devcampus.meme_templates.domain.MemeTemplatesRepository
import com.devcampus.meme_templates.domain.model.MemeTemplate
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.filter

@OptIn(FlowPreview::class)
@HiltViewModel
internal class MemeTemplatesViewModel @Inject constructor(
    private val repository: MemeTemplatesRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ViewState>(ViewState())
    val state: StateFlow<ViewState> = _state.asStateFlow()

    private val searchQuery = MutableStateFlow<String>("")

    init {
        viewModelScope.launch {

            val templates = repository.getTemplates()

            _state.update { s ->
                ViewState(templates = templates)
            }

            observeSearchQuery(templates)
        }
    }

    private fun observeSearchQuery(templates: List<MemeTemplate>) {
        searchQuery.debounce(300).onEach { query ->

            val filtered = if (query.isEmpty()) {
                templates
            } else {
                templates.filter {
                    it.path.contains(query, ignoreCase = true)
                }
            }

            _state.update { s ->
                ViewState(templates = filtered)
            }
        }.launchIn(viewModelScope)
    }

    fun onQueryUpdate(string: String) {
        searchQuery.update { string }
    }
}

internal data class ViewState(val templates: List<MemeTemplate> = emptyList())
