package org.sopt.sopkathon_android2.core.designsystem.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

// 피그마 state_empty: 72 원형 아이콘 + 제목 + 설명 + 선택 버튼
@Composable
fun SopkathonEmptyState(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    actionText: String? = null,
    onActionClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SopkathonTheme.colors.coolGray50),
            contentAlignment = Alignment.Center,
        ) {
            Image(painterResource(iconRes), contentDescription = null)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                style = SopkathonTheme.typography.subtitle,
                color = SopkathonTheme.colors.coolGray950,
                textAlign = TextAlign.Center,
            )
            Text(
                text = description,
                style = SopkathonTheme.typography.bodySmall,
                color = SopkathonTheme.colors.coolGray500,
                textAlign = TextAlign.Center,
            )
        }
        if (actionText != null) {
            Box(
                modifier = Modifier
                    .height(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SopkathonTheme.colors.coolGray50)
                    .noRippleClickable(onClick = onActionClick)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = actionText,
                    style = SopkathonTheme.typography.button,
                    color = SopkathonTheme.colors.coolGray950,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SopkathonEmptyStatePreview() {
    SopkathonTheme {
        SopkathonEmptyState(
            iconRes = R.drawable.ic_myprofile_hanger_32,
            title = "아직 만든 코디가 없어요",
            description = "동료에게 첫 코디를 골라줘 보세요",
            actionText = "코디 만들기",
        )
    }
}
