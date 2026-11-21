package org.sopt.sopkathon_android2.presentation.feed

import org.sopt.sopkathon_android2.core.designsystem.component.post.PostAuthorUiModel
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope
import org.sopt.sopkathon_android2.presentation.feed.model.FeedPostUiModel

data class FeedUiState(
    val selectedScope: FeedScope = FeedScope.DEPARTMENT,
    val posts: List<FeedPostUiModel> = emptyList(),
) {
    companion object {
        val dummy = FeedUiState(
            posts = listOf(
                FeedPostUiModel(
                    postId = 501L,
                    author = PostAuthorUiModel(202L, "김민지", "대리", "마케팅팀", null),
                    targetLabel = "박정훈 과장님 코디",
                    scopeLabel = "우리 부서",
                    createdAt = "2026-11-22T02:00:00+09:00",
                    imageUrl = "",
                    hashtag = "금요일회식룩",
                    title = "오늘의 회식룩",
                    content = "과장님 평소 정장만 입으셔서 이번엔 네이비 니트에 아이보리 데님으로 캐주얼하게! 금요일 회식룩 추천해요.",
                    likeCount = 18,
                    commentCount = 3,
                ),
                FeedPostUiModel(
                    postId = 502L,
                    author = PostAuthorUiModel(203L, "이준호", "사원", "마케팅팀", null),
                    targetLabel = "박정훈 과장님 코디",
                    scopeLabel = "우리 부서",
                    createdAt = "2026-11-22T01:30:00+09:00",
                    imageUrl = "",
                    hashtag = "편안한출근룩",
                    title = "편안한 출근룩",
                    content = "활동하기 편하면서 깔끔한 출근 코디를 골라봤어요.",
                    likeCount = 7,
                    commentCount = 1,
                ),
            ),
        )
    }
}
