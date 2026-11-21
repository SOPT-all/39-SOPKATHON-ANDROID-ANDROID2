package org.sopt.sopkathon_android2.presentation.myprofile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun MyProfileHeader(
    name: String,
    rank: String,
    department: String,
    profileImageUrl: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AsyncImage(
            model = profileImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(SopkathonTheme.colors.coolGray100),
        )
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = name,
                style = SopkathonTheme.typography.headline,
                color = SopkathonTheme.colors.coolGray950,
                maxLines = 1,
            )
            Text(
                text = listOf(rank, department).filter(String::isNotBlank).joinToString(" · "),
                style = SopkathonTheme.typography.bodySmall,
                color = SopkathonTheme.colors.coolGray500,
                maxLines = 1,
            )
        }
    }
}
