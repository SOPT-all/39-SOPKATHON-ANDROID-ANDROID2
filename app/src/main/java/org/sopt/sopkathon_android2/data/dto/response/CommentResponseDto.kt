package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentResponseDto(
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("author")
    val author: FeedUserResponseDto,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
)

@Serializable
data class CommentCreateResponseDto(
    @SerialName("commentId")
    val commentId: Long,
    @SerialName("postId")
    val postId: Long,
    @SerialName("content")
    val content: String,
    @SerialName("createdAt")
    val createdAt: String,
    @SerialName("commentCount")
    val commentCount: Int,
)
