package org.sopt.sopkathon_android2.presentation.upload.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.upload.model.PostUploadItemUiModel

/**
 * 캐릭터 썸네일과 코디 대상, 입힌 아이템 목록
 *
 * @param imageUri 캐릭터 캡처 PNG
 */
@Composable
fun PostUploadOutfitSummary(
    imageUri: String,
    colleagueName: String,
    colleaguePosition: String,
    items: List<PostUploadItemUiModel>,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors
    val typography = SopkathonTheme.typography

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(width = 120.dp, height = 160.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(colors.coolGray50),
        ) {
            AsyncImage(
                model = imageUri,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp, bottom = 4.dp),
                contentScale = ContentScale.Fit,
            )
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "$colleagueName ${colleaguePosition}님 코디",
                modifier = Modifier
                    .background(colors.coolGray50, RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp),
                style = typography.buttonSmall,
                color = colors.coolGray950,
            )
            Text(
                text = "아이템 ${items.size}개",
                style = typography.button,
                color = colors.coolGray950,
            )
            items.forEach { item ->
                OutfitItemRow(item = item)
            }
        }
    }
}

@Composable
private fun OutfitItemRow(
    item: PostUploadItemUiModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(
                    color = if (item.isInfoComplete) SopkathonTheme.colors.green600 else SopkathonTheme.colors.pink500,
                    shape = CircleShape,
                ),
        )
        Text(
            text = item.name,
            style = SopkathonTheme.typography.caption,
            color = SopkathonTheme.colors.coolGray500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostUploadOutfitSummaryPreview() {
    SopkathonTheme {
        PostUploadOutfitSummary(
            imageUri = "",
            colleagueName = "박정훈",
            colleaguePosition = "과장",
            items = listOf(
                PostUploadItemUiModel(clothesId = 3L, name = "네이비 크루넥 니트", categoryLabel = "상의", isInfoComplete = true),
                PostUploadItemUiModel(clothesId = 4L, name = "아이보리 스트레이트 데님", categoryLabel = "하의", isInfoComplete = false),
            ),
            modifier = Modifier.padding(16.dp),
        )
    }
}
