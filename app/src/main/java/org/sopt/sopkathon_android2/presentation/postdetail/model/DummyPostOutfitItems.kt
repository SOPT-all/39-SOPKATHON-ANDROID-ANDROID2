package org.sopt.sopkathon_android2.presentation.postdetail.model

import org.sopt.sopkathon_android2.core.designsystem.component.post.OutfitItemType

// 서버 상품 데이터 대신 상세 화면에 표시할 해커톤용 고정 상품
internal object DummyPostOutfitItems {
    val items = listOf(
        OutfitItemUiModel(
            clothesId = 10,
            name = "네이비 크루넥 니트",
            category = "상의 · 니트",
            imageUrl = "file:///android_asset/postdetail/navy_knit.png",
            purchaseUrl = "https://www.uniqlo.com/kr/ko/",
            type = OutfitItemType.TOP,
            brand = "UNIQLO",
            priceLabel = "49,900원",
            attributes = listOf("컬러" to "네이비", "소재" to "코튼 100%"),
        ),
        OutfitItemUiModel(
            clothesId = 11,
            name = "아이보리 데님",
            category = "하의 · 데님",
            imageUrl = "file:///android_asset/postdetail/ivory_denim.png",
            purchaseUrl = "https://www2.hm.com/ko_kr/",
            type = OutfitItemType.BOTTOM,
            brand = "H&M",
            priceLabel = "39,900원",
            attributes = listOf("컬러" to "아이보리", "핏" to "스트레이트"),
        ),
    )
}
