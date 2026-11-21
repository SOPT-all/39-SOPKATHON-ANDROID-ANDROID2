package org.sopt.sopkathon_android2.presentation.dressup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DressUpViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DressUpUiState(title = "DressUp"))
    val uiState: StateFlow<DressUpUiState> = _uiState.asStateFlow()
}
