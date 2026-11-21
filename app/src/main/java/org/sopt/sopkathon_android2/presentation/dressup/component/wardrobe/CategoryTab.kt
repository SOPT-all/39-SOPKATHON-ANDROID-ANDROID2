package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
internal fun CategoryTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val colors = SopkathonTheme.colors
    val shape = RoundedCornerShape(16.dp)

    Box(
        modifier = Modifier
            .height(32.dp)
            .clip(shape)
            .background(if (isSelected) colors.coolGray950 else colors.white)
            .then(if (isSelected) Modifier else Modifier.border(1.dp, colors.coolGray150, shape))
            .noRippleClickable(onClick = onClick)
            .padding(horizontal = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = SopkathonTheme.typography.buttonSmall,
            color = if (isSelected) colors.white else colors.coolGray500,
        )
    }
}
