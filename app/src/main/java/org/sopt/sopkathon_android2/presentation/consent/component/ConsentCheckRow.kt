package org.sopt.sopkathon_android2.presentation.consent.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun ConsentCheckRow(
    text: String,
    isChecked: Boolean,
    onCheckClick: () -> Unit,
    onDetailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .noRippleClickable(onClick = onCheckClick)
            .padding(start = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ConsentCheckBox(isChecked = isChecked)

        Text(
            text = text,
            style = SopkathonTheme.typography.itemName,
            color = SopkathonTheme.colors.coolGray950,
            modifier = Modifier.weight(1f),
        )

        Text(
            text = "자세히",
            style = SopkathonTheme.typography.button,
            color = SopkathonTheme.colors.coolGray500,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.noRippleClickable(onClick = onDetailClick),
        )
    }
}

@Composable
private fun ConsentCheckBox(
    isChecked: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isChecked) {
        Image(
            painter = painterResource(R.drawable.ic_consent_check_24),
            contentDescription = null,
            modifier = modifier.size(24.dp),
        )
    } else {
        Box(
            modifier = modifier
                .size(24.dp)
                .clip(RoundedCornerShape(6.dp))
                .background(SopkathonTheme.colors.white)
                .border(
                    width = 1.5.dp,
                    color = SopkathonTheme.colors.coolGray300,
                    shape = RoundedCornerShape(6.dp),
                ),
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ConsentCheckRowPreview() {
    SopkathonTheme {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            ConsentCheckRow(
                text = "(필수) 사진 사용에 동의합니다",
                isChecked = false,
                onCheckClick = {},
                onDetailClick = {},
            )
            ConsentCheckRow(
                text = "(필수) 사진 사용에 동의합니다",
                isChecked = true,
                onCheckClick = {},
                onDetailClick = {},
            )
        }
    }
}
