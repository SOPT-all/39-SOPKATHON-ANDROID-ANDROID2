package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class FeedItemResponseDto(
    val postId: Long,
    val imageUrl: String? = null,
    val title: String,
    val likeCount: Int,
)
