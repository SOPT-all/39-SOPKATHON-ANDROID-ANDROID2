package org.sopt.sopkathon_android2.presentation.feed.model

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

internal data class PostLikeUpdate(
    val userId: Long,
    val postId: Long,
    val liked: Boolean,
    val count: Int,
)

// 상세에서 확정된 좋아요를 백스택에 남은 피드에도 전달
internal object PostLikeUpdates {
    private val changes = MutableSharedFlow<PostLikeUpdate>(extraBufferCapacity = 64)
    val updates = changes.asSharedFlow()

    suspend fun publish(update: PostLikeUpdate) {
        changes.emit(update)
    }
}
