package org.sopt.sopkathon_android2.core.common.extensions

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.focus.FocusEventModifierNode
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.platform.LocalFocusManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

fun Modifier.noRippleClickable(
    enabled: Boolean = true,
    onClick: () -> Unit,
): Modifier = composed {
    clickable(
        interactionSource = remember { MutableInteractionSource() },
        indication = null,
        enabled = enabled,
        onClick = onClick,
    )
}

/**
 * 빈 곳을 누르면 키보드를 내리는 함수
 *
 * 입력 화면의 가장 바깥 레이아웃에 사용
 */
fun Modifier.clearFocusOnTap(): Modifier = composed {
    val focusManager = LocalFocusManager.current
    pointerInput(focusManager) {
        detectTapGestures { focusManager.clearFocus() }
    }
}

/**
 * 안쪽에 포커스가 있는 동안 스크롤을 끝까지 내리는 함수
 *
 * 입력칸이 스크롤 영역 맨 아래에 있을 때만 사용 (중간에 있으면 입력칸이 위로 밀려 가려짐)
 * 키보드나 에러 문구로 길이가 바뀌어도 다시 끝으로 맞춤. `verticalScroll` 앞에 사용
 *
 * @param scrollState `verticalScroll`에 넘긴 것과 같은 [ScrollState]
 */
fun Modifier.scrollToEndOnFocus(scrollState: ScrollState): Modifier = this then ScrollToEndOnFocusElement(scrollState)

private data class ScrollToEndOnFocusElement(
    private val scrollState: ScrollState,
) : ModifierNodeElement<ScrollToEndOnFocusNode>() {
    override fun create(): ScrollToEndOnFocusNode = ScrollToEndOnFocusNode(scrollState)

    override fun update(node: ScrollToEndOnFocusNode) {
        node.update(scrollState)
    }
}

private class ScrollToEndOnFocusNode(
    private var scrollState: ScrollState,
) : Modifier.Node(),
    FocusEventModifierNode {
    private var scrollJob: Job? = null

    override fun onFocusEvent(focusState: FocusState) {
        if (!focusState.hasFocus) {
            scrollJob?.cancel()
            scrollJob = null
            return
        }
        if (scrollJob?.isActive == true) return
        scrollJob = coroutineScope.launch {
            snapshotFlow { scrollState.maxValue }.collect { scrollState.scrollTo(it) }
        }
    }

    fun update(scrollState: ScrollState) {
        if (this.scrollState === scrollState) return
        scrollJob?.cancel()
        scrollJob = null
        this.scrollState = scrollState
    }
}
