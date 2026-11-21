package org.sopt.sopkathon_android2.presentation.feed

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.dto.response.CursorResponseDto
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.service.ApiService
import org.sopt.sopkathon_android2.data.service.FeedApiService
import org.sopt.sopkathon_android2.data.di.FeedServiceModule
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope
import org.sopt.sopkathon_android2.presentation.feed.model.toUiModel
import org.sopt.sopkathon_android2.presentation.feed.model.PostLikeUpdates

class FeedViewModel(
    private val apiService: FeedApiService = FeedServiceModule.apiService,
    private val userApiService: ApiService = ServiceModule.apiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState = _uiState.asStateFlow()

    private var pageJob: Job? = null
    private var generation = 0

    private var userId: Long? = null
    private val _uiEffect = Channel<FeedUiEffect>(Channel.BUFFERED)
    val uiEffect = _uiEffect.receiveAsFlow()
    private val likeJobs = mutableMapOf<Pair<Long, Long>, Job>()
    private val likeStates = mutableMapOf<Pair<Long, Long>, Pair<Boolean, Int>>()

    // 피드 응답에 부서가 없어 사용자 조회로 채운 userId별 부서
    private val departments = mutableMapOf<Long, String>()

    init {
        viewModelScope.launch {
            PostLikeUpdates.updates.collect { update ->
                if (userId == update.userId) {
                    updateLike(update.userId, update.postId, update.liked, update.count)
                }
            }
        }
    }

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
        // 새 조회 이전에 완료된 변경만 서버 응답으로 갱신
        likeStates.keys.removeAll { it !in likeJobs }
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
                likeJobs.filterKeys { it.first == currentUserId }.values.toList().joinAll()
                getFeedPage(
                    userId = currentUserId,
                    scope = state.selectedScope,
                    cursor = if (isRefresh) null else state.nextCursor,
                )
            }.onSuccess { page ->
                if (requestGeneration != generation) return@onSuccess
                val posts = page.items.map { dto ->
                    val post = dto.toUiModel()
                    likeStates[currentUserId to post.postId]?.let { (liked, count) ->
                        post.copy(isLiked = liked, likeCount = count)
                    } ?: post
                }
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

    private suspend fun getFeedPage(
        userId: Long,
        scope: FeedScope,
        cursor: String?,
    ): CursorResponseDto<FeedPostResponseDto> {
        val popular = scope == FeedScope.POPULAR
        var nextCursor = cursor
        var page: CursorResponseDto<FeedPostResponseDto>
        val items = mutableListOf<FeedPostResponseDto>()
        var requestCount = 0
        do {
            val response = apiService.getFeed(
                userId = userId,
                sort = if (popular) "POPULAR" else "LATEST",
                period = if (popular) "WEEK" else null,
                cursor = nextCursor,
                limit = 20,
            )
            check(response.status in 200..299) { response.code }
            page = requireNotNull(response.data)
            items += if (scope == FeedScope.DEPARTMENT) page.items.filterSameDepartment(userId) else page.items
            val isCursorMoved = page.nextCursor != null && page.nextCursor != nextCursor
            nextCursor = page.nextCursor
            requestCount++
            // 우리 부서 글이 한 페이지에 없을 때 빈 목록으로 끝나지 않게 몇 페이지 더 확인
        } while (items.isEmpty() && page.hasMore && isCursorMoved && requestCount < MAX_DEPARTMENT_PAGE_REQUESTS)
        return page.copy(items = items)
    }

    private suspend fun List<FeedPostResponseDto>.filterSameDepartment(userId: Long): List<FeedPostResponseDto> {
        val myDepartment = departmentOf(userId)
        val authorDepartments = coroutineScope {
            map { it.author.userId }.distinct()
                .map { authorId -> async { authorId to departmentOf(authorId) } }
                .awaitAll()
                .toMap()
        }
        return filter { authorDepartments[it.author.userId] == myDepartment }
    }

    private suspend fun departmentOf(userId: Long): String = departments[userId]
        ?: requireNotNull(userApiService.getUser(userId).data).department.also { departments[userId] = it }

    fun toggleLike(postId: Long) {
        val currentUserId = userId?.takeIf { it > 0 } ?: return
        val key = currentUserId to postId
        if (key in likeJobs) return
        val previous = _uiState.value.posts.find { it.postId == postId } ?: return
        val liked = !previous.isLiked
        val count = (previous.likeCount.toLong() + if (liked) 1 else -1)
            .coerceIn(0, Int.MAX_VALUE.toLong()).toInt()
        updateLike(currentUserId, postId, liked, count)
        val job = viewModelScope.launch(start = CoroutineStart.LAZY) {
            try {
                suspendRunCatching {
                    val response = if (liked) {
                        apiService.postPostLike(currentUserId, postId)
                    } else {
                        apiService.deletePostLike(currentUserId, postId)
                    }
                    check(response.status in 200..299) { response.code }
                    requireNotNull(response.data).also {
                        check(it.postId == postId && it.likeCount >= 0)
                    }
                }.onSuccess { result ->
                    updateLike(
                        currentUserId, postId, result.liked,
                        result.likeCount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                    )
                }.onFailure { throwable ->
                    Log.e("FeedViewModel", "좋아요 변경 실패", throwable)
                    updateLike(currentUserId, postId, previous.isLiked, previous.likeCount)
                    if (userId == currentUserId) _uiEffect.send(FeedUiEffect.LikeFailed)
                }
            } finally {
                likeJobs.remove(key)
            }
        }
        likeJobs[key] = job
        job.start()
    }

    private fun updateLike(userId: Long, postId: Long, liked: Boolean, count: Int) {
        likeStates[userId to postId] = liked to count
        if (this.userId != userId) return
        _uiState.update { state ->
            state.copy(
                posts = state.posts.map { post ->
                    if (post.postId == postId) post.copy(isLiked = liked, likeCount = count) else post
                },
            )
        }
    }

    private companion object {
        const val MAX_DEPARTMENT_PAGE_REQUESTS = 5
    }
}
