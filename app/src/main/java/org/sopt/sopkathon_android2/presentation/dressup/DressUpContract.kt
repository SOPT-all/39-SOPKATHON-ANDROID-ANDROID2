package org.sopt.sopkathon_android2.presentation.dressup

import org.sopt.sopkathon_android2.presentation.dressup.model.ClothesCategory
import org.sopt.sopkathon_android2.presentation.dressup.model.WornItemUiModel

/**
 * @param itemInfoSheetKeys 아이템 정보 시트에 보일 옷 key. null이면 시트 닫힘
 */
data class DressUpUiState(
    val colleagueName: String = "",
    val wornItems: List<WornItemUiModel> = emptyList(),
    val itemInfoSheetKeys: List<String>? = null,
) {
    val incompleteCount: Int get() = wornItems.count { !it.isInfoComplete }

    // 서버 옷 추가 API가 링크를 필수로 받아서 링크 없는 옷이 있으면 코디 완성을 막음
    val isCompleteEnabled: Boolean
        get() = wornItems.isNotEmpty() && wornItems.all { it.link.isNotBlank() }

    val itemInfoSheetItems: List<WornItemUiModel>?
        get() = itemInfoSheetKeys?.let { keys -> wornItems.filter { it.key in keys } }

    companion object {
        val dummy = DressUpUiState(
            colleagueName = "박정훈 과장",
            wornItems = listOf(
                WornItemUiModel(key = "bottom", category = ClothesCategory.BOTTOM),
                WornItemUiModel(
                    key = "top",
                    category = ClothesCategory.TOP,
                    name = "네이비 크루넥 니트",
                    link = "https://www.uniqlo.com/kr",
                ),
            ),
        )
    }
}
