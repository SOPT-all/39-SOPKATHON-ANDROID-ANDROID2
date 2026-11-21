package org.sopt.sopkathon_android2.presentation.search.searchinput

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SearchInputViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SearchInputUiState())
    val uiState: StateFlow<SearchInputUiState> = _uiState.asStateFlow()

    val queryState = TextFieldState()

    // 서버 연결 전 임시 데이터
    private val colleagues = listOf(
        SearchColleague(userId = 1L, name = "박정훈", rank = "부장", department = "영업1팀", profileImageUrl = ""),
        SearchColleague(userId = 2L, name = "김민지", rank = "대리", department = "마케팅팀", profileImageUrl = ""),
        SearchColleague(userId = 3L, name = "이서준", rank = "사원", department = "개발팀", profileImageUrl = ""),
        SearchColleague(userId = 4L, name = "최수정", rank = "과장", department = "인사팀", profileImageUrl = ""),
        SearchColleague(userId = 5L, name = "한지우", rank = "주임", department = "디자인팀", profileImageUrl = ""),
    )

    init {
        observeQuery()
    }

    // 입력이 0.3초 멈추면 검색 완료로 보고 결과 갱신, 글자마다 목록이 바뀌는 것 방지
    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            snapshotFlow { queryState.text.toString().trim() }
                .debounce(300)
                .distinctUntilChanged()
                .collect { query ->
                    _uiState.update {
                        it.copy(query = query, results = search(query))
                    }
                }
        }
    }

    private fun search(query: String): List<SearchColleague> {
        if (query.isEmpty()) return emptyList()
        return colleagues.filter {
            it.name.contains(query) || it.rank.contains(query) || it.department.contains(query)
        }
    }
}
