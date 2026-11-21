package org.sopt.sopkathon_android2.presentation.search.searchinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.common.extensions.LoadMoreEffect
import org.sopt.sopkathon_android2.core.common.extensions.clearFocusOnTap
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.search.searchinput.component.RecentSearchRow
import org.sopt.sopkathon_android2.presentation.search.searchinput.component.SearchInputField
import org.sopt.sopkathon_android2.presentation.search.searchinput.component.SearchColleagueItem

@Composable
fun SearchInputRoute(
    navigateUp: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchInputViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val keyboard = LocalSoftwareKeyboardController.current

    fun dismissKeyboard() {
        keyboard?.hide()
        focusManager.clearFocus()
    }

    LaunchedEffect(focusRequester) {
        focusRequester.requestFocus()
    }

    SearchInputScreen(
        uiState = uiState,
        focusRequester = focusRequester,
        queryState = viewModel.queryState,
        onSubmit = {
            viewModel.submitQuery()
            dismissKeyboard()
        },
        onCancel = {
            dismissKeyboard()
            navigateUp()
        },
        onPersonClick = { id ->
            viewModel.submitQuery()
            dismissKeyboard()
            onColleagueClick(id)
        },
        onRecentClick = { query ->
            viewModel.selectRecentQuery(query)
            dismissKeyboard()
        },
        onLoadMore = viewModel::loadMore,
        onRemoveRecent = viewModel::removeRecentQuery,
        onClearRecent = viewModel::clearRecentQueries,
        modifier = modifier,
    )
}

@Composable
private fun SearchInputScreen(
    uiState: SearchInputUiState,
    focusRequester: FocusRequester,
    queryState: TextFieldState,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
    onPersonClick: (Long) -> Unit,
    onRecentClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRemoveRecent: (String) -> Unit,
    onClearRecent: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    var isFieldFocused by rememberSaveable { mutableStateOf(false) }
    // 검색어 없이 입력창에 포커스 있을 때만 최근 검색 노출
    val showRecent = isFieldFocused && queryState.text.isBlank()
    LaunchedEffect(uiState.query) {
        listState.scrollToItem(0)
    }
    listState.LoadMoreEffect(
        items = uiState.results,
        isLoading = uiState.isLoading,
        onLoadMore = onLoadMore,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white)
            .imePadding()
            .clearFocusOnTap(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchInputField(
                state = queryState,
                onSearch = onSubmit,
                focusRequester = focusRequester,
                modifier = Modifier
                .weight(1f)
                .onFocusChanged { isFieldFocused = it.hasFocus },
            )
            Box(
                modifier = Modifier.height(48.dp).noRippleClickable(onClick = onCancel),
                contentAlignment = Alignment.Center,
            ) {
                Text("취소", style = SopkathonTheme.typography.body, color = SopkathonTheme.colors.coolGray950)
            }
        }

        LazyColumn(state = listState, modifier = Modifier.weight(1f)) {
            if (!showRecent && uiState.query.isNotBlank()) {
                item(key = "result-heading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(start = 16.dp, top = 16.dp, bottom = 8.dp),
                    ) {
                        Text(
                            text = if (uiState.isLoading || uiState.hasError || uiState.nextCursor != null) {
                                // 다음 페이지가 남아 있으면 전체 수를 알 수 없어 숫자 생략
                                "검색 결과"
                            } else {
                                "검색 결과 ${uiState.results.size}명"
                            },
                            style = SopkathonTheme.typography.subtitle,
                            color = SopkathonTheme.colors.coolGray950,
                        )
                    }
                }
                items(uiState.results, key = { it.userId }) { colleague ->
                    SearchColleagueItem(
                        name = colleague.name,
                        rank = colleague.rank,
                        department = colleague.department,
                        profileImageUrl = colleague.profileImageUrl,
                        onClick = { onPersonClick(colleague.userId) },
                    )
                }
                if (uiState.isLoading || uiState.hasError) {
                    item(key = "search-status") {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = if (uiState.isLoading) "검색 중…" else "검색 결과를 불러오지 못했어요.",
                                style = SopkathonTheme.typography.bodySmall,
                                color = SopkathonTheme.colors.coolGray500,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }
            }

            if (showRecent && uiState.recentQueries.isNotEmpty()) {
                item(key = "recent-heading") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = "최근 검색",
                            style = SopkathonTheme.typography.subtitle,
                            color = SopkathonTheme.colors.coolGray950,
                        )
                        Box(
                            modifier = Modifier.height(42.dp).noRippleClickable(onClick = onClearRecent),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "전체 삭제",
                                style = SopkathonTheme.typography.caption,
                                color = SopkathonTheme.colors.coolGray500,
                            )
                        }
                    }
                }
                items(uiState.recentQueries, key = { "recent:$it" }) { query ->
                    RecentSearchRow(
                        query = query,
                        onClick = { onRecentClick(query) },
                        onRemove = { onRemoveRecent(query) },
                    )
                }
            }
        }
    }
}

@ScreenPreview
@Composable
private fun SearchInputScreenPreview() {
    SopkathonTheme {
        SearchInputScreen(
            uiState = SearchInputUiState(
                query = "박정",
                results = listOf(
                    SearchColleague(1L, "박정훈", "부장", "영업1팀", ""),
                ),
                recentQueries = listOf("이준호", "최은지 과장"),
            ),
            focusRequester = remember { FocusRequester() },
            queryState = rememberTextFieldState("박정"),
            onSubmit = {},
            onCancel = {},
            onPersonClick = {},
            onRecentClick = {},
            onLoadMore = {},
            onRemoveRecent = {},
            onClearRecent = {},
        )
    }
}
