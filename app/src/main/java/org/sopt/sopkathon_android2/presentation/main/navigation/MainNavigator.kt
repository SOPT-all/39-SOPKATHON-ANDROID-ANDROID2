package org.sopt.sopkathon_android2.presentation.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

fun NavController.navigateToHome() {
    navigate(Home)
}

// 예시
// fun NavController.navigateToDetail(id: Long) {
//     navigate(Detail(id))
// }

class MainNavigator(
    val navController: NavHostController,
)

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
