package org.sopt.sopkathon_android2.presentation.search

import org.sopt.sopkathon_android2.data.dto.response.FeedItemResponseDto

data class SearchUiState(
    val popularPosts: List<PopularPost> = emptyList(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
) {
    companion object {
        val preview = SearchUiState(
            popularPosts = listOf(
                PopularPost(1L, "금요일 회식룩", "", 18),
                PopularPost(2L, "편안한 출근룩", "", 12),
                PopularPost(3L, "봄맞이 산뜻룩", "", 9),
                PopularPost(4L, "워크숍 데일리룩", "", 7),
                PopularPost(5L, "발표하는 날", "", 5),
                PopularPost(6L, "캐주얼 금요일", "", 3),
            ),
            isLoading = false,
        )
    }
}

data class PopularPost(
    val postId: Long,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
)

internal fun FeedItemResponseDto.toUiModel() = PopularPost(
    postId = postId,
    title = title,
    imageUrl = imageUrl.orEmpty(),
    likeCount = likeCount,
)
