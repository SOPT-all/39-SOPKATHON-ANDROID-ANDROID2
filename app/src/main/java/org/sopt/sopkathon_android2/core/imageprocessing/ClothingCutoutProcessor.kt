package org.sopt.sopkathon_android2.core.imageprocessing

import android.content.Context
import android.net.Uri
import org.sopt.sopkathon_android2.core.imageprocessing.internal.CutoutEngine

class ClothingCutoutProcessor(context: Context) {
    private val engine = CutoutEngine(context)
    val modelState = engine.modelState

    suspend fun prepareModel() = engine.prepareModel()

    suspend fun process(imageUri: Uri): Uri = engine.process(imageUri) { _, foreground -> foreground }
}
