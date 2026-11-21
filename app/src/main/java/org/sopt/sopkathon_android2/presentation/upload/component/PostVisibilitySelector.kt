package org.sopt.sopkathon_android2.presentation.upload.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.upload.model.PostVisibility

private val OptionShape = RoundedCornerShape(10.dp)
private val OptionGap = 4.dp

@Composable
fun PostVisibilitySelector(
    selected: PostVisibility,
    onSelect: (PostVisibility) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "공개 범위",
            style = SopkathonTheme.typography.button,
            color = SopkathonTheme.colors.coolGray950,
        )

        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .background(SopkathonTheme.colors.coolGray100, RoundedCornerShape(14.dp))
                .padding(4.dp),
        ) {
            val options = PostVisibility.entries
            val optionWidth = (maxWidth - OptionGap * (options.size - 1)) / options.size
            val indicatorOffset by animateDpAsState(
                targetValue = (optionWidth + OptionGap) * options.indexOf(selected),
                label = "VisibilityIndicatorOffset",
            )

            // 흰 선택 면을 옵션마다 그리지 않고 하나만 옮겨서 고를 때 옆으로 미끄러짐
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .wrapContentWidth(Alignment.Start)
                    .offset(x = indicatorOffset)
                    .width(optionWidth)
                    .shadow(elevation = 1.dp, shape = OptionShape)
                    .background(SopkathonTheme.colors.white, OptionShape),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(OptionGap),
            ) {
                options.forEach { visibility ->
                    VisibilityOption(
                        visibility = visibility,
                        isSelected = visibility == selected,
                        onClick = { onSelect(visibility) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun VisibilityOption(
    visibility: PostVisibility,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors
    val labelColor by animateColorAsState(
        targetValue = if (isSelected) colors.coolGray950 else colors.coolGray500,
        label = "VisibilityLabelColor",
    )

    Column(
        modifier = modifier
            .clip(OptionShape)
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = visibility.label,
            style = SopkathonTheme.typography.button,
            color = labelColor,
        )
        Text(
            text = visibility.description,
            style = SopkathonTheme.typography.caption,
            color = colors.coolGray500,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PostVisibilitySelectorPreview() {
    SopkathonTheme {
        PostVisibilitySelector(
            selected = PostVisibility.DEPARTMENT,
            onSelect = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
