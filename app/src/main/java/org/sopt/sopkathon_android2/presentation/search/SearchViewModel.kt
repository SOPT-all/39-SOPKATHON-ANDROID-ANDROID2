package org.sopt.sopkathon_android2.presentation.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState(title = "Search"))
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
}
