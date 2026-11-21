package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeCategory
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiState

private const val TAG = "WardrobeViewModel"

class WardrobeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(WardrobeUiState())
    val uiState: StateFlow<WardrobeUiState> = _uiState.asStateFlow()

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

    fun onClothesAdd(clothes: WardrobeClothes) {
        _uiState.update { it.withAddedClothes(clothes).copy(isAddSheetVisible = false) }
    }
}
