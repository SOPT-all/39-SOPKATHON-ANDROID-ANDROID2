package org.sopt.sopkathon_android2.presentation.colleagueprofile

import org.sopt.sopkathon_android2.presentation.colleagueprofile.model.ColleagueOutfitUiModel

data class ColleagueProfileUiState(
    val name: String = "",
    val rank: String = "",
    val department: String = "",
    val profileImageUrl: String = "",
    val receivedOutfits: List<ColleagueOutfitUiModel> = emptyList(),
    val createdOutfits: List<ColleagueOutfitUiModel> = emptyList(),
    val isLoading: Boolean = false,
) {
    // 섹션 제목용 성 뺀 이름 (박정훈 → 정훈)
    val firstName: String
        get() = if (name.length >= 3) name.drop(1) else name

    companion object {
        // Preview용
        val dummy = ColleagueProfileUiState(
            name = "박정훈",
            rank = "부장",
            department = "영업1팀",
            receivedOutfits = listOf(
                ColleagueOutfitUiModel(1L, "행복한 불금룩", "", 18),
                ColleagueOutfitUiModel(2L, "일하기 싫어룩", "", 12),
            ),
        )
    }
}

sealed interface ColleagueProfileUiEffect {
    data class ShowToast(val message: String) : ColleagueProfileUiEffect
}
