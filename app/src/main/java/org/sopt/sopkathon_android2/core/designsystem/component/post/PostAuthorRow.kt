package org.sopt.sopkathon_android2.core.designsystem.component.post

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.common.util.RelativeTimeFormatter
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import java.util.TimeZone

@Composable
fun PostAuthorRow(
    author: PostAuthorUiModel,
    targetLabel: String,
    scopeLabel: String?,
    createdAt: String,
    onAuthorClick: () -> Unit,
    onMoreClick: () -> Unit,
    modifier: Modifier = Modifier,
    nowMillis: Long = System.currentTimeMillis(),
    serverTimeZone: TimeZone? = null,
) {
    val timeLabel = RelativeTimeFormatter.format(
        createdAt = createdAt,
        nowMillis = nowMillis,
        serverTimeZone = serverTimeZone,
    )

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SopkathonTheme.colors.coolGray50)
                .noRippleClickable(onClick = onAuthorClick),
            contentAlignment = Alignment.Center,
        ) {
            if (author.profileImageUrl.isNullOrBlank()) {
                Text(
                    text = author.name.take(1),
                    style = SopkathonTheme.typography.subtitle,
                )
            } else {
                AsyncImage(
                    model = author.profileImageUrl,
                    contentDescription = "${author.name} 프로필",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = author.name,
                    modifier = Modifier.noRippleClickable(onClick = onAuthorClick),
                    color = SopkathonTheme.colors.coolGray950,
                    style = SopkathonTheme.typography.button,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = listOfNotNull(author.position, author.department)
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    color = SopkathonTheme.colors.coolGray500,
                    style = SopkathonTheme.typography.caption,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                PostMetaChip(
                    label = targetLabel,
                    modifier = Modifier.weight(1f, fill = false),
                )
                if (!scopeLabel.isNullOrBlank()) {
                    PostMetaChip(label = scopeLabel)
                }
                Text(
                    text = timeLabel,
                    color = SopkathonTheme.colors.coolGray500,
                    style = SopkathonTheme.typography.caption,
                    maxLines = 1,
                )
            }
        }

        PostIconButton(
            icon = R.drawable.ic_feed_more_24,
            description = "게시글 더보기",
            onClick = onMoreClick,
            tint = SopkathonTheme.colors.coolGray500,
        )
    }
}

@Composable
private fun PostMetaChip(
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier
            .background(SopkathonTheme.colors.coolGray50, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp),
        color = SopkathonTheme.colors.coolGray950,
        style = SopkathonTheme.typography.buttonSmall,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Preview(showBackground = true, widthDp = 328)
@Composable
private fun PostAuthorRowPreview() {
    SopkathonTheme {
        PostAuthorRow(
            author = PostAuthorUiModel(
                userId = 202L,
                name = "김민지",
                position = "대리",
                department = "마케팅팀",
                profileImageUrl = null,
            ),
            targetLabel = "박정훈 과장님 코디",
            scopeLabel = "우리 부서",
            createdAt = "2023-11-14T22:13:00Z",
            onAuthorClick = {},
            onMoreClick = {},
            nowMillis = 1_700_000_000_000L,
        )
    }
}
