package org.sopt.sopkathon_android2.presentation.colleagueselect

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.presentation.colleagueselect.model.toColleagueUiModel

class ColleagueSelectViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ColleagueSelectUiState())
    val uiState: StateFlow<ColleagueSelectUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ColleagueSelectUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<ColleagueSelectUiEffect> = _uiEffect.receiveAsFlow()

    private var loadJob: Job? = null

    init {
        getColleagues(isRefresh = true)
    }

    // 필터 변경 시 선택 초기화 후 첫 페이지부터 다시 조회
    fun selectPosition(position: String) {
        if (position == _uiState.value.selectedPosition) return

        _uiState.update { it.copy(selectedPosition = position, selectedColleagueId = null) }
        getColleagues(isRefresh = true)
    }

    // 같은 행을 다시 누르면 선택 해제
    fun toggleColleague(colleagueId: Long) {
        _uiState.update {
            it.copy(selectedColleagueId = if (it.selectedColleagueId == colleagueId) null else colleagueId)
        }
    }

    fun getColleagues(isRefresh: Boolean = false) {
        val state = _uiState.value
        if (!isRefresh && (state.isLoading || !state.hasMore)) return

        // 필터를 빠르게 바꿀 때 이전 응답이 새 목록을 덮는 것 방지
        loadJob?.cancel()
        loadJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            suspendRunCatching {
                requireNotNull(
                    // 검색어 없이 호출하면 전체 동료 목록
                    ServiceModule.apiService.searchUsers(
                        keyword = null,
                        position = state.selectedPosition.takeIf { it != ALL_POSITION },
                        cursor = if (isRefresh) null else state.nextCursor,
                    ).data,
                )
            }.onSuccess { page ->
                val newItems = page.items.map { it.toColleagueUiModel() }
                _uiState.update {
                    it.copy(
                        colleagues = (if (isRefresh) newItems else it.colleagues + newItems).distinctBy { item -> item.id },
                        nextCursor = page.nextCursor,
                        hasMore = page.hasMore,
                        isLoading = false,
                    )
                }
            }.onFailure { throwable ->
                Log.e("ColleagueSelectViewModel", "동료 목록 조회 실패", throwable)
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(ColleagueSelectUiEffect.ShowErrorToast)
            }
        }
    }
}
