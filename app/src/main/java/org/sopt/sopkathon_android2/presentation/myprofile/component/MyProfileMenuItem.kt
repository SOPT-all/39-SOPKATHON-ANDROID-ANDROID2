package org.sopt.sopkathon_android2.presentation.myprofile.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun MyProfileMenuItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = SopkathonTheme.typography.body,
            color = if (isSecondary) SopkathonTheme.colors.coolGray500 else SopkathonTheme.colors.coolGray950,
            modifier = Modifier.weight(1f),
        )
        // 아이콘 추출 전까지 텍스트로 대체
        Text(
            text = "›",
            style = SopkathonTheme.typography.title,
            color = SopkathonTheme.colors.coolGray300,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun MyProfileMenuItemPreview() {
    SopkathonTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
        ) {
            MyProfileMenuItem(
                text = "내가 만든 코디",
                onClick = {},
            )
            MyProfileMenuItem(
                text = "받은 코디",
                onClick = {},
            )
            MyProfileMenuItem(
                text = "사진 동의 철회",
                onClick = {},
                isSecondary = true,
            )
        }
    }
}
