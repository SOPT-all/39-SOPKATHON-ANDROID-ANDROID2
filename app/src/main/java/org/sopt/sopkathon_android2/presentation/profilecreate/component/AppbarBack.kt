package org.sopt.sopkathon_android2.presentation.profilecreate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 뒤로가기 버튼 + 제목 상단바
 */
@Composable
fun AppbarBack(
    title: String,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(SopkathonTheme.colors.white)
            .padding(start = 4.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_profilecreate_back_24),
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .noRippleClickable(onClick = navigateUp)
                .padding(12.dp), // 터치 영역 48, 아이콘 24
            tint = Color.Unspecified,
        )

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = SopkathonTheme.typography.itemName,
            color = SopkathonTheme.colors.coolGray950,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AppbarBackPreview() {
    SopkathonTheme {
        AppbarBack(
            title = "프로필 만들기",
            navigateUp = {},
        )
    }
}
