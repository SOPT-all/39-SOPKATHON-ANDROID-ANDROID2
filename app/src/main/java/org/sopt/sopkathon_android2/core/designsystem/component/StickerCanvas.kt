package org.sopt.sopkathon_android2.core.designsystem.component

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

// 선택 해제가 그려진 뒤에 캡처하도록 기다리는 프레임 수
private const val CAPTURE_WAIT_FRAMES = 2
private val MinStickerLength = 40.dp
private val HandleTouchSize = 24.dp
private val HandleSize = 8.dp

/**
 * [StickerCanvas]에서 선택한 스티커, 스티커 위치, 캡처를 가진 상태
 */
@Stable
class StickerCanvasState internal constructor(
    internal val layer: GraphicsLayer,
    private val cacheDirectory: File,
) {
    var selectedKey: Any? by mutableStateOf(null)
        internal set
    internal val rects = mutableStateMapOf<Any, Rect>()
    internal var canvasSize = Size.Zero

    fun clearSelection() {
        selectedKey = null
    }

    /**
     * 선택을 풀고 캔버스 영역만 투명 배경 PNG로 캐시에 저장하는 함수
     *
     * 프레임을 기다려서 `rememberCoroutineScope()`에서 호출. 업로드는 `PresignedUrlUploader.uploadPng`
     */
    suspend fun capture(): Result<Uri> = suspendRunCatching {
        clearSelection()
        repeat(CAPTURE_WAIT_FRAMES) { withFrameNanos { } }
        val bitmap = layer.toImageBitmap().asAndroidBitmap()
        withContext(Dispatchers.IO) {
            val file = File(cacheDirectory, "captured${System.currentTimeMillis()}.png")
            file.outputStream().use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) { "Capture failed" }
            }
            Uri.fromFile(file)
        }
    }
}

@Composable
fun rememberStickerCanvasState(): StickerCanvasState {
    val context = LocalContext.current
    val layer = rememberGraphicsLayer()
    return remember(layer, context) { StickerCanvasState(layer, context.cacheDir) }
}

/**
 * 스티커를 붙이는 캔버스
 *
 * 캔버스 영역이 그대로 캡처되니 캐릭터 크기로 둠. 스티커도 이 안에서만 움직임.
 * 배경은 캔버스 바깥에 둬야 투명 배경으로 캡처됨
 *
 * 안에서 [Sticker]로 붙인 것만 탭하면 선택, 선택한 것만 드래그로 이동하고 오른쪽 아래 핸들로 크기 조절.
 * 캔버스 빈 곳을 탭하면 선택 해제. 먼저 부른 것이 아래에 깔림
 *
 * ```
 * val stickerState = rememberStickerCanvasState()
 * StickerCanvas(state = stickerState, modifier = Modifier.size(190.dp, 500.dp)) {
 *     Sticker(key = "bottom", size = DpSize(105.dp, 228.dp), initialPosition = DpOffset(43.dp, 272.dp)) {
 *         AsyncImage(model = bottomUri, contentDescription = null, modifier = Modifier.fillMaxSize())
 *     }
 *     AsyncImage(model = faceUri, contentDescription = null, modifier = Modifier.align(Alignment.TopCenter))
 *     Sticker(key = "top", size = DpSize(190.dp, 213.dp), initialPosition = DpOffset(0.dp, 102.dp)) {
 *         AsyncImage(model = topUri, contentDescription = null, modifier = Modifier.fillMaxSize())
 *     }
 * }
 * ```
 */
@Composable
fun StickerCanvas(
    state: StickerCanvasState,
    modifier: Modifier = Modifier,
    content: @Composable StickerCanvasScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .onSizeChanged { state.canvasSize = it.toSize() }
            .drawWithContent {
                state.layer.record { this@drawWithContent.drawContent() }
                drawLayer(state.layer)
            }
            .pointerInput(state) { detectTapGestures { state.clearSelection() } },
    ) {
        val scope = remember(state, this) { StickerCanvasScope(state, this) }
        scope.content()
    }
}

@Stable
class StickerCanvasScope internal constructor(
    internal val state: StickerCanvasState,
    boxScope: BoxScope,
) : BoxScope by boxScope

/**
 * [StickerCanvas] 안에서 옮기고 크기를 바꾸는 스티커
 *
 * @param key 스티커마다 다른 값. 위치와 선택을 이 값으로 기억
 * @param size 처음 크기
 * @param initialPosition 처음 왼쪽 위 자리
 * @param content 스티커 크기를 채우도록 `Modifier.fillMaxSize()` 사용
 */
