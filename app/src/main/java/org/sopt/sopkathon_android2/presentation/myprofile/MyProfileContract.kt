package org.sopt.sopkathon_android2.presentation.myprofile

import org.sopt.sopkathon_android2.presentation.myprofile.model.MyProfileOutfitUiModel

data class MyProfileUiState(
    val name: String = "",
    val rank: String = "",
    val department: String = "",
    val profileImageUrl: String = "",
    val myOutfits: List<MyProfileOutfitUiModel> = emptyList(),
    val createdOutfits: List<MyProfileOutfitUiModel> = emptyList(),
    val isLoading: Boolean = false,
) {
    companion object {
        // Preview용
        val dummy = MyProfileUiState(
            name = "김민지",
            rank = "대리",
            department = "마케팅팀",
            myOutfits = listOf(
                MyProfileOutfitUiModel(1L, "행복한 불금룩", "", 18),
                MyProfileOutfitUiModel(2L, "일하기 싫어룩", "", 12),
            ),
            createdOutfits = emptyList(),
        )
    }
}

sealed interface MyProfileUiEffect {
    data class ShowToast(val message: String) : MyProfileUiEffect
}
