package org.sopt.sopkathon_android2.core.common.extensions

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.filterNotNull

// 서버 기본 limit. 한 페이지 앞서 요청해 목록 끝에서 기다리지 않게 하려는 값
private const val DEFAULT_THRESHOLD = 20

/**
 * 목록 끝에 가까워지면 다음 페이지를 부르는 함수
 *
 * 커서 페이징 화면에서 LazyColumn에 넘긴 [LazyListState]로 사용
 *
 * @param items 서버에서 받은 목록. 헤더, 로딩 아이템은 빼고 넘김
 * @param isLoading 요청 중이면 부르지 않음
 * @param threshold 끝에서 몇 개 전부터 부를지
 * @param onLoadMore 다음 페이지 요청
 */
@Composable
fun LazyListState.LoadMoreEffect(
    items: List<*>,
    isLoading: Boolean,
    threshold: Int = DEFAULT_THRESHOLD,
    onLoadMore: () -> Unit,
) {
    LoadMoreEffect(
        key = this,
        lastVisibleIndex = { layoutInfo.visibleItemsInfo.lastOrNull()?.index },
        totalItemsCount = { layoutInfo.totalItemsCount },
        items = items,
        isLoading = isLoading,
        threshold = threshold,
        onLoadMore = onLoadMore,
    )
}

/**
 * 그리드 끝에 가까워지면 다음 페이지를 부르는 함수
 *
 * 커서 페이징 화면에서 LazyVerticalGrid에 넘긴 [LazyGridState]로 사용. 인자는 [LazyListState.LoadMoreEffect]와 같음
 */
@Composable
fun LazyGridState.LoadMoreEffect(
    items: List<*>,
    isLoading: Boolean,
    threshold: Int = DEFAULT_THRESHOLD,
    onLoadMore: () -> Unit,
) {
    LoadMoreEffect(
        key = this,
        lastVisibleIndex = { layoutInfo.visibleItemsInfo.lastOrNull()?.index },
        totalItemsCount = { layoutInfo.totalItemsCount },
        items = items,
        isLoading = isLoading,
        threshold = threshold,
        onLoadMore = onLoadMore,
    )
}

@Composable
private fun LoadMoreEffect(
    key: Any,
    lastVisibleIndex: () -> Int?,
    totalItemsCount: () -> Int,
    items: List<*>,
    isLoading: Boolean,
    threshold: Int,
    onLoadMore: () -> Unit,
) {
    require(threshold >= 0) { "threshold cannot be negative, but was $threshold" }

    val currentItems by rememberUpdatedState(items)
    val loading by rememberUpdatedState(isLoading)
    val currentThreshold by rememberUpdatedState(threshold)
    val loadMore by rememberUpdatedState(onLoadMore)

    LaunchedEffect(key) {
        // 끝에 닿았을 때의 목록. 목록이 그대로면(요청 실패) 끝에 머무는 동안 다시 부르지 않음
        snapshotFlow {
            val lastVisible = lastVisibleIndex() ?: -1
            currentItems.takeIf { it.isNotEmpty() && lastVisible + currentThreshold >= totalItemsCount() - 1 }
        }
            .filterNotNull()
            .collect { if (!loading) loadMore() }
    }
}
