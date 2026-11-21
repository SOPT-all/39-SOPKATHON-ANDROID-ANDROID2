package org.sopt.sopkathon_android2.presentation.search

data class SearchUiState(
    val popularPosts: List<PopularPost> = emptyList(),
)

data class PopularPost(
    val postId: Long,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
)
