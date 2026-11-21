package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostDetailResponseDto(
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
    val description: String? = null,
    @SerialName("outfitItems")
    val outfitItems: List<OutfitItemResponseDto> = emptyList(),
    @SerialName("purchaseLinks")
    val purchaseLinks: List<PostPurchaseLinkResponseDto> = emptyList(),
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
data class OutfitItemResponseDto(
    @SerialName("clothesId")
    val clothesId: Long,
    @SerialName("name")
    val name: String,
    @SerialName("category")
    val category: String,
    @SerialName("imageUrl")
    val imageUrl: String?,
    @SerialName("purchaseUrl")
    val purchaseUrl: String?,
)

@Serializable
data class PostPurchaseLinkResponseDto(
    @SerialName("url")
    val url: String,
)
