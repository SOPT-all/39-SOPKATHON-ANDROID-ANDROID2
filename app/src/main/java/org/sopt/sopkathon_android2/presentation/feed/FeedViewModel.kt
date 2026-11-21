package org.sopt.sopkathon_android2.presentation.feed

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState.dummy)
    val uiState = _uiState.asStateFlow()

    fun selectScope(scope: FeedScope) {
        _uiState.update { it.copy(selectedScope = scope) }
    }

    fun toggleLike(postId: Long) {
        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.postId == postId) {
                        post.copy(
                            isLiked = !post.isLiked,
                            likeCount = post.likeCount + if (post.isLiked) -1 else 1,
                        )
                    } else {
                        post
                    }
                },
            )
        }
    }
}
