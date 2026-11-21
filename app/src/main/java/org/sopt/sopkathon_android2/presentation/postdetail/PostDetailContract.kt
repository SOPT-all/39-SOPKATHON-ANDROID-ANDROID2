package org.sopt.sopkathon_android2.presentation.postdetail

import org.sopt.sopkathon_android2.core.designsystem.component.post.OutfitItemType
import org.sopt.sopkathon_android2.presentation.postdetail.model.CommentUiModel
import org.sopt.sopkathon_android2.presentation.postdetail.model.PostDetailUiModel

sealed interface PostDetailSheet {
    data object Comments : PostDetailSheet
    data class Item(val clothesId: Long) : PostDetailSheet
}

data class PostDetailUiState(
    val detail: PostDetailUiModel? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isLiking: Boolean = false,
    val selectedTagType: OutfitItemType? = null,
    val sheet: PostDetailSheet? = null,
    val comments: List<CommentUiModel> = emptyList(),
    val commentsLoaded: Boolean = false,
    val isLoadingComments: Boolean = false,
    val commentsError: String? = null,
    val nextCursor: String? = null,
    val hasMoreComments: Boolean = false,
    val draft: String = "",
    val isPosting: Boolean = false,
)

sealed interface PostDetailUiEffect {
    data class ShowError(val message: String) : PostDetailUiEffect
}
