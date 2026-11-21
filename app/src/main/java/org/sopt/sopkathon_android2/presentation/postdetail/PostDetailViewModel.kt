package org.sopt.sopkathon_android2.presentation.postdetail

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostDetailViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState(title = "PostDetail"))
    val uiState: StateFlow<PostDetailUiState> = _uiState.asStateFlow()
}
