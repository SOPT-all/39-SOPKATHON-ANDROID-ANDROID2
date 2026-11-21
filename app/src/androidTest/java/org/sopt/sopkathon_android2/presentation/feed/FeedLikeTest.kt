package org.sopt.sopkathon_android2.presentation.feed

import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedUserResponseDto
import org.sopt.sopkathon_android2.data.dto.response.PostLikeResponseDto
import org.sopt.sopkathon_android2.data.service.FeedApiService

class FeedLikeTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun blocksDuplicateClickAndKeepsServerCountAcrossRefresh() {
        val api = LikeApi()
        withViewModel(api) { vm ->
            instrumentation.runOnMainSync {
                vm.toggleLike(501)
                vm.toggleLike(501)
                assertEquals(1, api.posts)
                assertTrue(vm.uiState.value.posts.single().isLiked)
                vm.refresh()
                assertEquals(1, api.reads)
            }
            api.result.complete(BaseResponse(200, "OK", "", PostLikeResponseDto(501, true, 22)))
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                assertTrue(vm.uiState.value.posts.single().isLiked)
                assertEquals(22, vm.uiState.value.posts.single().likeCount)
                assertEquals(2, api.reads)
            }
        }
    }

    @Test
    fun failedUnlikeRestoresStateAndAllowsRetry() {
        val api = LikeApi(initialLiked = true)
        withViewModel(api) { vm ->
            instrumentation.runOnMainSync {
                vm.toggleLike(501)
                assertFalse(vm.uiState.value.posts.single().isLiked)
            }
            api.result.completeExceptionally(IllegalStateException("Network failure"))
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                assertTrue(vm.uiState.value.posts.single().isLiked)
                assertEquals(18, vm.uiState.value.posts.single().likeCount)
                api.result = CompletableDeferred()
                vm.toggleLike(501)
                assertEquals(2, api.deletes)
            }
            api.result.complete(BaseResponse(200, "OK", "", PostLikeResponseDto(501, false, 17)))
            instrumentation.waitForIdleSync()
            assertFalse(vm.uiState.value.posts.single().isLiked)
        }
    }

    private fun withViewModel(api: LikeApi, block: (FeedViewModel) -> Unit) {
        val store = ViewModelStore()
        lateinit var vm: FeedViewModel
        instrumentation.runOnMainSync {
            vm = FeedViewModel(api)
            store.put("feed", vm)
            vm.setUserId(1)
        }
        try {
            block(vm)
        } finally {
            instrumentation.runOnMainSync { store.clear() }
        }
    }

    private class LikeApi(private val initialLiked: Boolean = false) : FeedApiService {
        var reads = 0
        var posts = 0
        var deletes = 0
        var result = CompletableDeferred<BaseResponse<PostLikeResponseDto>>()




        override suspend fun getFeed(userId: Long, sort: String, period: String?, limit: Int, cursor: String?) =
            BaseResponse(200, "OK", "", CursorResponseDto(
                items = listOf(FeedPostResponseDto(
                    postId = 501,
                    author = FeedUserResponseDto(2, "작성자", "대리"),
                    targetUser = FeedUserResponseDto(1, "대상자", "과장"),
                    imageUrl = "", title = "제목", description = "본문",
                    likeCount = 18, liked = initialLiked, commentCount = 0,
                    createdAt = "2026-11-22T10:00:00+09:00",
                )),
                nextCursor = null,
                hasMore = false,
            )).also { reads++ }

        override suspend fun postPostLike(userId: Long, postId: Long): BaseResponse<PostLikeResponseDto> {
            posts++
            return result.await()
        }

        override suspend fun deletePostLike(userId: Long, postId: Long): BaseResponse<PostLikeResponseDto> {
            deletes++
            return result.await()
        }
    }
}
