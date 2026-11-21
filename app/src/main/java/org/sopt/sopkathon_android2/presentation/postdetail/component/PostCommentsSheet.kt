package org.sopt.sopkathon_android2.presentation.postdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.LoadMoreEffect
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.common.util.RelativeTimeFormatter
import org.sopt.sopkathon_android2.core.designsystem.component.SopkathonSnackbarHost
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.postdetail.PostDetailUiState
import org.sopt.sopkathon_android2.presentation.postdetail.model.CommentUiModel

@Composable
fun PostCommentsSheet(
    uiState: PostDetailUiState,
    onDraftChange: (String) -> Unit,
    onPostClick: () -> Unit,
    onLoadMore: () -> Unit,
    onRetry: () -> Unit,
    onAuthorClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    listState.LoadMoreEffect(
        items = uiState.comments.map { it.commentId },
        isLoading = uiState.isLoadingComments,
        threshold = 3,
        onLoadMore = onLoadMore,
    )
    Box(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.55f).imePadding()) {
            Text(
                "댓글 ${uiState.detail?.post?.commentCount ?: 0}",
                modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 16.dp),
                style = SopkathonTheme.typography.subtitle,
                color = SopkathonTheme.colors.coolGray950,
            )
            HorizontalDivider(color = SopkathonTheme.colors.coolGray50)
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                items(uiState.comments, key = { it.commentId }) { comment ->
                    PostCommentRow(comment, comment.userId == uiState.detail?.targetUserId, onAuthorClick)
                }
                item {
                    when {
                        uiState.isLoadingComments -> Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = SopkathonTheme.colors.coolGray950)
                        }
                        uiState.commentsError != null -> Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                            Text(uiState.commentsError, style = SopkathonTheme.typography.bodySmall)
                            TextButton(onClick = onRetry) { Text("다시 시도", color = SopkathonTheme.colors.coolGray950) }
                        }
                        uiState.commentsLoaded && uiState.comments.isEmpty() -> Text(
                            "첫 댓글을 남겨주세요", style = SopkathonTheme.typography.bodySmall,
                            color = SopkathonTheme.colors.coolGray500, modifier = Modifier.padding(vertical = 24.dp),
                        )
                    }
                }
            }
            HorizontalDivider(color = SopkathonTheme.colors.coolGray50)
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TextField(
                    value = uiState.draft,
                    onValueChange = onDraftChange,
                    enabled = !uiState.isPosting,
                    placeholder = { Text("한마디 남기기...", style = SopkathonTheme.typography.bodySmall) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    maxLines = 4,
                    textStyle = SopkathonTheme.typography.bodySmall,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SopkathonTheme.colors.coolGray50,
                        unfocusedContainerColor = SopkathonTheme.colors.coolGray50,
                        disabledContainerColor = SopkathonTheme.colors.coolGray50,
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        cursorColor = SopkathonTheme.colors.coolGray950,
                    ),
                )
                SheetAction(
                    if (uiState.isPosting) "게시 중" else "게시", onPostClick,
                    Modifier.width(60.dp), primary = true,
                    enabled = uiState.draft.isNotBlank() && !uiState.isPosting,
                )
            }
        }
    SopkathonSnackbarHost(Modifier.align(Alignment.TopCenter))
    }
}

@Composable
private fun PostCommentRow(
    comment: CommentUiModel,
    isTarget: Boolean,
    onAuthorClick: (Long) -> Unit,
) {
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(
            Modifier.size(36.dp).clip(CircleShape).background(SopkathonTheme.colors.coolGray50)
                .noRippleClickable { onAuthorClick(comment.userId) },
            contentAlignment = Alignment.Center,
        ) { Text(comment.name.take(1), style = SopkathonTheme.typography.button) }
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(comment.name, style = SopkathonTheme.typography.button, color = SopkathonTheme.colors.coolGray950,
                    modifier = Modifier.noRippleClickable { onAuthorClick(comment.userId) })
                Text(
                    listOf(comment.position, RelativeTimeFormatter.format(comment.createdAt)).filter { it.isNotBlank() }.joinToString(" · "),
                    style = SopkathonTheme.typography.caption, color = SopkathonTheme.colors.coolGray500,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (isTarget) Text(
                    "코디 주인공", style = SopkathonTheme.typography.caption, color = SopkathonTheme.colors.white,
                    modifier = Modifier.background(SopkathonTheme.colors.coolGray950, RoundedCornerShape(4.dp)).padding(horizontal = 6.dp, vertical = 2.dp),
                )
            }

            Text(comment.content, style = SopkathonTheme.typography.bodySmall, color = SopkathonTheme.colors.coolGray950)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
private fun PostCommentsSheetPreview() {
    SopkathonTheme {
        PostCommentsSheet(
            PostDetailUiState(commentsLoaded = true, comments = listOf(CommentUiModel(1, 1, "박정훈", "과장", "니트 잘 어울리네요!", "2026-11-22T10:00:00+09:00"))),
            {}, {}, {}, {}, {},
        )
    }
}
