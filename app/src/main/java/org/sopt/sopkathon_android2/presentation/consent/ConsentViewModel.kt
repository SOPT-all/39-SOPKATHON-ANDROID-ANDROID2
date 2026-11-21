package org.sopt.sopkathon_android2.presentation.consent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ConsentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConsentUiState(title = "Consent"))
    val uiState: StateFlow<ConsentUiState> = _uiState.asStateFlow()
}
