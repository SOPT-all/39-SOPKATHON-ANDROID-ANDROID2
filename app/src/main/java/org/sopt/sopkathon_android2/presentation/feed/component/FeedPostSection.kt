package org.sopt.sopkathon_android2.presentation.feed.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.component.post.PostAuthorRow
import org.sopt.sopkathon_android2.core.designsystem.component.post.PostContent
import org.sopt.sopkathon_android2.core.designsystem.component.post.PostImageSection
import org.sopt.sopkathon_android2.core.designsystem.component.post.PostReactionBar
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.feed.FeedUiState
import org.sopt.sopkathon_android2.presentation.feed.model.FeedPostUiModel

@Composable
fun FeedPostSection(
    post: FeedPostUiModel,
    onAuthorClick: () -> Unit,
    onPostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onMoreClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        PostAuthorRow(
            author = post.author,
            targetLabel = post.targetLabel,
            scopeLabel = post.scopeLabel,
            createdAt = post.createdAt,
            onAuthorClick = onAuthorClick,
            onMoreClick = onMoreClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PostImageSection(
            imageUrl = post.imageUrl,
            hashtag = post.hashtag,
            modifier = Modifier.noRippleClickable(onClick = onPostClick),
        )
        PostReactionBar(
            likeCount = post.likeCount,
            commentCount = post.commentCount,
            isLiked = post.isLiked,
            onLikeClick = onLikeClick,
            onCommentClick = onCommentClick,
            onShareClick = onShareClick,
        )
        PostContent(
            title = post.title,
            content = post.content,
            modifier = Modifier.noRippleClickable(onClick = onPostClick),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FeedPostSectionPreview() {
    SopkathonTheme {
        FeedPostSection(
            post = FeedUiState.dummy.posts.first(),
            onAuthorClick = {},
            onPostClick = {},
            onLikeClick = {},
            onCommentClick = {},
            onMoreClick = {},
            onShareClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
