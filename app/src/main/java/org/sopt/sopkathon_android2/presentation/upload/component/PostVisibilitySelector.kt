package org.sopt.sopkathon_android2.presentation.upload.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(SopkathonTheme.colors.coolGray100, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PostVisibility.entries.forEach { visibility ->
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

@Composable
private fun VisibilityOption(
    visibility: PostVisibility,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors
    val selectedModifier = if (isSelected) {
        Modifier
            .shadow(elevation = 1.dp, shape = OptionShape)
            .background(colors.white, OptionShape)
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .then(selectedModifier)
            .clip(OptionShape)
            .noRippleClickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = visibility.label,
            style = SopkathonTheme.typography.button,
            color = if (isSelected) colors.coolGray950 else colors.coolGray500,
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
