package org.sopt.sopkathon_android2.presentation.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.sopt.sopkathon_android2.presentation.home.HomeRoute

@Composable
fun MainNavHost(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navigator.navController,
        startDestination = Home,
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(paddingValues), // 키보드 올라올 때 하단 패딩 소비
    ) {
        composable<Home> {
            HomeRoute(
                modifier = Modifier.padding(paddingValues),
            )
        }

        // 예시
        // composable<Detail> { backStackEntry ->
        //     val detail = backStackEntry.toRoute<Detail>()
        //     DetailRoute(
        //         id = detail.id,
        //         navigateUp = navigator.navController::navigateUp,
        //         modifier = Modifier.padding(paddingValues),
        //     )
        // }
    }
}
