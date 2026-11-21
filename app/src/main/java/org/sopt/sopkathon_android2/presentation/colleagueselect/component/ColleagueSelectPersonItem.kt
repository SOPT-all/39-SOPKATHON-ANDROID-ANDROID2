package org.sopt.sopkathon_android2.presentation.colleagueselect.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun ColleagueSelectPersonItem(
    name: String,
    position: String,
    team: String,
    profileImageUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .background(SopkathonTheme.colors.white)
            .then(
                if (isSelected) {
                    Modifier.border(width = 1.dp, color = SopkathonTheme.colors.coolGray500)
                } else {
                    Modifier
                },
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = profileImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(SopkathonTheme.colors.coolGray150),
        )

        Column(
            modifier = Modifier.weight(1f),
        ) {
            Text(
                text = name,
                style = SopkathonTheme.typography.subtitle,
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

        Box(
            modifier = Modifier.size(40.dp),
            contentAlignment = Alignment.Center,
        ) {
            ColleagueSelectRadio(isSelected = isSelected)
        }
    }
}

@Composable
private fun ColleagueSelectRadio(
    isSelected: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(20.dp)
            .border(
                width = 2.dp,
                color = if (isSelected) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray300,
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(SopkathonTheme.colors.coolGray950),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ColleagueSelectPersonItemPreview() {
    SopkathonTheme {
        Column {
            ColleagueSelectPersonItem(
                name = "박정훈",
                position = "부장",
                team = "영업1팀",
                profileImageUrl = "",
                isSelected = true,
                onClick = {},
            )
            ColleagueSelectPersonItem(
                name = "한지민",
                position = "부장",
                team = "마케팅1팀",
                profileImageUrl = "",
                isSelected = false,
                onClick = {},
            )
        }
    }
}
