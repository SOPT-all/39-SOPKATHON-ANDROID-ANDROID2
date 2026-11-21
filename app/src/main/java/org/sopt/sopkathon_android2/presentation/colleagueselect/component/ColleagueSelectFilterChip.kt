package org.sopt.sopkathon_android2.presentation.colleagueselect.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
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
fun ColleagueSelectFilterChip(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .heightIn(min = 32.dp)
            .clip(shape)
            .background(if (isSelected) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.white)
            .then(
                if (isSelected) {
                    Modifier
                } else {
                    Modifier.border(width = 1.dp, color = SopkathonTheme.colors.coolGray100, shape = shape)
                },
            )
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SopkathonTheme.typography.button,
            color = if (isSelected) SopkathonTheme.colors.white else SopkathonTheme.colors.coolGray950,
            maxLines = 1,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun ColleagueSelectFilterChipPreview() {
    SopkathonTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            ColleagueSelectFilterChip(
                text = "전체",
                isSelected = true,
                onClick = {},
            )
            ColleagueSelectFilterChip(
                text = "부장",
                isSelected = false,
                onClick = {},
            )
            ColleagueSelectFilterChip(
                text = "차장",
                isSelected = false,
                onClick = {},
            )
        }
    }
}
