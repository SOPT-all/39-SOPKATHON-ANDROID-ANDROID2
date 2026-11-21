package org.sopt.sopkathon_android2.presentation.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.main.navigation.MainTab

// 피그마 nav_floating
@Composable
fun MainBottomBar(
    currentTab: MainTab?,
    onTabClick: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(start = 12.dp, end = 12.dp, bottom = 4.dp)
            .dropShadow(
                shape = RoundedCornerShape(32.dp),
                shadow = Shadow(radius = 10.dp, color = Color.Black.copy(alpha = 0.1f), offset = DpOffset(0.dp, 6.dp)),
            )
            .background(SopkathonTheme.colors.white, RoundedCornerShape(32.dp))
            .border(1.dp, SopkathonTheme.colors.white, RoundedCornerShape(32.dp))
            .height(64.dp)
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { tab ->
            val isSelected = tab == currentTab
            Column(
                modifier = Modifier
                    .size(width = 72.dp, height = 56.dp)
                    .noRippleClickable { onTabClick(tab) },
                verticalArrangement = Arrangement.spacedBy(2.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Image(
                    painter = painterResource(if (isSelected) tab.selectedIconRes else tab.iconRes),
                    contentDescription = null,
                )
                Text(
                    text = stringResource(tab.labelRes),
                    style = if (isSelected) SopkathonTheme.typography.buttonSmall else SopkathonTheme.typography.caption,
                    color = if (isSelected) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray300,
                    maxLines = 1,
                )
            }
        }
    }
}

@ScreenPreview
@Composable
private fun MainBottomBarPreview() {
    SopkathonTheme {
        MainBottomBar(
            currentTab = MainTab.FEED,
            onTabClick = {},
        )
    }
}
