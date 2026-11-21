package org.sopt.sopkathon_android2.presentation.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope
import org.sopt.sopkathon_android2.presentation.feed.model.toUiModel

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private var pageJob: Job? = null
    private var generation = 0

    private var userId: Long? = null

    fun setUserId(userId: Long?) {
        if (this.userId == userId && (_uiState.value.isLoading || _uiState.value.posts.isNotEmpty())) return
        this.userId = userId
        refresh()
    }

    fun selectScope(scope: FeedScope) {
        if (_uiState.value.selectedScope == scope) return
        refresh(scope)
    }

    fun refresh(scope: FeedScope = _uiState.value.selectedScope) {
        pageJob?.cancel()
        generation++
        _uiState.value = FeedUiState(selectedScope = scope)
        requestPage(isRefresh = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || !state.hasMore || state.loadError != null) return
        requestPage(isRefresh = false)
    }

    fun retry() {
        val state = _uiState.value
        if (state.isLoading) return
        requestPage(isRefresh = state.posts.isEmpty())
    }

    private fun requestPage(isRefresh: Boolean) {
        val state = _uiState.value
        val currentUserId = userId?.takeIf { it > 0 } ?: run {
            _uiState.update { it.copy(isLoading = false, loadError = "사용자 정보가 필요해요") }
            return
        }
        val requestGeneration = generation
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        pageJob = viewModelScope.launch {
            suspendRunCatching {
                val popular = state.selectedScope == FeedScope.POPULAR
                val response = ServiceModule.apiService.getFeed(
                    userId = currentUserId,
                    sort = if (popular) "POPULAR" else "LATEST",
                    period = if (popular) "WEEK" else null,
                    cursor = if (isRefresh) null else state.nextCursor,
                    limit = 20,
                )
                check(response.status in 200..299) { response.code }
                requireNotNull(response.data)
            }.onSuccess { page ->
                if (requestGeneration != generation) return@onSuccess
                val posts = page.items.map { it.toUiModel() }
                _uiState.update {
                    it.copy(
                        posts = (if (isRefresh) posts else it.posts + posts)
                            .distinctBy { post -> post.postId },
                        nextCursor = page.nextCursor,
                        hasMore = page.hasMore && page.nextCursor != null &&
                            page.nextCursor != state.nextCursor && page.items.isNotEmpty(),
                        isLoading = false,
                    )
                }
            }.onFailure { throwable ->
                Log.e("FeedViewModel", "피드 조회 실패", throwable)
                if (requestGeneration == generation) {
                    _uiState.update {
                        it.copy(isLoading = false, loadError = "목록을 불러오지 못했어요")
                    }
                }
            }
        }
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
