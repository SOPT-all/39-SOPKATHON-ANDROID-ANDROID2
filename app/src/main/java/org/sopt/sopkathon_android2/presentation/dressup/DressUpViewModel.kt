package org.sopt.sopkathon_android2.presentation.dressup

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.sopt.sopkathon_android2.presentation.dressup.model.ItemInfoInput

class DressUpViewModel : ViewModel() {
    // 옷장, 얼굴 누끼 연결 전까지 예시 옷으로 확인
    private val _uiState = MutableStateFlow(DressUpUiState.dummy)
    val uiState: StateFlow<DressUpUiState> = _uiState.asStateFlow()

    fun onStickerActionClick(key: Any) {
        _uiState.update { it.copy(itemInfoSheetKeys = listOf(key.toString())) }
    }

    fun onPurchaseLinkClick() {
        _uiState.update { state -> state.copy(itemInfoSheetKeys = state.wornItems.map { it.key }) }
    }

    fun onItemInfoSheetDismiss() {
        _uiState.update { it.copy(itemInfoSheetKeys = null) }
    }

    fun onItemInfoSave(inputs: List<ItemInfoInput>) {
        val inputByKey = inputs.associateBy { it.key }
        _uiState.update { state ->
            state.copy(
                wornItems = state.wornItems.map { item ->
                    inputByKey[item.key]?.let { item.copy(name = it.name, link = it.link) } ?: item
                },
                itemInfoSheetKeys = null,
            )
        }
    }

    fun onItemDelete(key: Any) {
        _uiState.update { state -> state.copy(wornItems = state.wornItems.filterNot { it.key == key }) }
    }
}
