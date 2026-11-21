package org.sopt.sopkathon_android2.presentation.upload

import org.sopt.sopkathon_android2.presentation.upload.model.PostUploadItemUiModel
import org.sopt.sopkathon_android2.presentation.upload.model.PostVisibility

/**
 * @param imageKey 캐릭터 캡처 이미지 key. 2.2에서 업로드함
 * @param imageUri 썸네일로 쓰는 캡처 PNG
 */
data class PostUploadUiState(
    val colleagueId: Long = 0L,
    val colleagueName: String = "",
    val colleaguePosition: String = "",
    val imageKey: String = "",
    val imageUri: String = "",
    val items: List<PostUploadItemUiModel> = emptyList(),
    val title: String = "",
    val visibility: PostVisibility = PostVisibility.DEPARTMENT,
    val isLoading: Boolean = false,
) {
    val isPostEnabled: Boolean
        get() = title.isNotBlank() && !isLoading

    companion object {
        const val TITLE_MAX_LENGTH = 20
        const val DESCRIPTION_MAX_LENGTH = 60

        val dummy = PostUploadUiState(
            colleagueId = 1L,
            colleagueName = "박정훈",
            colleaguePosition = "과장",
            items = listOf(
                PostUploadItemUiModel(clothesId = 3L, name = "네이비 크루넥 니트", categoryLabel = "상의", isInfoComplete = true),
                PostUploadItemUiModel(clothesId = 4L, name = "아이보리 스트레이트 데님", categoryLabel = "하의", isInfoComplete = false),
            ),
            title = "오늘의 회식룩",
        )
    }
}

sealed interface PostUploadUiEffect {
    data object NavigateToFeed : PostUploadUiEffect
}
