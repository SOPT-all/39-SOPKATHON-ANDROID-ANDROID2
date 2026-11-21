package org.sopt.sopkathon_android2.presentation.consent

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ConsentViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ConsentUiState())
    val uiState: StateFlow<ConsentUiState> = _uiState.asStateFlow()

    fun toggleConsent() {
        _uiState.update { it.copy(isConsentChecked = !it.isConsentChecked) }
    }
}
