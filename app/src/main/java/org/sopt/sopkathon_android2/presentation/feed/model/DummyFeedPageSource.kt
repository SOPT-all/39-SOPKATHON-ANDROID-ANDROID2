package org.sopt.sopkathon_android2.presentation.feed.model

import kotlinx.coroutines.delay
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.presentation.feed.FeedUiState
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope

internal object DummyFeedPageSource {
    private const val TOTAL_COUNT = 15

    suspend fun getPage(
        scope: FeedScope,
        cursor: String?,
        limit: Int,
    ): CursorResponseDto<FeedPostUiModel> {
        delay(500)
        val start = cursor?.toIntOrNull() ?: 0
        require(start in 0..TOTAL_COUNT && limit > 0)
        val end = (start + limit).coerceAtMost(TOTAL_COUNT)
        val templates = FeedUiState.dummy.posts
        val posts = (start until end).map { index ->
            val template = templates[index % templates.size]
            template.copy(
                postId = 501L + index,
                scopeLabel = if (scope == FeedScope.COMPANY) "회사 전체" else "우리 부서",
                likeCount = if (scope == FeedScope.POPULAR) 100 - index else template.likeCount,
            )
        }
        return CursorResponseDto(
            items = posts,
            nextCursor = end.takeIf { it < TOTAL_COUNT }?.toString(),
            hasMore = end < TOTAL_COUNT,
        )
    }
}
