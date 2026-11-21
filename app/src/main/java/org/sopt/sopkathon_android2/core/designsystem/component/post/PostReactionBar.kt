package org.sopt.sopkathon_android2.core.designsystem.component.post

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun PostReactionBar(
    likeCount: Int,
    commentCount: Int,
    isLiked: Boolean,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .noRippleClickable(onClick = onLikeClick)
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    contentDescription = "좋아요 ${likeCount}개"
                    stateDescription = if (isLiked) "좋아요 선택됨" else "좋아요 선택 안 됨"
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_feed_heart_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = SopkathonTheme.colors.coolGray950,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = likeCount.toString(),
                style = SopkathonTheme.typography.body,
                color = SopkathonTheme.colors.coolGray950,
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Row(
            modifier = Modifier
                .noRippleClickable(onClick = onCommentClick)
                .semantics(mergeDescendants = true) {
                    role = Role.Button
                    contentDescription = "댓글 ${commentCount}개"
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_feed_coment_24),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = SopkathonTheme.colors.coolGray950,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = commentCount.toString(),
                style = SopkathonTheme.typography.body,
                color = SopkathonTheme.colors.coolGray950,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PostIconButton(
            icon = R.drawable.ic_feed_share_24,
            description = "게시글 공유",
            onClick = onShareClick,
        )
    }
}

@Composable
internal fun PostIconButton(
    @DrawableRes icon: Int,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = SopkathonTheme.colors.coolGray950,
) {
    Box(
        modifier = modifier
            .size(40.dp)
            .noRippleClickable(onClick = onClick)
            .semantics { role = Role.Button },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = description,
            modifier = Modifier.size(24.dp),
            tint = tint,
        )
    }
}

@Preview(showBackground = true, widthDp = 328)
@Composable
private fun PostReactionBarPreview() {
    SopkathonTheme {
        PostReactionBar(
            likeCount = 18,
            commentCount = 3,
            isLiked = false,
            onLikeClick = {},
            onCommentClick = {},
            onShareClick = {},
        )
    }
}
