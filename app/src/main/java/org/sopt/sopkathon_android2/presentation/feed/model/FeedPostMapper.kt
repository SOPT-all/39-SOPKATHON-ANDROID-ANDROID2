package org.sopt.sopkathon_android2.presentation.feed.model

import org.sopt.sopkathon_android2.core.designsystem.component.post.PostAuthorUiModel
import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto

fun FeedPostResponseDto.toUiModel() = FeedPostUiModel(
    postId = postId,
    author = PostAuthorUiModel(
        userId = author.userId,
        name = author.name.trim(),
        position = author.position.trim(),
        department = null,
        profileImageUrl = null,
    ),
    targetLabel = "${targetUser.name.trim()} ${targetUser.position.trim()}님 코디",
    scopeLabel = "",
    createdAt = createdAt,
    imageUrl = imageUrl,
    hashtag = "",
    title = title,
    content = description,
    likeCount = likeCount,
    commentCount = commentCount,
    isLiked = liked,
)
