package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PresignedUrlResponseDto(
    @SerialName("presignedUrl")
    val presignedUrl: String,
    @SerialName("imageKey")
    val imageKey: String,
)
