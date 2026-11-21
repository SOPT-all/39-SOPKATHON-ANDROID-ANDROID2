package org.sopt.sopkathon_android2.presentation.dressup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

private val ChipShape = RoundedCornerShape(16.dp)

/**
 * 선택한 스티커 아래 칩. 정보를 저장한 옷이면 "아이템 정보 수정"
 */
@Composable
fun StickerActionChip(
    isEdit: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors

    Row(
        modifier = modifier
            .height(30.dp)
            .shadow(elevation = 6.dp, shape = ChipShape, ambientColor = colors.coolGray950, spotColor = colors.coolGray950)
            .clip(ChipShape)
            .background(colors.coolGray950)
            .noRippleClickable(onClick = onClick)
            .padding(start = 12.dp, end = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_dressup_edit_16),
            contentDescription = null,
            tint = Color.Unspecified,
        )
        Text(
            text = if (isEdit) "아이템 정보 수정" else "아이템 정보 입력",
            style = SopkathonTheme.typography.buttonSmall,
            color = colors.white,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StickerActionChipPreview() {
    SopkathonTheme {
        StickerActionChip(isEdit = false, onClick = {}, modifier = Modifier.padding(16.dp))
    }
}
