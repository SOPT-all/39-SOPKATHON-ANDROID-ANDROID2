package org.sopt.sopkathon_android2.presentation.colleagueselect

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColleagueSelectViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ColleagueSelectUiState(title = "ColleagueSelect"))
    val uiState: StateFlow<ColleagueSelectUiState> = _uiState.asStateFlow()
}
