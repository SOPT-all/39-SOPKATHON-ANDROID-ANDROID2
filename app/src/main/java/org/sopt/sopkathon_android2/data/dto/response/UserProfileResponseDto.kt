package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserProfileResponseDto(
    @SerialName("userId")
    val userId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("position")
    val position: String,
    @SerialName("department")
    val department: String,
    @SerialName("profileImageUrl")
    val profileImageUrl: String? = null,
    @SerialName("postCount")
    val postCount: Int = 0,
    @SerialName("receivedPostCount")
    val receivedPostCount: Int = 0,
    @SerialName("receivedLikeCount")
    val receivedLikeCount: Int = 0,
    @SerialName("receivedOutfits")
    val receivedOutfits: List<ReceivedOutfitResponseDto> = emptyList(),
)

@Serializable
data class ReceivedOutfitResponseDto(
    @SerialName("postId")
    val postId: Long,
    @SerialName("title")
    val title: String,
    @SerialName("imageUrl")
    val imageUrl: String? = null,
    @SerialName("likeCount")
    val likeCount: Int,
)
