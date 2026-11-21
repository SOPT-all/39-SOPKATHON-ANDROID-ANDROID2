package org.sopt.sopkathon_android2.presentation.colleagueselect.model

import org.sopt.sopkathon_android2.data.dto.response.SearchUserResponseDto

data class ColleagueUiModel(
    val id: Long,
    val name: String,
    val position: String,
    val team: String,
    val profileImageUrl: String,
)

fun SearchUserResponseDto.toColleagueUiModel() = ColleagueUiModel(
    id = userId,
    name = name,
    position = position,
    team = department,
    profileImageUrl = profileImageUrl.orEmpty(),
)
