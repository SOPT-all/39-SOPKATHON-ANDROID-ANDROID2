package org.sopt.sopkathon_android2.presentation.consent.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun ConsentInfoItem(
    @DrawableRes iconRes: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(SopkathonTheme.colors.coolGray50),
            contentAlignment = Alignment.Center,
        ) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = title,
                style = SopkathonTheme.typography.itemName,
                color = SopkathonTheme.colors.coolGray950,
            )
            Text(
                text = description,
                style = SopkathonTheme.typography.bodySmall,
                color = SopkathonTheme.colors.coolGray500,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ConsentInfoItemPreview() {
    SopkathonTheme {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            ConsentInfoItem(
                iconRes = R.drawable.ic_consent_photo_24,
                title = "프로필 사진",
                description = "동료 목록과 피드 썸네일에만 보여요.",
            )
            ConsentInfoItem(
                iconRes = R.drawable.ic_consent_hanger_24,
                title = "코디",
                description = "얼굴은 그대로, 옷만 갈아입혀요.",
            )
            ConsentInfoItem(
                iconRes = R.drawable.ic_consent_undo_24,
                title = "동의 철회",
                description = "내 프로필에서 언제든 철회할 수 있어요.",
            )
        }
    }
}
