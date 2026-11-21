package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes

@Composable
internal fun ClothesImage(clothes: WardrobeClothes) {
    AsyncImage(
        model = clothes.imageUrl,
        contentDescription = clothes.displayName,
        modifier = Modifier.fillMaxSize(),
    )
}
