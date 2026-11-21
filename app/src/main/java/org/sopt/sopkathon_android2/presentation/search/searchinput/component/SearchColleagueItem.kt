package org.sopt.sopkathon_android2.presentation.search.searchinput.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun SearchColleagueItem(
    name: String,
    rank: String,
    department: String,
    profileImageUrl: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp)
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 16.dp),
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
                .background(SopkathonTheme.colors.coolGray50),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = name,
                style = SopkathonTheme.typography.itemName,
                color = SopkathonTheme.colors.coolGray950,
            )
            Text(
                text = "$rank · $department",
                style = SopkathonTheme.typography.caption,
                color = SopkathonTheme.colors.coolGray500,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_search_chevronright_24),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchColleagueItemPreview() {
    SopkathonTheme {
        SearchColleagueItem(name = "박정훈", rank = "부장", department = "영업1팀", profileImageUrl = "", onClick = {})
    }
}
