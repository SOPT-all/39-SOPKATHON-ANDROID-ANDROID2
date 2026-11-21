package org.sopt.sopkathon_android2.presentation.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState(title = "Feed"))
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()
}
