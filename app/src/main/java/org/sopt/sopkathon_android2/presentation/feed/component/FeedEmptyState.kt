package org.sopt.sopkathon_android2.presentation.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun FeedEmptyState(
    onCreateOutfitClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(
            modifier = Modifier.size(72.dp).background(SopkathonTheme.colors.coolGray50, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text("👔", style = SopkathonTheme.typography.display)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            "아직 올라온 코디가 없어요",
            style = SopkathonTheme.typography.subtitle,
            color = SopkathonTheme.colors.coolGray950,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "우리 부서 첫 코디를 올려 대화를 시작해 보세요",
            style = SopkathonTheme.typography.bodySmall,
            color = SopkathonTheme.colors.coolGray500,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
        Spacer(Modifier.height(28.dp))
        Text(
            "첫 코디 만들기",
            modifier = Modifier
                .background(SopkathonTheme.colors.coolGray950, RoundedCornerShape(50))
                .noRippleClickable(onClick = onCreateOutfitClick)
                .padding(horizontal = 24.dp, vertical = 14.dp),
            style = SopkathonTheme.typography.buttonLarge,
            color = SopkathonTheme.colors.white,
        )
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 360)
@Composable
private fun FeedEmptyStatePreview() {
    SopkathonTheme {
        FeedEmptyState({}, Modifier.size(360.dp))
    }
}
