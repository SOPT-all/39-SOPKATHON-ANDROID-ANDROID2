package org.sopt.sopkathon_android2.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostCreateRequestDto(
    @SerialName("title")
    val title: String,
    @SerialName("imageKey")
    val imageKey: String,
    @SerialName("description")
    val description: String? = null,
    @SerialName("visibility")
    val visibility: String,
    @SerialName("targetUserId")
    val targetUserId: Long,
    @SerialName("clothesIds")
    val clothesIds: List<Long>,
)
