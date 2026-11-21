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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import java.io.File
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

private val MinStickerLength = 40.dp

// 선택 테두리는 스티커보다 이만큼 바깥에 그림
private val SelectionOutset = 6.dp
private val SelectionStrokeWidth = 1.5.dp
private val SelectionDashLength = 4.dp
private val SelectionHandleSize = 10.dp
private val ResizeHandleTouchSize = 24.dp
private val DeleteChipSize = 24.dp
private val SelectionActionGap = 12.dp

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
    internal var canvasSize by mutableStateOf(Size.Zero)

    fun clearSelection() {
        selectedKey = null
    }

    /**
     * 캔버스 영역만 투명 배경 PNG로 캐시에 저장하는 함수
     *
     * 선택 표시는 캡처 영역 밖에 그려서 같이 저장되지 않음. 업로드는 `PresignedUrlUploader.uploadPng`
     */
    suspend fun capture(): Result<Uri> = suspendRunCatching {
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
 * 선택 표시(점선 테두리, 핸들, 삭제 버튼, [selectionAction])는 캔버스 밖으로 나가도 그려지고 캡처에는 안 들어감.
 * 부모에 `clipToBounds`나 `verticalScroll`이 있으면 캔버스 밖 부분이 잘리고 눌리지 않음
 *
 * 스티커 이미지는 원본 크기로 불러옴. Coil은 처음 크기로만 불러와서 키우면 흐려짐
 *
 * ```
 * val stickerState = rememberStickerCanvasState()
 * val context = LocalContext.current
 * StickerCanvas(
 *     state = stickerState,
 *     onDeleteClick = { key -> viewModel.removeItem(key) },
 *     selectionAction = { key -> ActionChip(onClick = { viewModel.openSheet(key) }) },
 *     modifier = Modifier.size(190.dp, 500.dp),
 * ) {
 *     Sticker(key = "bottom", size = DpSize(105.dp, 228.dp), initialPosition = DpOffset(43.dp, 272.dp)) {
 *         AsyncImage(
 *             model = ImageRequest.Builder(context).data(bottomUri).size(Size.ORIGINAL).build(),
 *             contentDescription = null,
 *             modifier = Modifier.fillMaxSize(),
 *         )
 *     }
 *     AsyncImage(model = faceUri, contentDescription = null, modifier = Modifier.align(Alignment.TopCenter))
 * }
 * ```
 *
 * 스티커 목록을 반복해서 그릴 때는 `key(item.key) { Sticker(...) }`로 감싸야 지운 뒤에도 남은 스티커 자리가 유지됨
 *
 * @param onDeleteClick 선택한 스티커 오른쪽 위 삭제 버튼. null이면 버튼 없음. 누르면 그 스티커 자리를 지우고 호출
 * @param selectionAction 선택한 스티커 아래 가운데에 놓을 내용. 아래 자리가 캔버스 밖이면 스티커 위로 올림
 */
@Composable
fun StickerCanvas(
    state: StickerCanvasState,
    modifier: Modifier = Modifier,
    onDeleteClick: ((key: Any) -> Unit)? = null,
    selectionAction: (@Composable (key: Any) -> Unit)? = null,
    content: @Composable StickerCanvasScope.() -> Unit,
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
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

        val selectedKey = state.selectedKey
        val selectedRect = selectedKey?.let { state.rects[it] }
        if (selectedKey != null && selectedRect != null) {
            StickerSelection(
                state = state,
                selectedKey = selectedKey,
                rect = selectedRect,
                onDeleteClick = onDeleteClick,
            )
            if (selectionAction != null) {
                SelectionActionLayout(
                    rect = selectedRect,
                    canvasHeight = state.canvasSize.height,
                    modifier = Modifier.matchParentSize(),
                ) { selectionAction(selectedKey) }
            }
        }
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
    val rect = state.rects[key] ?: initialRect
    val isSelected = state.selectedKey == key

    // 선택 표시가 움직이기 전 스티커 자리도 알도록 처음 자리를 넣어 둠
    DisposableEffect(state, key) {
        if (key !in state.rects) state.rects[key] = currentInitialRect
        onDispose {
            if (state.selectedKey == key) state.clearSelection()
        }
    }

    Box(
        modifier = modifier
            .offset { IntOffset(rect.left.roundToInt(), rect.top.roundToInt()) }
            .size(with(density) { DpSize(rect.width.toDp(), rect.height.toDp()) })
            .pointerInput(key) { detectTapGestures { state.selectedKey = key } }
            .then(
                if (isSelected) {
                    Modifier.pointerInput(key) {
                        detectDragGestures { change, drag ->
                            change.consume()
                            // 다시 그려지기 전에 다음 드래그가 와도 최신 위치에서 계산하도록 상태에서 바로 읽음
                            val current = state.rects[key] ?: currentInitialRect
                            state.rects[key] = StickerGeometry.move(current, drag, state.canvasSize)
                        }
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        content()
    }
}

/**
 * 선택한 스티커를 감싸는 점선 테두리, 네 모서리 핸들, 오른쪽 위 삭제 버튼
 *
 * 오른쪽 아래 핸들만 크기 조절. 캡처 레이어 밖에 그림
 */
@Composable
private fun StickerSelection(
    state: StickerCanvasState,
    selectedKey: Any,
    rect: Rect,
    onDeleteClick: ((key: Any) -> Unit)?,
) {
    val density = LocalDensity.current
    val colors = SopkathonTheme.colors
    val minLength = with(density) { MinStickerLength.toPx() }
    val frame = with(density) { rect.inflate(SelectionOutset.toPx()) }
    val frameSize = with(density) { DpSize(frame.width.toDp(), frame.height.toDp()) }

    Box(
        modifier = Modifier
            .offset { IntOffset(frame.left.roundToInt(), frame.top.roundToInt()) }
            .size(frameSize)
            .drawBehind {
                val strokeWidth = SelectionStrokeWidth.toPx()
                val dash = SelectionDashLength.toPx()
                drawRect(
                    color = colors.coolGray950,
                    topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
                    size = Size(size.width - strokeWidth, size.height - strokeWidth),
                    style = Stroke(width = strokeWidth, pathEffect = PathEffect.dashPathEffect(floatArrayOf(dash, dash))),
                )
            },
    ) {
        SelectionHandle(modifier = Modifier.align(Alignment.TopStart).cornerOffset(-1, -1))
        SelectionHandle(modifier = Modifier.align(Alignment.BottomStart).cornerOffset(-1, 1))
        if (onDeleteClick == null) {
            SelectionHandle(modifier = Modifier.align(Alignment.TopEnd).cornerOffset(1, -1))
        }
        ResizeHandle(
            onDrag = { drag ->
                val current = state.rects[selectedKey] ?: return@ResizeHandle
                state.rects[selectedKey] = StickerGeometry.resize(current, drag, state.canvasSize, minLength)
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(ResizeHandleTouchSize / 2, ResizeHandleTouchSize / 2),
        )
        if (onDeleteClick != null) {
            DeleteChip(
                onClick = {
                    state.clearSelection()
                    state.rects.remove(selectedKey)
                    onDeleteClick(selectedKey)
                },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(DeleteChipSize / 2, -DeleteChipSize / 2),
            )
        }
    }
}

// 핸들 가운데가 테두리 모서리에 오도록 핸들 크기 반만큼 밖으로
private fun Modifier.cornerOffset(x: Int, y: Int): Modifier =
    offset(SelectionHandleSize / 2 * x, SelectionHandleSize / 2 * y)

@Composable
private fun SelectionHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(SelectionHandleSize)
            .background(SopkathonTheme.colors.white)
            .border(SelectionStrokeWidth, SopkathonTheme.colors.coolGray950),
    )
}

@Composable
private fun ResizeHandle(
    onDrag: (Offset) -> Unit,
    modifier: Modifier = Modifier,
) {
    val currentOnDrag by rememberUpdatedState(onDrag)

    Box(
        modifier = modifier
            .size(ResizeHandleTouchSize)
            .pointerInput(Unit) {
                detectDragGestures { change, drag ->
                    change.consume()
                    currentOnDrag(drag)
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        SelectionHandle()
    }
}

@Composable
private fun DeleteChip(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(DeleteChipSize)
            .clip(CircleShape)
            .background(SopkathonTheme.colors.coolGray950)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_sticker_delete_20),
            contentDescription = "스티커 삭제",
            tint = Color.Unspecified,
        )
    }
}

/**
 * [content]를 선택 테두리 아래 가운데에 놓는 레이아웃. 캔버스 아래로 나가면 테두리 위로 올림
 */
@Composable
private fun SelectionActionLayout(
    rect: Rect,
    canvasHeight: Float,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Layout(content = content, modifier = modifier) { measurables, constraints ->
        val placeable = measurables.first().measure(Constraints())
        val outset = SelectionOutset.toPx()
        val gap = SelectionActionGap.toPx()
        val frameBottom = rect.bottom + outset
        val below = frameBottom + gap
        val y = if (below + placeable.height <= canvasHeight) {
            below
        } else {
            rect.top - outset - gap - placeable.height
        }
        val x = rect.center.x - placeable.width / 2f
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(x.roundToInt(), y.roundToInt())
        }
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
    SopkathonTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center,
        ) {
            val state = rememberStickerCanvasState()
            StickerCanvas(
                state = state,
                onDeleteClick = {},
                selectionAction = { Text(text = "선택한 스티커: $it") },
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
}
