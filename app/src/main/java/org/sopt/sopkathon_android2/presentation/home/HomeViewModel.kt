package org.sopt.sopkathon_android2.presentation.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(title = "Home"))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
