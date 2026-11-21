package org.sopt.sopkathon_android2.presentation.colleagueselect

import org.sopt.sopkathon_android2.presentation.colleagueselect.model.ColleagueUiModel

const val ALL_POSITION = "전체"

data class ColleagueSelectUiState(
    val positions: List<String> = listOf(ALL_POSITION, "이사", "부장", "차장", "과장", "대리", "사원"),
    val selectedPosition: String = ALL_POSITION,
    val colleagues: List<ColleagueUiModel> = emptyList(),
    val selectedColleagueId: Long? = null,
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
) {
    val selectedColleague: ColleagueUiModel?
        get() = colleagues.find { it.id == selectedColleagueId }
}

sealed interface ColleagueSelectUiEffect {
    data object ShowErrorToast : ColleagueSelectUiEffect
}
