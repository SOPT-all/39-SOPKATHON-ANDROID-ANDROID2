package org.sopt.sopkathon_android2.presentation.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun FeedTeamLookBanner(
    title: String,
    deadlineLabel: String,
    participationText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val secondaryColor = SopkathonTheme.colors.coolGray500

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SopkathonTheme.colors.coolGray50, RoundedCornerShape(16.dp))
            .noRippleClickable(onClick = onClick)
            .semantics(mergeDescendants = true) { role = Role.Button }
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "이번 주 TEAM LOOK",
                    color = secondaryColor,
                    style = SopkathonTheme.typography.buttonSmall,
                )
                Text(
                    text = deadlineLabel,
                    modifier = Modifier
                        .background(SopkathonTheme.colors.coolGray950, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 1.dp),
                    color = SopkathonTheme.colors.white,
                    style = SopkathonTheme.typography.buttonSmall,
                    maxLines = 1,
                )
            }
            Text(
                text = title,
                color = SopkathonTheme.colors.coolGray950,
                style = SopkathonTheme.typography.buttonLarge,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = participationText,
                color = secondaryColor,
                style = SopkathonTheme.typography.caption,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_feed_chevronright_20),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = secondaryColor,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun FeedTeamLookBannerPreview() {
    SopkathonTheme {
        FeedTeamLookBanner(
            title = "금요일 회식룩을 골라 주세요",
            deadlineLabel = "D-2",
            participationText = "마케팅팀 12명 중 5명 참여",
            onClick = {},
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        )
    }
}
