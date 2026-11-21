package org.sopt.sopkathon_android2.presentation.postdetail

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.core.designsystem.component.post.OutfitItemType
import org.sopt.sopkathon_android2.core.network.BaseResponse
import org.sopt.sopkathon_android2.data.di.PostDetailServiceModule
import org.sopt.sopkathon_android2.data.dto.request.CommentCreateRequestDto
import org.sopt.sopkathon_android2.data.service.PostDetailApiService
import org.sopt.sopkathon_android2.presentation.postdetail.model.toUiModel
import org.sopt.sopkathon_android2.presentation.feed.model.PostLikeUpdate
import org.sopt.sopkathon_android2.presentation.feed.model.PostLikeUpdates

class PostDetailViewModel(
    private val api: PostDetailApiService = PostDetailServiceModule.apiService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PostDetailUiState())
    val uiState = _uiState.asStateFlow()
    private val effects = Channel<PostDetailUiEffect>(Channel.BUFFERED)
    val uiEffect = effects.receiveAsFlow()
    private var userId = 0L
    private var postId = 0L
    private var generation = 0
    private var detailJob: Job? = null
    private var commentsJob: Job? = null
    private var lastCommentsRefresh = true

    fun load(userId: Long, postId: Long, force: Boolean = false) {
        if (!force && this.userId == userId && this.postId == postId &&
            (_uiState.value.detail != null || _uiState.value.isLoading)) return
        if (this.userId == userId && this.postId == postId &&
            (_uiState.value.isPosting || _uiState.value.isLiking)) return
        this.userId = userId
        this.postId = postId
        val token = ++generation
        detailJob?.cancel()
        commentsJob?.cancel()
        if (userId <= 0) {
            _uiState.value = PostDetailUiState(error = "프로필을 만든 뒤 게시글을 확인해주세요")
            return
        }
        _uiState.value = PostDetailUiState(isLoading = true)
        detailJob = viewModelScope.launch {
            suspendRunCatching {
                require(userId > 0 && postId > 0)
                api.getPostDetail(userId, postId).requireData().also { check(it.postId == postId) }.toUiModel()
            }.onSuccess { detail ->
                if (token == generation) _uiState.update { it.copy(detail = detail, isLoading = false) }
            }.onFailure { error ->
                Log.e("PostDetailViewModel", "게시글 조회 실패", error)
                if (token == generation) _uiState.update {
                    it.copy(isLoading = false, error = "게시글을 불러오지 못했어요")
                }
            }
        }
    }

    fun retry() = load(userId, postId, force = true)

    fun selectTag(type: OutfitItemType) {
        _uiState.update { it.copy(selectedTagType = if (it.selectedTagType == type) null else type) }
    }

    fun openItem(clothesId: Long) {
        if (_uiState.value.detail?.items?.none { it.clothesId == clothesId } != false) return
        _uiState.update { it.copy(sheet = PostDetailSheet.Item(clothesId), selectedTagType = null) }
    }

    fun openComments() {
        _uiState.update { it.copy(sheet = PostDetailSheet.Comments, selectedTagType = null) }
        if (!_uiState.value.commentsLoaded) loadComments(refresh = true)
    }

    fun dismissSheet() {
        _uiState.update { it.copy(sheet = null) }
    }

    fun loadComments(refresh: Boolean = false) {
        val state = _uiState.value
        if (state.detail == null || state.isLoadingComments) return
        if (!refresh && (!state.hasMoreComments || state.commentsError != null)) return
        val token = generation
        val cursor = if (refresh) null else state.nextCursor
        lastCommentsRefresh = refresh
        _uiState.update { it.copy(isLoadingComments = true, commentsError = null) }
        commentsJob = viewModelScope.launch {
            suspendRunCatching { api.getPostComments(userId, postId, cursor).requireData() }
                .onSuccess { page ->
                    if (token != generation) return@onSuccess
                    _uiState.update {
                        it.copy(
                            comments = (if (refresh) page.items.map { dto -> dto.toUiModel() }
                                else it.comments + page.items.map { dto -> dto.toUiModel() })
                                .distinctBy { comment -> comment.commentId },
                            commentsLoaded = true,
                            nextCursor = page.nextCursor,
                            hasMoreComments = page.hasMore && page.items.isNotEmpty() &&
                                page.nextCursor != null && page.nextCursor != cursor,
                            isLoadingComments = false,
                        )
                    }
                }.onFailure { error ->
                    Log.e("PostDetailViewModel", "댓글 조회 실패", error)
                    if (token == generation) _uiState.update {
                        it.copy(isLoadingComments = false, commentsError = "댓글을 불러오지 못했어요")
                    }
                }
        }
    }

    fun retryComments() {
        val state = _uiState.value
        if (state.isLoadingComments) return
        _uiState.update { it.copy(commentsError = null) }
        loadComments(refresh = lastCommentsRefresh)
    }

    fun updateDraft(value: String) {
        if (!_uiState.value.isPosting) _uiState.update { it.copy(draft = value) }
    }

    fun postComment() {
        val state = _uiState.value
        val content = state.draft.trim()
        if (state.detail == null || state.isPosting || content.isEmpty()) return
        val token = generation
        _uiState.update { it.copy(isPosting = true) }
        viewModelScope.launch {
            suspendRunCatching {
                api.postPostComment(userId, postId, CommentCreateRequestDto(content)).requireData()
                    .also { check(it.postId == postId) }
            }.onSuccess { result ->
                if (token != generation) return@onSuccess
                _uiState.update {
                    it.copy(
                        draft = "", isPosting = false,
                        detail = it.detail?.let { detail ->
                            detail.copy(post = detail.post.copy(commentCount = result.commentCount))
                        },
                    )
                }
                // 작성 직전 진행 중이던 목록 응답 대신 새 댓글을 포함한 첫 페이지 조회
                commentsJob?.cancel()
                _uiState.update { it.copy(isLoadingComments = false) }
                loadComments(refresh = true)
            }.onFailure { error ->
                Log.e("PostDetailViewModel", "댓글 작성 실패", error)
                if (token == generation) {
                    _uiState.update { it.copy(isPosting = false) }
                    effects.send(PostDetailUiEffect.ShowError("댓글을 게시하지 못했어요. 다시 시도해주세요"))
                }
            }
        }
    }

    fun toggleLike() {
        val state = _uiState.value
        val previous = state.detail?.post ?: return
        if (state.isLiking) return
        val token = generation
        val requestUserId = userId
        _uiState.update {
            it.copy(isLiking = true, detail = it.detail?.copy(post = previous.copy(
                isLiked = !previous.isLiked,
                likeCount = (previous.likeCount.toLong() + if (previous.isLiked) -1 else 1)
                    .coerceIn(0, Int.MAX_VALUE.toLong()).toInt(),
            )))
        }
        viewModelScope.launch {
            suspendRunCatching {
                (if (previous.isLiked) api.deletePostLike(userId, postId)
                    else api.postPostLike(userId, postId)).requireData()
                    .also { check(it.postId == postId && it.likeCount >= 0) }
            }.onSuccess { result ->
                PostLikeUpdates.publish(
                    PostLikeUpdate(
                        requestUserId, result.postId, result.liked,
                        result.likeCount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                    ),
                )
                if (token == generation) _uiState.update {
                    it.copy(isLiking = false, detail = it.detail?.let { detail ->
                        detail.copy(post = detail.post.copy(
                            isLiked = result.liked,
                            likeCount = result.likeCount.coerceAtMost(Int.MAX_VALUE.toLong()).toInt(),
                        ))
                    })
                }
            }.onFailure { error ->
                Log.e("PostDetailViewModel", "좋아요 변경 실패", error)
                if (token == generation) {
                    _uiState.update {
                        it.copy(isLiking = false, detail = it.detail?.let { detail ->
                            detail.copy(post = detail.post.copy(isLiked = previous.isLiked, likeCount = previous.likeCount))
                        })
                    }
                    effects.send(PostDetailUiEffect.ShowError("좋아요를 변경하지 못했어요"))
                }
            }
        }
    }
}

private fun <T> BaseResponse<T>.requireData(): T {
    check(status in 200..299) { code }
    return requireNotNull(data)
}
