package org.sopt.sopkathon_android2.presentation.feed.model

import org.sopt.sopkathon_android2.core.designsystem.component.post.PostAuthorUiModel

data class FeedPostUiModel(
    val postId: Long,
    val author: PostAuthorUiModel,
    val targetLabel: String,
    val scopeLabel: String,
    val createdAt: String,
    val imageUrl: String,
    val hashtag: String,
    val title: String,
    val content: String,
    val likeCount: Int,
    val commentCount: Int,
    val isLiked: Boolean = false,
)
