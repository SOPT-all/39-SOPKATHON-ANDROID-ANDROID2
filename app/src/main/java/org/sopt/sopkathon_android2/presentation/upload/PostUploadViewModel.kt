package org.sopt.sopkathon_android2.presentation.upload

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PostUploadViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PostUploadUiState(title = "PostUpload"))
    val uiState: StateFlow<PostUploadUiState> = _uiState.asStateFlow()
}
