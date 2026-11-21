package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max
import kotlin.math.roundToInt

// 누끼 경계의 흐린 픽셀까지 옷으로 치지 않도록 둔 기준
private const val ALPHA_THRESHOLD = 16
private const val PADDING_RATIO = 0.02f

// 누끼 결과는 원본 사진 크기 그대로라 옷 둘레의 투명 여백을 잘라 냄
internal object ClothesCutoutTrimmer {
    suspend fun trim(context: Context, cutoutUri: Uri): Uri = withContext(Dispatchers.IO) {
        val source = context.contentResolver.openInputStream(cutoutUri).use { input ->
            requireNotNull(BitmapFactory.decodeStream(input)) { "Cutout decode failed" }
        }
        try {
            val bounds = source.paddedOpaqueBounds()
            val trimmed = Bitmap.createBitmap(source, bounds.left, bounds.top, bounds.width(), bounds.height())
            try {
                val file = File.createTempFile("clothes-cutout-", ".png", context.cacheDir)
                file.outputStream().use { output ->
                    check(trimmed.compress(Bitmap.CompressFormat.PNG, 100, output)) { "Cutout save failed" }
                }
                Uri.fromFile(file)
            } finally {
                if (trimmed !== source) trimmed.recycle()
            }
        } finally {
            source.recycle()
        }
    }

    private fun Bitmap.paddedOpaqueBounds(): Rect {
        val pixels = IntArray(width * height).also { getPixels(it, 0, width, 0, 0, width, height) }
        var left = width
        var top = height
        var right = -1
        var bottom = -1
        for (y in 0 until height) {
            val row = y * width
            for (x in 0 until width) {
                if (pixels[row + x] ushr 24 > ALPHA_THRESHOLD) {
                    if (x < left) left = x
                    if (x > right) right = x
                    if (top == height) top = y
                    bottom = y
                }
            }
        }
        if (right < 0) return Rect(0, 0, width, height)

        val padding = (max(right - left, bottom - top) * PADDING_RATIO).roundToInt()
        return Rect(
            (left - padding).coerceAtLeast(0),
            (top - padding).coerceAtLeast(0),
            (right + 1 + padding).coerceAtMost(width),
            (bottom + 1 + padding).coerceAtMost(height),
        )
    }
}
