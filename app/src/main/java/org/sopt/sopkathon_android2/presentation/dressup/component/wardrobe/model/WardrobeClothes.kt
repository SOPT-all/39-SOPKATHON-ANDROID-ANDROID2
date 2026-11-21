package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model

import org.sopt.sopkathon_android2.data.dto.response.ClothesResponseDto

data class WardrobeClothes(
    val clothesId: Long,
    val name: String,
    val imageUrl: String,
    val purchaseLink: String,
    val category: WardrobeCategory,
) {
    // 같은 옷을 두 번 입히지 않도록 스티커 key로도 사용
    val key: String
        get() = "clothes-$clothesId"

    val displayName: String
        get() = name.ifBlank { category.label }
}

fun ClothesResponseDto.toWardrobeClothes(category: WardrobeCategory) = WardrobeClothes(
    clothesId = clothesId,
    name = name.orEmpty(),
    imageUrl = imageUrl,
    purchaseLink = purchaseLink.orEmpty(),
    category = category,
)
