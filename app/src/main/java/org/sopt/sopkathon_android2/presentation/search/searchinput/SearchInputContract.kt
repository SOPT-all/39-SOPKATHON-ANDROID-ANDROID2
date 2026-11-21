package org.sopt.sopkathon_android2.presentation.search.searchinput

import org.sopt.sopkathon_android2.data.dto.response.SearchUserResponseDto

data class SearchInputUiState(
    val query: String = "",
    val results: List<SearchColleague> = emptyList(),
    val recentQueries: List<String> = emptyList(),
    val nextCursor: String? = null,
    val isLoading: Boolean = false,
    val hasError: Boolean = false,
)

data class SearchColleague(
    val userId: Long,
    val name: String,
    val rank: String,
    val department: String,
    val profileImageUrl: String,
)

internal fun SearchUserResponseDto.toUiModel() = SearchColleague(
    userId = userId,
    name = name,
    rank = position,
    department = department,
    profileImageUrl = profileImageUrl.orEmpty(),
)
