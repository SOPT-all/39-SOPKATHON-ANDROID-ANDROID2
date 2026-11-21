package org.sopt.sopkathon_android2.presentation.profilecreate

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileCreateViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileCreateUiState(title = "ProfileCreate"))
    val uiState: StateFlow<ProfileCreateUiState> = _uiState.asStateFlow()
}
