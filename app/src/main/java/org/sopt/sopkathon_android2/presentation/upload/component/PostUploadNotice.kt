package org.sopt.sopkathon_android2.presentation.upload.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 정보를 비운 옷이 있을 때 보이는 안내
 *
 * @param categoryLabels 비운 옷의 카테고리 ("상의", "하의")
 */
@Composable
fun PostUploadNotice(
    categoryLabels: List<String>,
    modifier: Modifier = Modifier,
) {
    Text(
        text = "${categoryLabels.joinToString(", ")} 정보가 아직 비어 있어요. 그대로 게시해도 괜찮아요.",
        modifier = modifier
            .fillMaxWidth()
            .background(SopkathonTheme.colors.amber50, RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        style = SopkathonTheme.typography.caption,
        color = SopkathonTheme.colors.amber700,
    )
}

@Preview(showBackground = true)
@Composable
private fun PostUploadNoticePreview() {
    SopkathonTheme {
        PostUploadNotice(categoryLabels = listOf("하의"))
    }
}
