package org.sopt.sopkathon_android2.presentation.colleagueselect

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ColleagueSelectViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ColleagueSelectUiState())
    val uiState: StateFlow<ColleagueSelectUiState> = _uiState.asStateFlow()

    // 필터 변경 시 선택 초기화
    fun selectPosition(position: String) {
        _uiState.update { it.copy(selectedPosition = position, selectedColleagueId = null) }
    }

    // 같은 카드를 다시 누르면 선택 해제
    fun toggleColleague(colleagueId: Long) {
        _uiState.update {
            it.copy(selectedColleagueId = if (it.selectedColleagueId == colleagueId) null else colleagueId)
        }
    }
}
