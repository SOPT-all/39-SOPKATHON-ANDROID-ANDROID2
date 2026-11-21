package org.sopt.sopkathon_android2.presentation.search

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.BuildConfig
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import retrofit2.HttpException

class SearchViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()
    private var started = false

    fun loadPopularPosts() {
        if (started) return
        started = true
        requestPopularPosts()
    }

    private fun requestPopularPosts() {
        val userId = BuildConfig.FEED_USER_ID
        if (userId <= 0) {
            _uiState.update {
                it.copy(isLoading = false, errorMessage = "인기 코디를 보려면 서버 사용자 ID가 필요해요.")
            }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }
        viewModelScope.launch {
            suspendRunCatching {
                val response = ServiceModule.apiService.getFeed(
                    userId = userId,
                    sort = "POPULAR",
                    period = "WEEK",
                    limit = PREVIEW_SIZE,
                    cursor = null,
                )
                check(response.status in 200..299)
                requireNotNull(response.data)
            }.onSuccess { page ->
                val posts = page.items.map { it.toUiModel() }
                _uiState.update { state ->
                    state.copy(
                        popularPosts = posts.distinctBy { it.postId }.take(PREVIEW_SIZE),
                        isLoading = false,
                    )
                }
            }.onFailure { error ->
                Log.w("SearchViewModel", "인기 코디 조회 실패", error)
                val userNotFound = error is HttpException && error.code() == 404
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = if (userNotFound) {
                            "등록된 서버 사용자를 찾을 수 없어요."
                        } else {
                            "인기 코디를 불러오지 못했어요."
                        },
                    )
                }
            }
        }
    }

    private companion object {
        const val PREVIEW_SIZE = 6
    }
}
