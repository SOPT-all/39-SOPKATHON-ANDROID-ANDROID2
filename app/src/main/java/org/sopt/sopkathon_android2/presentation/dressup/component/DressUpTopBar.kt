package org.sopt.sopkathon_android2.presentation.dressup.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun DressUpTopBar(
    colleagueName: String,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_dressup_back_24),
            contentDescription = "뒤로 가기",
            tint = Color.Unspecified,
            modifier = Modifier.noRippleClickable(onClick = onBackClick),
        )
        Text(
            text = "${colleagueName}님 옷 입히기",
            style = SopkathonTheme.typography.buttonLarge,
            color = SopkathonTheme.colors.coolGray950,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun DressUpTopBarPreview() {
    SopkathonTheme {
        DressUpTopBar(colleagueName = "박정훈 과장", onBackClick = {})
    }
}
