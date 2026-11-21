package org.sopt.sopkathon_android2.presentation.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import org.sopt.sopkathon_android2.core.designsystem.component.LocalSopkathonSnackbarState
import org.sopt.sopkathon_android2.core.designsystem.component.rememberSopkathonSnackbarState
import org.sopt.sopkathon_android2.presentation.main.navigation.MainNavHost
import org.sopt.sopkathon_android2.presentation.main.navigation.MainNavigator

@Composable
fun MainScreen(
    navigator: MainNavigator,
    modifier: Modifier = Modifier,
) {
    val snackbarState = rememberSopkathonSnackbarState()

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            if (navigator.shouldShowBottomBar()) {
                MainBottomBar(
                    currentTab = navigator.currentTab(),
                    onTabClick = navigator::navigateToTab,
                )
            }
        },
    ) { innerPadding ->
        CompositionLocalProvider(LocalSopkathonSnackbarState provides snackbarState) {
            MainNavHost(
                navigator = navigator,
                paddingValues = innerPadding,
            )
        }
    }
}
