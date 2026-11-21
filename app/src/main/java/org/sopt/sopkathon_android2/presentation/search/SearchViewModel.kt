package org.sopt.sopkathon_android2.presentation.search

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SearchViewModel : ViewModel() {
    // 서버 연결 전 임시 데이터
    private val _uiState = MutableStateFlow(
        SearchUiState(
            popularPosts = listOf(
                PopularPost(postId = 1L, title = "금요일 회식룩 아이엠그라운드 자기소개하기", imageUrl = "", likeCount = 18),
                PopularPost(postId = 2L, title = "편안한 출근룩", imageUrl = "", likeCount = 12),
                PopularPost(postId = 3L, title = "봄맞이 산뜻룩", imageUrl = "", likeCount = 9),
                PopularPost(postId = 4L, title = "워크숍 데일리룩", imageUrl = "", likeCount = 7),
                PopularPost(postId = 5L, title = "발표하는 날", imageUrl = "", likeCount = 5),
                PopularPost(postId = 6L, title = "캐주얼 금요일", imageUrl = "", likeCount = 3),
            ),
        ),
    )
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
}
