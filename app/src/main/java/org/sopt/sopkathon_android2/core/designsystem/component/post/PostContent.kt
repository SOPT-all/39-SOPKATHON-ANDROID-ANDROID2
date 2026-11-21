package org.sopt.sopkathon_android2.core.designsystem.component.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun PostContent(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = title,
            color = SopkathonTheme.colors.coolGray950,
            style = SopkathonTheme.typography.subtitle,
        )
        Text(
            text = content,
            color = SopkathonTheme.colors.coolGray700,
            style = SopkathonTheme.typography.bodySmall,
        )
    }
}

@Preview(showBackground = true, widthDp = 328)
@Composable
private fun PostContentPreview() {
    SopkathonTheme {
        PostContent(
            title = "오늘의 회식룩",
            content = "과장님 평소 정장만 입으셔서 이번엔 네이비 니트에 아이보리 데님으로 캐주얼하게! 금요일 회식룩 추천해요.",
        )
    }
}
