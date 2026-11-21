package org.sopt.sopkathon_android2.presentation.colleagueselect.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
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
fun ColleagueSelectPersonCard(
    name: String,
    position: String,
    team: String,
    coordiCount: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(SopkathonTheme.colors.white)
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) SopkathonTheme.colors.coolGray300 else SopkathonTheme.colors.coolGray100,
                shape = shape,
            )
            .clickable(onClick = onClick)
            .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 10.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // 미니룸 배경 · 캐릭터 이미지는 API 연결 시 추가
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(144f / 150f)
                .clip(RoundedCornerShape(8.dp))
                .background(SopkathonTheme.colors.coolGray50),
        ) {
            CoordiCountBadge(
                count = coordiCount,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 8.dp, top = 8.dp),
            )
            SelectionIndicator(
                isSelected = isSelected,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 6.dp),
            )
        }

        Column(
            modifier = Modifier.padding(start = 4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = name,
                style = SopkathonTheme.typography.itemName,
                color = SopkathonTheme.colors.coolGray950,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = "$position · $team",
                style = SopkathonTheme.typography.caption,
                color = SopkathonTheme.colors.coolGray500,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun CoordiCountBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .heightIn(min = 20.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(SopkathonTheme.colors.white)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "코디 $count",
            style = SopkathonTheme.typography.buttonSmall,
            color = SopkathonTheme.colors.coolGray950,
            maxLines = 1,
        )
    }
}

@Composable
private fun SelectionIndicator(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isSelected) {
        Box(
            modifier = modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SopkathonTheme.colors.coolGray950),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_colleagueselect_check_16),
                contentDescription = null,
                tint = SopkathonTheme.colors.white,
                modifier = Modifier.size(16.dp),
            )
        }
    } else {
        Box(
            modifier = modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(SopkathonTheme.colors.white)
                .border(width = 1.5.dp, color = SopkathonTheme.colors.coolGray300, shape = CircleShape),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ColleagueSelectPersonCardPreview() {
    SopkathonTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ColleagueSelectPersonCard(
                name = "박정훈",
                position = "부장",
                team = "영업1팀",
                coordiCount = 12,
                isSelected = true,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
            ColleagueSelectPersonCard(
                name = "이준호",
                position = "사원",
                team = "영업1팀",
                coordiCount = 3,
                isSelected = false,
                onClick = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
