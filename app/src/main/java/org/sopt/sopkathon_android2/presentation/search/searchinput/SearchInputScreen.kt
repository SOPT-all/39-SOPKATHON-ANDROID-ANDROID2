package org.sopt.sopkathon_android2.presentation.search.searchinput

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.search.searchinput.component.SearchColleagueItem
import org.sopt.sopkathon_android2.presentation.search.searchinput.component.SearchInputField

// 6.1 검색 입력
@Composable
fun SearchInputRoute(
    navigateUp: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchInputViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchInputScreen(
        uiState = uiState,
        queryState = viewModel.queryState,
        onCancelClick = navigateUp,
        onColleagueClick = onColleagueClick,
        modifier = modifier,
    )
}

@Composable
private fun SearchInputScreen(
    uiState: SearchInputUiState,
    queryState: TextFieldState,
    onCancelClick: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }

    // 진입하자마자 키보드 올림
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white)
            .imePadding(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            SearchInputField(
                state = queryState,
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester),
            )
            Text(
                text = "취소",
                style = SopkathonTheme.typography.itemName,
                color = SopkathonTheme.colors.coolGray950,
                modifier = Modifier.noRippleClickable(onClick = onCancelClick),
            )
        }

        if (uiState.query.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp)
                    .height(24.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "검색 결과 ${uiState.results.size}명",
                    style = SopkathonTheme.typography.itemName,
                    color = SopkathonTheme.colors.coolGray950,
                )
            }
            LazyColumn {
                items(uiState.results, key = { it.userId }) { colleague ->
                    SearchColleagueItem(
                        name = colleague.name,
                        rank = colleague.rank,
                        department = colleague.department,
                        profileImageUrl = colleague.profileImageUrl,
                        onClick = { onColleagueClick(colleague.userId) },
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
                    SearchColleague(userId = 1L, name = "박정훈", rank = "부장", department = "영업1팀", profileImageUrl = ""),
                ),
            ),
            queryState = rememberTextFieldState("박정"),
            onCancelClick = {},
            onColleagueClick = {},
        )
    }
}
