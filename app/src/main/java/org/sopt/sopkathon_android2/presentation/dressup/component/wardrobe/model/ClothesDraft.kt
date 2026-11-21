package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model

import android.net.Uri

data class ClothesDraft(
    val cutoutUri: Uri,
    val name: String,
    val purchaseLink: String,
    val category: WardrobeCategory,
)
