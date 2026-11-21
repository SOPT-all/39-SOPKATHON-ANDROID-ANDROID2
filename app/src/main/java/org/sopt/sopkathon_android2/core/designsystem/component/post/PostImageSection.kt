package org.sopt.sopkathon_android2.core.designsystem.component.post

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

enum class OutfitItemType {
    TOP,
    BOTTOM,
}

@Composable
fun PostImageSection(
    imageUrl: String,
    hashtag: String?,
    modifier: Modifier = Modifier,
    showItemMarkers: Boolean = false,
    onItemClick: (OutfitItemType) -> Unit = {},
    hasTopItem: Boolean = true,
    hasBottomItem: Boolean = true,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(328f / 340f)
            .clip(RoundedCornerShape(20.dp))
            .background(SopkathonTheme.colors.coolGray50),
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "코디 이미지",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        if (!hashtag.isNullOrBlank()) {
            Text(
                text = "#${hashtag.trim().trimStart('#')}",
                modifier = Modifier
                    .padding(12.dp)
                    .background(SopkathonTheme.colors.white, RoundedCornerShape(percent = 50))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = SopkathonTheme.colors.coolGray950,
                style = SopkathonTheme.typography.button,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        // 328×340 기준 캔버스에서 고정한 마커 중심 위치를 화면 크기에 맞춰 적용
        if (showItemMarkers && hasTopItem) {
            PostItemMarker(
                description = "상의 정보 보기",
                onClick = { onItemClick(OutfitItemType.TOP) },
                modifier = Modifier.offset(
                    x = maxWidth * (142f / 328f) - 14.dp,
                    y = maxHeight * (136f / 340f) - 14.dp,
                ),
            )
        }
        if (showItemMarkers && hasBottomItem) {
            PostItemMarker(
                description = "하의 정보 보기",
                onClick = { onItemClick(OutfitItemType.BOTTOM) },
                modifier = Modifier.offset(
                    x = maxWidth * (180f / 328f) - 14.dp,
                    y = maxHeight * (262f / 340f) - 14.dp,
                ),
            )
        }
    }
}

@Composable
private fun PostItemMarker(
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Image(
        painter = painterResource(R.drawable.ic_feed_marker_item_20),
        contentDescription = description,
        modifier = modifier
            .size(28.dp)
            .noRippleClickable(onClick = onClick)
            .semantics { role = Role.Button },
    )
}

@Preview(name = "피드 · 마커 없음", showBackground = true, widthDp = 328)
@Composable
private fun PostImageSectionFeedPreview() {
    SopkathonTheme {
        PostImageSection(
            imageUrl = "",
            hashtag = "금요일회식룩",
        )
    }
}

@Preview(name = "상세 · 상의와 하의 마커", showBackground = true, widthDp = 328)
@Composable
private fun PostImageSectionDetailPreview() {
    SopkathonTheme {
        PostImageSection(
            imageUrl = "",
            hashtag = "금요일회식룩",
            showItemMarkers = true,
            onItemClick = {},
        )
    }
}
