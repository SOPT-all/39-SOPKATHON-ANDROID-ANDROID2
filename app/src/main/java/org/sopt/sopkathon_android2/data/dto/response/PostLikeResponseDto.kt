package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostLikeResponseDto(
    @SerialName("postId")
    val postId: Long,
    @SerialName("liked")
    val liked: Boolean,
    @SerialName("likeCount")
    val likeCount: Long,
)
