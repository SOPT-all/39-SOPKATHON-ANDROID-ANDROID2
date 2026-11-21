package org.sopt.sopkathon_android2.presentation.dressup.model

enum class ClothesCategory(val label: String) {
    TOP("상의"),
    BOTTOM("하의"),
}

/**
 * 캐릭터에 입힌 옷 하나. 스티커 하나와 같음
 *
 * @param key 스티커 key
 * @param imageUri 누끼 이미지 Uri 문자열이나 서버 이미지 URL. 없으면 자리 표시만 그림
 * @param clothesId 옷장에 이미 있던 옷. 새로 찍은 옷은 코디 완성 때 옷 추가 API로 받음
 */
data class WornItemUiModel(
    val key: String,
    val category: ClothesCategory,
    val imageUri: String? = null,
    val name: String = "",
    val link: String = "",
    val clothesId: Long? = null,
) {
    // 이름을 안 넣으면 카테고리로 대신 보여 주고 서버에도 보냄
    val displayName: String get() = name.ifBlank { category.label }

    val isInfoComplete: Boolean get() = name.isNotBlank() && link.isNotBlank()

    companion object {
        // 서버 API에 브랜드가 없어 모든 옷에 같은 값
        const val BRAND = "UNIQLO"
    }
}

/**
 * 아이템 정보 시트에서 저장한 값
 */
data class ItemInfoInput(
    val key: String,
    val name: String,
    val link: String,
)
