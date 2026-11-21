package org.sopt.sopkathon_android2.data.dto.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ClothesListResponseDto(
    @SerialName("top")
    val top: List<ClothesResponseDto> = emptyList(),
    @SerialName("bottom")
    val bottom: List<ClothesResponseDto> = emptyList(),
)

@Serializable
data class ClothesResponseDto(
    @SerialName("clothesId")
    val clothesId: Long,
    @SerialName("name")
    val name: String? = null,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("purchaseLink")
    val purchaseLink: String? = null, // 예전 데이터나 서버 응답에서 비어 올 수 있음
)
