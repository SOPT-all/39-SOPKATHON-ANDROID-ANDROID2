package org.sopt.sopkathon_android2.presentation.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.search.component.PopularLookThumbnail
import org.sopt.sopkathon_android2.presentation.search.component.PopularPostCard
import org.sopt.sopkathon_android2.presentation.search.component.SearchBarRow

// 6 검색
@Composable
fun SearchRoute(
    onSearchClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(viewModel) {
        viewModel.loadPopularPosts()
    }

    SearchScreen(
        uiState = uiState,
        onSearchClick = onSearchClick,
        onPostClick = onPostClick,
        modifier = modifier,
    )
}

@Composable
private fun SearchScreen(
    uiState: SearchUiState,
    onSearchClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPreview = LocalInspectionMode.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = "검색",
                style = SopkathonTheme.typography.screenTitle,
                color = SopkathonTheme.colors.coolGray950,
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                SearchBarRow(
                    onClick = onSearchClick,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(
                    text = "이번 주 인기 코디",
                    style = SopkathonTheme.typography.itemName,
                    color = SopkathonTheme.colors.coolGray950,
                    modifier = Modifier
                        .padding(bottom = 12.dp)
                        .height(24.dp),
                )
            }
            itemsIndexed(uiState.popularPosts, key = { _, post -> post.postId }) { index, post ->
                PopularPostCard(
                    title = post.title,
                    imageUrl = post.imageUrl,
                    likeCount = post.likeCount,
                    onClick = { onPostClick(post.postId) },
                    modifier = Modifier.padding(bottom = 16.dp),
                    thumbnail = if (isPreview) ({ PopularLookThumbnail(index) }) else null,
                )
            }
            val status = when {
                uiState.isLoading -> "인기 코디를 불러오는 중이에요."
                uiState.errorMessage != null -> uiState.errorMessage
                uiState.popularPosts.isEmpty() -> "이번 주 인기 코디가 아직 없어요."
                else -> null
            }
            if (status != null) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = status,
                            style = SopkathonTheme.typography.bodySmall,
                            color = SopkathonTheme.colors.coolGray500,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@ScreenPreview
@Composable
private fun SearchScreenPreview() {
    SopkathonTheme {
        SearchScreen(
            uiState = SearchUiState.preview,
            onSearchClick = {},
            onPostClick = {},
        )
    }
}
