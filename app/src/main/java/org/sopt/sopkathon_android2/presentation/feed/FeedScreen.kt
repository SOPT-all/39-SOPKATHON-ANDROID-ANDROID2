package org.sopt.sopkathon_android2.presentation.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Alignment
import androidx.compose.runtime.LaunchedEffect
import org.sopt.sopkathon_android2.core.common.extensions.LoadMoreEffect
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.feed.component.FeedHeader
import org.sopt.sopkathon_android2.presentation.feed.component.FeedPostSection
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScope
import org.sopt.sopkathon_android2.presentation.feed.component.FeedScopeTabs
import org.sopt.sopkathon_android2.presentation.feed.component.FeedTeamLookBanner

@Composable
fun FeedRoute(
    onColleagueClick: (Long) -> Unit,
    onPostClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    FeedScreen(
        uiState = uiState,
        onScopeClick = viewModel::selectScope,
        onLoadMore = viewModel::loadMore,
        onRetry = viewModel::retry,
        onLikeClick = viewModel::toggleLike,
        onColleagueClick = onColleagueClick,
        onPostClick = onPostClick,
        onNotificationClick = { },
        onTeamLookClick = { },
        onMoreClick = { },
        onShareClick = { },
        modifier = modifier,
    )
}

@Composable
private fun FeedScreen(
    uiState: FeedUiState,
    onScopeClick: (FeedScope) -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onLikeClick: (Long) -> Unit,
    onColleagueClick: (Long) -> Unit,
    onPostClick: (Long) -> Unit,
    onNotificationClick: () -> Unit,
    onTeamLookClick: () -> Unit,
    onMoreClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    LaunchedEffect(uiState.selectedScope) {
        listState.scrollToItem(0)
    }
    listState.LoadMoreEffect(
        items = uiState.posts.map { it.postId },
        isLoading = uiState.isLoading,
        threshold = 3,
        onLoadMore = onLoadMore,
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white),
    ) {
        FeedHeader(onNotificationClick = onNotificationClick)
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 24.dp),
        ) {
            item(key = "scope") {
                FeedScopeTabs(
                    selectedScope = uiState.selectedScope,
                    onScopeClick = onScopeClick,
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 12.dp),
                )
            }
            item(key = "teamLook") {
                FeedTeamLookBanner(
                    title = "금요일 회식룩을 골라 주세요",
                    deadlineLabel = "D-2",
                    participationText = "마케팅팀 12명 중 5명 참여",
                    onClick = onTeamLookClick,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            items(
                items = uiState.posts,
                key = { it.postId },
            ) { post ->
                FeedPostSection(
                    post = post,
                    onAuthorClick = { onColleagueClick(post.author.userId) },
                    onPostClick = { onPostClick(post.postId) },
                    onLikeClick = { onLikeClick(post.postId) },
                    onCommentClick = { onPostClick(post.postId) },
                    onMoreClick = onMoreClick,
                    onShareClick = onShareClick,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
                Spacer(modifier = Modifier.height(20.dp))
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .background(SopkathonTheme.colors.coolGray50),
                )
                Spacer(modifier = Modifier.height(20.dp))
            }
            item(key = "pageStatus") {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    when {
                        uiState.isLoading -> CircularProgressIndicator(
                            color = SopkathonTheme.colors.coolGray950,
                        )
                        uiState.loadError != null -> Column {
                            Text(text = uiState.loadError)
                            TextButton(onClick = onRetry) { Text(text = "다시 시도") }
                        }
                        uiState.posts.isEmpty() -> Text(text = "아직 게시글이 없어요")
                        !uiState.hasMore -> Text(
                            text = "모든 게시글을 확인했어요",
                            style = SopkathonTheme.typography.caption,
                            color = SopkathonTheme.colors.coolGray500,
                        )
                    }
                }
            }
        }
    }
}

@ScreenPreview
@Composable
private fun FeedScreenPreview() {
    SopkathonTheme {
        FeedScreen(
            uiState = FeedUiState.dummy,
            onScopeClick = {},
            onLoadMore = {},
            onRetry = {},
            onLikeClick = {},
            onColleagueClick = {},
            onPostClick = {},
            onNotificationClick = {},
            onTeamLookClick = {},
            onMoreClick = {},
            onShareClick = {},
        )
    }
}
