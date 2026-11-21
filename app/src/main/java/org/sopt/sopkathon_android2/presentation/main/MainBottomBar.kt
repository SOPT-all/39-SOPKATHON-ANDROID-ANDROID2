package org.sopt.sopkathon_android2.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.main.navigation.MainTab

// 아이콘은 아이콘 삽입 작업 때 추가
@Composable
fun MainBottomBar(
    currentTab: MainTab?,
    onTabClick: (MainTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(SopkathonTheme.colors.white)
            .navigationBarsPadding()
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        MainTab.entries.forEach { tab ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .noRippleClickable { onTabClick(tab) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(tab.labelRes),
                    style = SopkathonTheme.typography.tab,
                    color = if (tab == currentTab) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray400,
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