@Composable
fun StickerCanvasScope.Sticker(
    key: Any,
    size: DpSize,
    initialPosition: DpOffset,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // 스티커 내용 Box 안에서는 바깥 스코프의 state를 바로 못 읽음
    val state = state
    val density = LocalDensity.current
    val initialRect = with(density) {
        Rect(Offset(initialPosition.x.toPx(), initialPosition.y.toPx()), size.toSize())
    }
    val currentInitialRect by rememberUpdatedState(initialRect)
    val minLength = with(density) { MinStickerLength.toPx() }
    val rect = state.rects[key] ?: initialRect
    val isSelected = state.selectedKey == key

    // 다시 그려지기 전에 다음 드래그가 와도 최신 위치에서 계산하도록 상태에서 바로 읽음
    fun updateRect(change: (Rect) -> Rect) {
        state.rects[key] = change(state.rects[key] ?: currentInitialRect)
    }

    Box(
        modifier = modifier
            .offset { IntOffset(rect.left.roundToInt(), rect.top.roundToInt()) }
            .size(with(density) { DpSize(rect.width.toDp(), rect.height.toDp()) })
            .pointerInput(key) { detectTapGestures { state.selectedKey = key } }
            .then(
                if (isSelected) {
                    Modifier
                        .border(1.dp, Color.Black)
                        .pointerInput(key) {
                            detectDragGestures { change, drag ->
                                change.consume()
                                updateRect { StickerGeometry.move(it, drag, state.canvasSize) }
                            }
                        }
                } else {
                    Modifier
                },
            ),
    ) {
        content()
        if (isSelected) {
            ResizeHandle(
                onDrag = { drag -> updateRect { StickerGeometry.resize(it, drag, state.canvasSize, minLength) } },
                modifier = Modifier.align(Alignment.BottomEnd),
            )
        }
    }
}

@Composable
private fun ResizeHandle(
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnDrag by rememberUpdatedState(onDrag)

    Box(
        modifier = modifier
            .size(HandleTouchSize)
            .pointerInput(Unit) {
                detectDragGestures { change, drag ->
                    change.consume()
                    currentOnDrag(drag)
                }
            },
        contentAlignment = Alignment.BottomEnd,
    ) {
        Box(
            modifier = Modifier
                .size(HandleSize)
                .background(Color.White)
                .border(1.dp, Color.Black),
        )
    }
}

/**
 * 스티커 이동, 크기 조절 계산 (px)
 */
internal object StickerGeometry {
    // 캔버스 밖으로는 못 옮김 (와이어 E18)
    fun move(
        rect: Rect,
        drag: Offset,
        canvasSize: Size,
    ): Rect {
        val left = (rect.left + drag.x).coerceIn(0f, max(0f, canvasSize.width - rect.width))
        val top = (rect.top + drag.y).coerceIn(0f, max(0f, canvasSize.height - rect.height))
        return Rect(Offset(left, top), rect.size)
    }

    /**
     * 왼쪽 위를 고정하고 비율을 유지한 채 크기를 바꾸는 함수
     *
     * [drag]를 대각선에 투영한 만큼 커지거나 작아지고, 캔버스 끝과 [minLength] 사이에서 멈춤
     */
    fun resize(
        rect: Rect,
        drag: Offset,
        canvasSize: Size,
        minLength: Float,
    ): Rect {
        val projection = (drag.x * rect.width + drag.y * rect.height) /
            (rect.width * rect.width + rect.height * rect.height)
        val maxScale = max(min((canvasSize.width - rect.left) / rect.width, (canvasSize.height - rect.top) / rect.height), 1f)
        val minScale = min(minLength / min(rect.width, rect.height), 1f)
        val scale = (1 + projection).coerceIn(min(minScale, maxScale), maxScale)
        return Rect(rect.topLeft, rect.size * scale)
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun StickerCanvasPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray),
        contentAlignment = Alignment.Center,
    ) {
        StickerCanvas(
            state = rememberStickerCanvasState(),
            modifier = Modifier.size(190.dp, 500.dp),
        ) {
            Sticker(key = "bottom", size = DpSize(105.dp, 228.dp), initialPosition = DpOffset(43.dp, 272.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Blue))
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .size(95.dp, 115.dp)
                    .background(Color.DarkGray),
            )
            Sticker(key = "top", size = DpSize(190.dp, 213.dp), initialPosition = DpOffset(0.dp, 102.dp)) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Magenta))
            }
        }
    }
}
