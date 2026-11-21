package org.sopt.sopkathon_android2.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.currentStateAsState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

private val SnackbarShape = RoundedCornerShape(12.dp)

// M3 SnackbarDuration.Short, Long과 같은 값
private const val SHORT_DURATION_MILLIS = 4_000L
private const val LONG_DURATION_MILLIS = 10_000L

enum class SopkathonSnackbarType(
    @get:DrawableRes internal val iconRes: Int,
) {
    Error(R.drawable.ic_snackbar_error_20),
    Success(R.drawable.ic_snackbar_success_20),
}

/**
 * [SopkathonSnackbarHost]에 띄울 스낵바 내용
 *
 * `LocalSopkathonSnackbarState.current.show(SopkathonSnackbarVisuals(...))`로 사용
 *
 * @param description 메시지 아래 보조 문구
 * @param actionLabel 오른쪽 텍스트 버튼. 누르면 [SopkathonSnackbarState.show]의 `onActionClick` 호출
 */
data class SopkathonSnackbarVisuals(
    override val message: String,
    val type: SopkathonSnackbarType = SopkathonSnackbarType.Error,
    val description: String? = null,
    override val actionLabel: String? = null,
) : SnackbarVisuals {
    override val duration: SnackbarDuration = SnackbarDuration.Indefinite
    override val withDismissAction: Boolean = false
}

/**
 * 앱 전체가 함께 쓰는 스낵바 상태
 *
 * `MainScreen`에서 [LocalSopkathonSnackbarState]로 제공
 */
@Stable
class SopkathonSnackbarState internal constructor(
    internal val hostState: SnackbarHostState,
    private val scope: CoroutineScope,
) {
    private val pendingVisuals = mutableListOf<SopkathonSnackbarVisuals>()

    /**
     * 스낵바를 띄우는 함수
     *
     * 띄운 직후 화면을 이동해도 다음 화면의 [SopkathonSnackbarHost]에 이어서 표시
     * 이미 떠 있으면 앞의 것이 닫힌 뒤 표시. 같은 내용이 떠 있거나 대기 중이면 무시
     *
     * @param onActionClick [SopkathonSnackbarVisuals.actionLabel]을 눌렀을 때 호출. 화면을 떠난 뒤에도 호출될 수 있음
     */
    fun show(
        visuals: SopkathonSnackbarVisuals,
        onActionClick: () -> Unit = {},
    ) {
        if (visuals in pendingVisuals) return
        pendingVisuals += visuals
        scope.launch {
            try {
                if (hostState.showSnackbar(visuals) == SnackbarResult.ActionPerformed) onActionClick()
            } finally {
                pendingVisuals -= visuals
            }
        }
    }
}

@Composable
fun rememberSopkathonSnackbarState(): SopkathonSnackbarState {
    val scope = rememberCoroutineScope()
    val state = remember(scope) { SopkathonSnackbarState(SnackbarHostState(), scope) }
    val currentData = state.hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current

    LaunchedEffect(currentData) {
        if (currentData == null) return@LaunchedEffect
        val hasAction = currentData.visuals.actionLabel != null
        // 액션을 누를 시간 확보
        val millis = if (hasAction) LONG_DURATION_MILLIS else SHORT_DURATION_MILLIS
        delay(
            accessibilityManager?.calculateRecommendedTimeoutMillis(
                originalTimeoutMillis = millis,
                containsIcons = true,
                containsText = true,
                containsControls = hasAction,
            ) ?: millis,
        )
        currentData.dismiss()
    }
    return state
}

val LocalSopkathonSnackbarState = staticCompositionLocalOf<SopkathonSnackbarState> {
    error("LocalSopkathonSnackbarState 없음. MainScreen 안에서 사용")
}

/**
 * 콘텐츠 위에 겹쳐 뜨는 스낵바 호스트
 *
 * top app bar 아래 콘텐츠 `Box` 마지막에 `Modifier.align(Alignment.TopCenter)`로 배치
 */
@Composable
fun SopkathonSnackbarHost(
    modifier: Modifier = Modifier,
) {
    if (LocalInspectionMode.current) return

    val lifecycleState by LocalLifecycleOwner.current.lifecycle.currentStateAsState()
    if (!lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) return

    SnackbarHost(
        hostState = LocalSopkathonSnackbarState.current.hostState,
        modifier = modifier,
    ) { snackbarData ->
        val visuals = snackbarData.visuals as SopkathonSnackbarVisuals
        SopkathonSnackbar(
            message = visuals.message,
            modifier = Modifier.padding(horizontal = 16.dp),
            type = visuals.type,
            description = visuals.description,
            actionLabel = visuals.actionLabel,
            onActionClick = snackbarData::performAction,
        )
    }
}

@Composable
private fun SopkathonSnackbar(
    message: String,
    modifier: Modifier = Modifier,
    type: SopkathonSnackbarType = SopkathonSnackbarType.Error,
    description: String? = null,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .dropShadow(
                shape = SnackbarShape,
                shadow = Shadow(
                    radius = 12.dp,
                    color = Color.Black.copy(alpha = 0.12f),
                    offset = DpOffset(x = 0.dp, y = 4.dp),
                ),
            )
            .background(color = SopkathonTheme.colors.coolGray900, shape = SnackbarShape)
            .padding(start = 16.dp, top = 12.dp, end = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = ImageVector.vectorResource(type.iconRes),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            // 두 가지 색이라 원본 색 유지
            tint = Color.Unspecified,
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = message,
                color = SopkathonTheme.colors.white,
                style = SopkathonTheme.typography.button,
            )
            if (description != null) {
                Text(
                    text = description,
                    color = SopkathonTheme.colors.white.copy(alpha = 0.7f),
                    style = SopkathonTheme.typography.caption,
                )
            }
        }

        if (actionLabel != null) {
            Text(
                text = actionLabel,
                modifier = Modifier.noRippleClickable(onClick = onActionClick),
                color = SopkathonTheme.colors.pink,
                style = SopkathonTheme.typography.button,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SopkathonSnackbarErrorPreview() {
    SopkathonTheme {
        SopkathonSnackbar(
            message = "얼굴은 가릴 수 없어요. 옷은 스테이지 안에서만 옮길 수 있어요",
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SopkathonSnackbarSuccessPreview() {
    SopkathonTheme {
        SopkathonSnackbar(
            message = "코디를 게시했어요!",
            modifier = Modifier.padding(16.dp),
            type = SopkathonSnackbarType.Success,
            description = "마케팅팀 동료들에게 보여요",
            actionLabel = "공유하기",
        )
    }
}
