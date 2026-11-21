package org.sopt.sopkathon_android2.presentation.myprofile.model

import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.ReceivedOutfitResponseDto

data class MyProfileOutfitUiModel(
    val postId: Long,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
)

fun ReceivedOutfitResponseDto.toUiModel() = MyProfileOutfitUiModel(
    postId = postId,
    title = title,
    imageUrl = imageUrl.orEmpty(),
    likeCount = likeCount,
)

fun FeedPostResponseDto.toOutfitUiModel() = MyProfileOutfitUiModel(
    postId = postId,
    title = title,
    imageUrl = imageUrl,
    likeCount = likeCount,
)
