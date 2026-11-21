package org.sopt.sopkathon_android2.presentation.dressup.component

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

/**
 * 옷 입히기 화면의 검정 채움 버튼 (코디 완성, 저장하기). 높이 56, 모서리 28
 */
@Composable
fun DressUpFilledButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    val colors = SopkathonTheme.colors

    Box(
        modifier = modifier
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
private fun DressUpFilledButtonPreview() {
    SopkathonTheme {
        DressUpFilledButton(text = "저장하기", onClick = {}, modifier = Modifier.fillMaxWidth())
    }
}
