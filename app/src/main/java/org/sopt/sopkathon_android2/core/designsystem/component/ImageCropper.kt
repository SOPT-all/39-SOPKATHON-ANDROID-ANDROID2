package org.sopt.sopkathon_android2.core.designsystem.component

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toAndroidRectF
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.allowHardware
import coil3.size.Precision
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private const val MAX_ZOOM = 15f
private const val MAX_DECODE_SIZE = 4096
private const val MAX_OUTPUT_SIZE = 2048
private val CropAreaPadding = 16.dp
private val OverlayColor = Color.Black.copy(alpha = 0.8f)

/**
 * [ImageCropper]의 이미지를 잘라 저장하는 상태
 */
@Stable
class ImageCropperState internal constructor(private val cacheDirectory: File) {
    internal var bitmap: Bitmap? by mutableStateOf(null)
    internal var geometry: CropGeometry? by mutableStateOf(null)

    /** 이미지를 다 불러오기 전에는 [crop]이 실패하니 확인 버튼을 막을 때 사용 */
    val isLoaded: Boolean
        get() = geometry != null

    /**
     * 박스 영역을 잘라 캐시에 PNG로 저장하는 함수
     *
     * 누끼 이미지를 잘라도 투명 배경이 남도록 PNG
     *
     * @param maxOutputSize 자른 이미지 긴 변 최대 px
     */
    suspend fun crop(maxOutputSize: Int = MAX_OUTPUT_SIZE): Result<Uri> {
        val source = bitmap
        val current = geometry
        if (source == null || current == null) return Result.failure(IllegalStateException("Image is not loaded"))
        return suspendRunCatching {
            withContext(Dispatchers.IO) {
                val cropped = source.cropped(current, maxOutputSize)
                val file = File(cacheDirectory, "cropped${System.currentTimeMillis()}.png")
                try {
                    file.outputStream().use { output ->
                        check(cropped.compress(Bitmap.CompressFormat.PNG, 100, output)) { "Image crop failed" }
                    }
                } finally {
                    cropped.recycle()
                }
                Uri.fromFile(file)
            }
        }
    }
}

@Composable
fun rememberImageCropperState(): ImageCropperState {
    val context = LocalContext.current
    return remember(context) { ImageCropperState(context.cacheDir) }
}

/**
 * 이미지 자르기 영역
 *
 * 가운데 박스는 고정. 한 손가락으로 사진을 옮기고 두 손가락으로 확대, 축소. 사진은 박스 밖으로 빠지지 않음
 *
 * @param aspectRatio 박스 비율 (너비 / 높이)
 * @param state 자르기는 [ImageCropperState.crop]
 */
@Composable
fun ImageCropper(
    imageUri: Uri?,
    aspectRatio: Float,
    modifier: Modifier = Modifier,
    state: ImageCropperState = rememberImageCropperState(),
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val paint = remember { Paint(Paint.FILTER_BITMAP_FLAG) }

    BoxWithConstraints(modifier = modifier.clipToBounds()) {
        val bounds = with(density) {
            Rect(0f, 0f, maxWidth.toPx(), maxHeight.toPx()).deflate(CropAreaPadding.toPx())
        }
        LaunchedEffect(state, imageUri, bounds, aspectRatio) {
            state.geometry = null
            val image = imageUri?.let { context.loadCropBitmap(it) } ?: return@LaunchedEffect
            state.bitmap = image
            state.geometry = CropGeometry.initial(bounds, aspectRatio, Size(image.width.toFloat(), image.height.toFloat()))
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(state) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        state.geometry = state.geometry?.transformed(centroid, pan, zoom)
                    }
                },
        ) {
            val image = state.bitmap ?: return@Canvas
            val geometry = state.geometry ?: return@Canvas
            val box = geometry.box
            drawIntoCanvas { it.nativeCanvas.drawBitmap(image, null, geometry.photoRect.toAndroidRectF(), paint) }
            clipRect(box.left, box.top, box.right, box.bottom, clipOp = ClipOp.Difference) {
                drawRect(OverlayColor)
            }
            drawRect(color = Color.White, topLeft = box.topLeft, size = box.size, style = Stroke(1.dp.toPx()))
        }
    }
}

