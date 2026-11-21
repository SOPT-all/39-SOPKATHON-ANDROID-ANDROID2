package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model

sealed interface WardrobeUiEffect {
    data class ClothesAdded(val clothes: WardrobeClothes) : WardrobeUiEffect
    data object ShowAddErrorToast : WardrobeUiEffect
}
