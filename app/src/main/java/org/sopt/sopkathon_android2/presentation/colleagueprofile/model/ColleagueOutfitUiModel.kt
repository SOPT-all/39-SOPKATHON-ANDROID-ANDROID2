package org.sopt.sopkathon_android2.presentation.colleagueprofile.model

import org.sopt.sopkathon_android2.data.dto.response.FeedPostResponseDto
import org.sopt.sopkathon_android2.data.dto.response.ReceivedOutfitResponseDto

data class ColleagueOutfitUiModel(
    val postId: Long,
    val title: String,
    val imageUrl: String,
    val likeCount: Int,
)

fun ReceivedOutfitResponseDto.toUiModel() = ColleagueOutfitUiModel(
    postId = postId,
    title = title,
    imageUrl = imageUrl.orEmpty(),
    likeCount = likeCount,
)

fun FeedPostResponseDto.toOutfitUiModel() = ColleagueOutfitUiModel(
    postId = postId,
    title = title,
    imageUrl = imageUrl,
    likeCount = likeCount,
)
