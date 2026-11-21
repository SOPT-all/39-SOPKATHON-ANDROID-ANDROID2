package org.sopt.sopkathon_android2.presentation.feed.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun FeedHeader(
    onNotificationClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SopkathonTheme.colors.white)
            .height(56.dp)
            .padding(start = 16.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "부꾸부꾸",
            modifier = Modifier.weight(1f),
            color = SopkathonTheme.colors.coolGray950,
            style = SopkathonTheme.typography.screenTitle,
            maxLines = 1,
        )
        Box(
            modifier = Modifier
                .size(44.dp)
                .noRippleClickable(onClick = onNotificationClick)
                .semantics { role = Role.Button },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_feed_bell_24),
                contentDescription = "알림",
                modifier = Modifier.size(24.dp),
                tint = SopkathonTheme.colors.coolGray950,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 56)
@Composable
private fun FeedHeaderPreview() {
    SopkathonTheme {
        FeedHeader(onNotificationClick = {})
    }
}
