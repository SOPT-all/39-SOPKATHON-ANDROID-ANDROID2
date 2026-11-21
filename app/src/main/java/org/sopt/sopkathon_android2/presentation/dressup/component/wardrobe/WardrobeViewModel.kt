package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import android.app.Application
import android.util.Log
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
import org.sopt.sopkathon_android2.core.network.PresignedUrlUploader
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.dto.request.ClothesCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.request.PresignedUrlRequestDto
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.ClothesDraft
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeCategory
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiEffect
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiState

private const val TAG = "WardrobeViewModel"

// 누끼 PNG를 올릴 때 Context가 필요해서 AndroidViewModel
class WardrobeViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<WardrobeUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<WardrobeUiEffect> = _uiEffect.receiveAsFlow()

    init {
        getClothes()
    }

    private fun getClothes() {
        viewModelScope.launch {
            suspendRunCatching { requireNotNull(ServiceModule.apiService.getClothes().data) }
                .onSuccess { response -> _uiState.update { it.withServerClothes(response) } }
                .onFailure { Log.e(TAG, "옷장 조회 실패", it) }
        }
    }

    fun onCategoryChange(category: WardrobeCategory) {
        _uiState.update { it.copy(category = category) }
    }

    fun onExpandedChange(isExpanded: Boolean) {
        _uiState.update { it.copy(isExpanded = isExpanded) }
    }

    fun onAddClick() {
        _uiState.update { it.copy(isAddSheetVisible = true) }
    }

    fun onAddSheetDismiss() {
        _uiState.update { it.copy(isAddSheetVisible = false) }
    }

    // 화면이 다시 만들어져도 업로드가 끊기지 않게 viewModelScope에서 올림
    fun onClothesAdd(draft: ClothesDraft) {
        if (_uiState.value.isUploading) return
        _uiState.update { it.copy(isUploading = true) }
        viewModelScope.launch {
            suspendRunCatching { postClothes(draft) }
                .onSuccess { clothesId ->
                    // 다시 불러오기 전까지 누끼 파일로 보여 주므로 지우지 않음
                    val clothes = WardrobeClothes(
                        clothesId = clothesId,
                        name = draft.name,
                        imageUrl = draft.cutoutUri.toString(),
                        purchaseLink = draft.purchaseLink,
                        category = draft.category,
                    )
                    _uiState.update { it.withAddedClothes(clothes).copy(isAddSheetVisible = false, isUploading = false) }
                    _uiEffect.send(WardrobeUiEffect.ClothesAdded(clothes))
                }.onFailure {
                    Log.e(TAG, "옷 추가 실패", it)
                    _uiState.update { state -> state.copy(isUploading = false) }
                    _uiEffect.send(WardrobeUiEffect.ShowAddErrorToast)
                }
        }
    }

    private suspend fun postClothes(draft: ClothesDraft): Long {
        val presignedUrl = requireNotNull(
            ServiceModule.apiService.postPresignedUrl(
                PresignedUrlRequestDto(contentType = PresignedUrlUploader.PNG_CONTENT_TYPE),
            ).data,
        )
        PresignedUrlUploader.uploadPng(getApplication(), presignedUrl.presignedUrl, draft.cutoutUri)
        return requireNotNull(
            ServiceModule.apiService.postClothes(
                ClothesCreateRequestDto(
                    name = draft.name,
                    imageKey = presignedUrl.imageKey,
                    purchaseLink = draft.purchaseLink,
                    category = draft.category.name,
                ),
            ).data,
        ).clothesId
    }
}
