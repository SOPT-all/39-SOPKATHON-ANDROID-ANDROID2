package org.sopt.sopkathon_android2.presentation.upload.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun PostUploadButton(
    text: String,
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(28.dp))
            .background(if (isEnabled) colors.coolGray950 else colors.coolGray150)
            .noRippleClickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SopkathonTheme.typography.buttonLarge,
            color = if (isEnabled) colors.white else colors.coolGray300,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostUploadButtonPreview() {
    SopkathonTheme {
        PostUploadButton(text = "피드에 게시하기", isEnabled = true, onClick = {})
    }
}
