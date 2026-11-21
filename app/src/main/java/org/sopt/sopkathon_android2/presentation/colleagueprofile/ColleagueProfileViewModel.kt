package org.sopt.sopkathon_android2.presentation.colleagueprofile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColleagueProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ColleagueProfileUiState(title = "ColleagueProfile"))
    val uiState: StateFlow<ColleagueProfileUiState> = _uiState.asStateFlow()
}
