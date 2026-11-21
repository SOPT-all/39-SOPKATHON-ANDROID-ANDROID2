package org.sopt.sopkathon_android2.presentation.feed

import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.presentation.feed.model.toUiModel

class FeedPostMapperTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun mapsServerLikeStateAndDoesNotInventMissingMetadata() {
        val response = json.decodeFromString<BaseResponse<CursorResponseDto<FeedPostResponseDto>>>(
            """{
                "status":200,"code":"FEED_RETRIEVED","message":"ok",
                "data":{"items":[{
                    "postId":501,
                    "author":{"userId":202,"name":"김민지","position":" 대리"},
                    "targetUser":{"userId":101,"name":"박정훈","position":"과장"},
                    "imageUrl":"image.jpg","title":"금요일 회식룩","description":"본문",
                    "purchaseLinks":[{"url":"https://example.com/item"}],
                    "likeCount":18,"liked":true,"commentCount":3,
                    "createdAt":"2026-11-22T10:30:00+09:00"
                }],"nextCursor":"opaque-cursor","hasMore":true}
            }""",
        )
        val item = requireNotNull(response.data).items.single().toUiModel()
        assertEquals("대리", item.author.position)
        assertEquals("박정훈 과장님 코디", item.targetLabel)
        assertEquals("본문", item.content)
        assertEquals("", item.hashtag)
        assertEquals("", item.scopeLabel)
        assertTrue(item.isLiked)
        assertEquals("opaque-cursor", response.data?.nextCursor)
    }

    @Test
    fun acceptsEmptyLastPage() {
        val page = json.decodeFromString<CursorResponseDto<FeedPostResponseDto>>(
            """{"items":[],"nextCursor":null,"hasMore":false}""",
        )
        assertTrue(page.items.isEmpty())
        assertFalse(page.hasMore)
        assertEquals(null, page.nextCursor)
    }
}
