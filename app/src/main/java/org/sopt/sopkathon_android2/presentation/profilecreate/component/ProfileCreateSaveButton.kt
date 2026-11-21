package org.sopt.sopkathon_android2.presentation.profilecreate.component

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

// 공통 버튼(#12) 머지되면 교체
@Composable
fun ProfileCreateSaveButton(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (enabled) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray150)
            .noRippleClickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SopkathonTheme.typography.buttonLarge,
            color = if (enabled) SopkathonTheme.colors.white else SopkathonTheme.colors.coolGray300,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileCreateSaveButtonPreview() {
    SopkathonTheme {
        ProfileCreateSaveButton(text = "프로필 저장", enabled = true, onClick = {})
    }
}
