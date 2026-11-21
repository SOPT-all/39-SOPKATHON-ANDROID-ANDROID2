package org.sopt.sopkathon_android2.presentation.feed

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.presentation.feed.model.DummyFeedPageSource
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope

class FeedViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private var pageJob: Job? = null
    private var generation = 0

    init {
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
        val requestGeneration = generation
        _uiState.update { it.copy(isLoading = true, loadError = null) }
        pageJob = viewModelScope.launch {
            try {
                val page = DummyFeedPageSource.getPage(
                    scope = state.selectedScope,
                    cursor = if (isRefresh) null else state.nextCursor,
                    limit = 5,
                )
                if (requestGeneration != generation) return@launch
                _uiState.update {
                    it.copy(
                        posts = (if (isRefresh) page.items else it.posts + page.items)
                            .distinctBy { post -> post.postId },
                        nextCursor = page.nextCursor,
                        hasMore = page.hasMore && page.nextCursor != null &&
                            page.nextCursor != state.nextCursor && page.items.isNotEmpty(),
                        isLoading = false,
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
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
