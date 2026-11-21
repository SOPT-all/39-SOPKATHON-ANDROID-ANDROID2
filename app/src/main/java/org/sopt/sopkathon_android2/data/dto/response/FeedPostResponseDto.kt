package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FeedPostResponseDto(
    @SerialName("postId")
    val postId: Long,
    @SerialName("author")
    val author: FeedUserResponseDto,
    @SerialName("targetUser")
    val targetUser: FeedUserResponseDto,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("likeCount")
    val likeCount: Int,
    @SerialName("liked")
    val liked: Boolean,
    @SerialName("commentCount")
    val commentCount: Int,
    @SerialName("createdAt")
    val createdAt: String,
)

@Serializable
data class FeedUserResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("position")
    val position: String,
)
