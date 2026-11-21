package org.sopt.sopkathon_android2.presentation.postdetail

import androidx.lifecycle.ViewModelStore
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CompletableDeferred
import org.junit.Assert.*
import org.junit.Test
import org.sopt.sopkathon_android2.core.designsystem.component.post.OutfitItemType
import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.dto.request.CommentCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.response.*
import org.sopt.sopkathon_android2.data.service.PostDetailApiService
import java.lang.reflect.Proxy

class PostDetailTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun itemSelectionAndCommentPaginationDoNotDuplicateOrLoop() {
        val api = DetailApi()
        withViewModel(api) { vm ->
            instrumentation.runOnMainSync {
                assertNotNull(vm.uiState.value.detail)
                vm.selectTag(OutfitItemType.TOP)
                vm.openItem(10)
                assertEquals(PostDetailSheet.Item(10), vm.uiState.value.sheet)
                vm.dismissSheet()
                vm.openComments()
                assertEquals(2, vm.uiState.value.comments.size)
                vm.loadComments()
                assertEquals(listOf(1L, 2L, 3L), vm.uiState.value.comments.map { it.commentId })
                vm.loadComments()
                assertEquals(2, api.reads)
                assertFalse(vm.uiState.value.hasMoreComments)
            }
        }
    }

    @Test
    fun failedCommentPreservesDraftAndRetryUpdatesServerCount() {
        val api = DetailApi()
        withViewModel(api) { vm ->
            instrumentation.runOnMainSync {
                vm.openComments()
                vm.updateDraft("  댓글 내용  ")
                vm.postComment()
                vm.postComment()
                assertEquals(1, api.writes)
                assertEquals("댓글 내용", api.sentContent)
            }
            api.created.completeExceptionally(IllegalStateException("offline"))
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                assertEquals("  댓글 내용  ", vm.uiState.value.draft)
                assertFalse(vm.uiState.value.isPosting)
                api.created = CompletableDeferred()
                vm.postComment()
            }
            api.created.complete(BaseResponse(200, "OK", "", CommentCreateResponseDto(4, 501, "댓글 내용", "2026-11-22T10:00:00Z", 4)))
            instrumentation.waitForIdleSync()
            instrumentation.runOnMainSync {
                assertEquals("", vm.uiState.value.draft)
                assertEquals(4, vm.uiState.value.detail!!.post.commentCount)
                assertFalse(vm.uiState.value.isPosting)
                assertEquals(2, api.writes)
            }
        }
    }

    @Test
    fun likeUsesServerCountAndBlocksDuplicateClick() {
        val api = DetailApi()
        withViewModel(api) { vm ->
            instrumentation.runOnMainSync {
                vm.toggleLike()
                vm.toggleLike()
                assertEquals(1, api.likes)
                assertTrue(vm.uiState.value.detail!!.post.isLiked)
            }
            api.liked.complete(BaseResponse(200, "OK", "", PostLikeResponseDto(501, true, 25)))
            instrumentation.waitForIdleSync()
            assertEquals(25, vm.uiState.value.detail!!.post.likeCount)
        }
    }

    private fun withViewModel(api: DetailApi, block: (PostDetailViewModel) -> Unit) {
        val store = ViewModelStore()
        lateinit var vm: PostDetailViewModel
        instrumentation.runOnMainSync {
            vm = PostDetailViewModel(api)
            store.put("detail", vm)
            vm.load(1, 501)
        }
        try { block(vm) } finally { instrumentation.runOnMainSync { store.clear() } }
    }

    private class DetailApi : PostDetailApiService by unusedApi() {
        var reads = 0
        var writes = 0
        var likes = 0
        var sentContent = ""
        var created = CompletableDeferred<BaseResponse<CommentCreateResponseDto>>()
        val liked = CompletableDeferred<BaseResponse<PostLikeResponseDto>>()

        override suspend fun getPostDetail(userId: Long, postId: Long) = BaseResponse(200, "OK", "", PostDetailResponseDto(
            postId = 501,
            author = FeedUserResponseDto(2, "김민지", "대리"),
            targetUser = FeedUserResponseDto(1, "박정훈", "과장"),
            imageUrl = "", title = "오늘의 회식룩", description = "네이비 니트 코디",
            outfitItems = listOf(OutfitItemResponseDto(10, "네이비 니트", "TOP", "", "https://example.com")),
            likeCount = 18, liked = false, commentCount = 3, createdAt = "2026-11-22T10:00:00Z",
        ))

        override suspend fun getPostComments(userId: Long, postId: Long, cursor: String?, limit: Int, requesterId: Long): BaseResponse<CursorResponseDto<CommentResponseDto>> {
            reads++
            val ids = if (cursor == null) listOf(1L, 2L) else listOf(2L, 3L)
            return BaseResponse(200, "OK", "", CursorResponseDto(ids.map {
                CommentResponseDto(it, FeedUserResponseDto(1, "박정훈", "과장"), "좋은 코디네요", "2026-11-22T10:00:00Z")
            }, if (cursor == null) "page2" else null, cursor == null))
        }

        override suspend fun postPostComment(userId: Long, postId: Long, request: CommentCreateRequestDto, requesterId: Long): BaseResponse<CommentCreateResponseDto> {
            writes++
            sentContent = request.content
            return created.await()
        }

        override suspend fun postPostLike(userId: Long, postId: Long): BaseResponse<PostLikeResponseDto> {
            likes++
            return liked.await()
        }
    }
}

private fun unusedApi(): PostDetailApiService = Proxy.newProxyInstance(
    PostDetailApiService::class.java.classLoader, arrayOf(PostDetailApiService::class.java),
) { _, method, _ -> error("Unexpected API: ${method.name}") } as PostDetailApiService
