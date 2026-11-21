package org.sopt.sopkathon_android2.presentation.search.searchinput

data class SearchInputUiState(
    val query: String = "",
    val results: List<SearchColleague> = emptyList(),
)

data class SearchColleague(
    val userId: Long,
    val name: String,
    val rank: String,
    val department: String,
    val profileImageUrl: String,
)
