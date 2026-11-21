package org.sopt.sopkathon_android2.presentation.dressup

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.presentation.dressup.component.character.CharacterHeadMaker
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import org.sopt.sopkathon_android2.presentation.dressup.model.ClothesCategory
import org.sopt.sopkathon_android2.presentation.dressup.model.ItemInfoInput
import org.sopt.sopkathon_android2.presentation.dressup.model.WornItemUiModel
import org.sopt.sopkathon_android2.presentation.main.navigation.DressUp
import org.sopt.sopkathon_android2.presentation.upload.OutfitUploader
import org.sopt.sopkathon_android2.presentation.upload.model.OutfitUploadItem
import org.sopt.sopkathon_android2.presentation.upload.model.toPostUpload

private const val TAG = "DressUpViewModel"

class DressUpViewModel(application: Application) : AndroidViewModel(application) {
    private val headMaker = CharacterHeadMaker(application)
    private lateinit var colleague: DressUp

    private val _uiState = MutableStateFlow(DressUpUiState())
    val uiState: StateFlow<DressUpUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<DressUpUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<DressUpUiEffect> = _uiEffect.receiveAsFlow()

    fun setColleague(dressUp: DressUp) {
        colleague = dressUp
        _uiState.update { it.copy(colleagueName = "${dressUp.name} ${dressUp.position}".trim()) }
        dressUp.profileImageUrl?.let(::makeCharacterHead)
    }

    private fun makeCharacterHead(profileImageUrl: String) {
        viewModelScope.launch {
            suspendRunCatching { headMaker.make(profileImageUrl) }
                .onSuccess { uri -> _uiState.update { it.copy(headUri = uri) } }
                .onFailure { Log.e(TAG, "머리 이미지 생성 실패", it) }
        }
    }

    fun onWear(clothes: WardrobeClothes) {
        val item = clothes.toWornItem()
        _uiState.update { state ->
            if (state.wornItems.any { it.key == item.key }) return@update state
            // 캐릭터에 상의, 하의 자리가 하나씩이라 같은 카테고리 옷은 바꿔 입힘
            state.copy(wornItems = state.wornItems.filterNot { it.category == item.category } + item)
        }
    }

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

    /**
     * @param capture `StickerCanvasState.capture`. 캡처도 업로드 중 표시 안에서 해서 연타 시 두 번 올리지 않음
     */
    fun onCompleteClick(capture: suspend () -> Result<Uri>) {
        if (_uiState.value.isUploading) return
        _uiState.update { it.copy(isUploading = true) }
        val items = _uiState.value.wornItems.map { it.toOutfitUploadItem() }

        viewModelScope.launch {
            suspendRunCatching {
                val captureUri = capture().getOrThrow()
                OutfitUploader.upload(getApplication(), items, captureUri).toPostUpload(
                    colleagueId = colleague.colleagueId,
                    colleagueName = colleague.name,
                    colleaguePosition = colleague.position,
                    captureUri = captureUri,
                    items = items,
                )
            }
                .onSuccess { _uiEffect.send(DressUpUiEffect.NavigateToPostUpload(it)) }
                .onFailure { Log.e(TAG, "코디 업로드 실패", it) }
            _uiState.update { it.copy(isUploading = false) }
        }
    }
}

private fun WardrobeClothes.toWornItem() = WornItemUiModel(
    key = key,
    category = ClothesCategory.valueOf(category.name),
    imageUri = imageUrl,
    name = name,
    link = purchaseLink,
    clothesId = clothesId,
)

private fun WornItemUiModel.toOutfitUploadItem() = OutfitUploadItem(
    name = displayName,
    category = category.name,
    categoryLabel = category.label,
    link = link,
    isInfoComplete = isInfoComplete,
    clothesId = clothesId,
    cutoutUri = if (clothesId == null) imageUri?.toUri() else null,
)
