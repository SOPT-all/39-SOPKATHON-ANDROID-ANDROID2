package org.sopt.sopkathon_android2.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CommentCreateRequestDto(
    @SerialName("content")
    val content: String,
)
