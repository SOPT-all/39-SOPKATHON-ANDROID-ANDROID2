package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model

import org.sopt.sopkathon_android2.data.dto.response.ClothesListResponseDto

data class WardrobeUiState(
    val category: WardrobeCategory = WardrobeCategory.TOP,
    val isExpanded: Boolean = false,
    val tops: List<WardrobeClothes> = emptyList(),
    val bottoms: List<WardrobeClothes> = emptyList(),
) {
    val clothes: List<WardrobeClothes>
        get() = when (category) {
            WardrobeCategory.TOP -> tops
            WardrobeCategory.BOTTOM -> bottoms
        }

    fun withServerClothes(response: ClothesListResponseDto): WardrobeUiState = copy(
        tops = response.top.map { it.toWardrobeClothes(WardrobeCategory.TOP) },
        bottoms = response.bottom.map { it.toWardrobeClothes(WardrobeCategory.BOTTOM) },
    )

    // 추가한 옷이 바로 보이도록 그 카테고리로 전환
    fun withAddedClothes(clothes: WardrobeClothes): WardrobeUiState = when (clothes.category) {
        WardrobeCategory.TOP -> copy(category = WardrobeCategory.TOP, tops = listOf(clothes) + tops)
        WardrobeCategory.BOTTOM -> copy(category = WardrobeCategory.BOTTOM, bottoms = listOf(clothes) + bottoms)
    }

    companion object {
        val dummy = WardrobeUiState(
            tops = listOf("네이비 니트", "옥스퍼드 셔츠", "그레이 후디", "베이지 가디건", "화이트 티셔츠", "차콜 반집업", "스트라이프 티")
                .mapIndexed { index, name -> dummyClothes(index + 1L, name, WardrobeCategory.TOP) },
            bottoms = listOf("아이보리 데님", "인디고 진", "차콜 슬랙스", "베이지 치노", "블랙 와이드", "그레이 조거", "올리브 카고")
                .mapIndexed { index, name -> dummyClothes(index + 101L, name, WardrobeCategory.BOTTOM) },
        )

        private fun dummyClothes(
            clothesId: Long,
            name: String,
            category: WardrobeCategory,
        ) = WardrobeClothes(
            clothesId = clothesId,
            name = name,
            imageUrl = "",
            purchaseLink = "",
            category = category,
        )
    }
}
