package org.sopt.sopkathon_android2.data.service

import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto

// 프로필 응답에 작성한 글 목록이 없어서 최신 피드에서 작성자로 골라냄
suspend fun FeedApiService.getPostsByAuthor(
    viewerId: Long,
    authorId: Long,
    postCount: Int,
): List<FeedPostResponseDto> {
    if (postCount <= 0) return emptyList()
    val posts = mutableListOf<FeedPostResponseDto>()
    var cursor: String? = null
    repeat(MAX_PAGE_REQUESTS) {
        val page = requireNotNull(getFeed(userId = viewerId, cursor = cursor, limit = PAGE_SIZE).data)
        posts += page.items.filter { it.author.userId == authorId }
        // 다 찾았거나 마지막 페이지면 중단
        if (posts.size >= postCount || !page.hasMore || page.nextCursor == null || page.nextCursor == cursor) {
            return posts.distinctBy { it.postId }
        }
        cursor = page.nextCursor
    }
    return posts.distinctBy { it.postId }
}

private const val PAGE_SIZE = 50
private const val MAX_PAGE_REQUESTS = 5
