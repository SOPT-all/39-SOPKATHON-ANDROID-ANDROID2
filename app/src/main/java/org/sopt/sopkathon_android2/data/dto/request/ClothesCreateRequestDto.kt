package org.sopt.sopkathon_android2.data.dto.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClothesCreateRequestDto(
    @SerialName("name")
    val name: String,
    @SerialName("imageKey")
    val imageKey: String,
    @SerialName("purchaseLink")
    val purchaseLink: String,
    @SerialName("category")
    val category: String, // TOP, BOTTOM
)