/**
 * 고정된 자르기 박스와 그 위 사진의 배율, 중심 (px)
 */
internal data class CropGeometry(
    val box: Rect,
    val imageSize: Size,
    val scale: Float,
    val center: Offset,
) {
    // 사진이 박스를 꽉 채우는 배율
    private val minScale: Float
        get() = max(box.width / imageSize.width, box.height / imageSize.height)

    val photoRect: Rect
        get() {
            val size = imageSize * scale
            return Rect(center - Offset(size.width / 2, size.height / 2), size)
        }

    /**
     * 손가락 아래 사진 지점이 손가락을 따라가게 옮기고 확대하는 함수
     *
     * @param centroid `detectTransformGestures`가 주는 이전 손가락 중심
     */
    fun transformed(
        centroid: Offset,
        pan: Offset,
        zoom: Float,
    ): CropGeometry {
        val newScale = (scale * zoom).coerceIn(minScale, minScale * MAX_ZOOM)
        val newCenter = centroid + pan + (center - centroid) * (newScale / scale)
        // 사진 가장자리가 박스 안으로 들어오지 않을 만큼만 중심 이동
        val maxShiftX = max(0f, (imageSize.width * newScale - box.width) / 2)
        val maxShiftY = max(0f, (imageSize.height * newScale - box.height) / 2)
        return copy(
            scale = newScale,
            center = Offset(
                x = newCenter.x.coerceIn(box.center.x - maxShiftX, box.center.x + maxShiftX),
                y = newCenter.y.coerceIn(box.center.y - maxShiftY, box.center.y + maxShiftY),
            ),
        )
    }

    companion object {
        fun initial(
            bounds: Rect,
            aspectRatio: Float,
            imageSize: Size,
        ): CropGeometry {
            val width = min(bounds.width, bounds.height * aspectRatio)
            val boxSize = Size(width, width / aspectRatio)
            val box = Rect(bounds.center - Offset(boxSize.width / 2, boxSize.height / 2), boxSize)
            val scale = max(box.width / imageSize.width, box.height / imageSize.height)
            return CropGeometry(box = box, imageSize = imageSize, scale = scale, center = box.center)
        }
    }
}

// 픽셀을 읽어 잘라야 해서 하드웨어 비트맵을 끔
private suspend fun Context.loadCropBitmap(uri: Uri): Bitmap? {
    val request = ImageRequest.Builder(this)
        .data(uri)
        .size(MAX_DECODE_SIZE)
        .precision(Precision.INEXACT)
        .allowHardware(false)
        .memoryCachePolicy(CachePolicy.DISABLED)
        .build()
    return imageLoader.execute(request).image?.toBitmap()
}

// 박스 안을 원본 픽셀 크기로 자르되 긴 변은 maxOutputSize까지
private fun Bitmap.cropped(
    geometry: CropGeometry,
    maxOutputSize: Int,
): Bitmap {
    val box = geometry.box
    val outputScale = min(1f / geometry.scale, maxOutputSize / max(box.width, box.height))
    val output = Bitmap.createBitmap(
        (box.width * outputScale).roundToInt().coerceAtLeast(1),
        (box.height * outputScale).roundToInt().coerceAtLeast(1),
        Bitmap.Config.ARGB_8888,
    )
    val photo = geometry.photoRect.translate(-box.left, -box.top)
    val destination = RectF(
        photo.left * outputScale,
        photo.top * outputScale,
        photo.right * outputScale,
        photo.bottom * outputScale,
    )
    Canvas(output).drawBitmap(this, null, destination, Paint(Paint.FILTER_BITMAP_FLAG))
    return output
}
