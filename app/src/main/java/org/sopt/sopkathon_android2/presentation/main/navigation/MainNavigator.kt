package org.sopt.sopkathon_android2.presentation.main.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController

// 온보딩(동의·프로필 생성)을 백스택에서 지우고 피드로
fun NavController.navigateToFeedFromOnboarding() {
    navigate(Feed) {
        popUpTo<Consent> { inclusive = true }
    }
}

// 온보딩(동의·프로필 생성)을 백스택에서 지우고 가입 완료로
fun NavController.navigateToSignUpComplete(signUpComplete: SignUpComplete) {
    navigate(signUpComplete) {
        popUpTo<Consent> { inclusive = true }
    }
}

// 가입 완료 화면을 지우고 피드로
fun NavController.navigateToFeedFromSignUpComplete() {
    navigate(Feed) {
        popUpTo<SignUpComplete> { inclusive = true }
    }
}

fun NavController.navigateToProfileCreate() {
    navigate(ProfileCreate)
}

fun NavController.navigateToColleagueSelect() {
    navigate(ColleagueSelect) {
        launchSingleTop = true
    }
}

fun NavController.navigateToDressUp(colleagueId: Long) {
    navigate(DressUp(colleagueId))
}

fun NavController.navigateToPostUpload(colleagueId: Long) {
    navigate(PostUpload(colleagueId))
}

// 게시 후 코디 만들기 흐름을 지우고 피드로
fun NavController.navigateToFeedAfterUpload() {
    navigate(Feed) {
        popUpTo<Feed> { inclusive = false }
        launchSingleTop = true
    }
}

fun NavController.navigateToColleagueProfile(userId: Long) {
    navigate(ColleagueProfile(userId))
}

fun NavController.navigateToSearchInput() {
    navigate(SearchInput)
}

fun NavController.navigateToPostDetail(postId: Long) {
    navigate(PostDetail(postId))
}

class MainNavigator(
    val navController: NavHostController,
) {
    val startDestination = Consent

    @Composable
    fun currentDestination(): NavDestination? {
        val backStackEntry by navController.currentBackStackEntryAsState()
        return backStackEntry?.destination
    }

    @Composable
    fun currentTab(): MainTab? {
        val destination = currentDestination()
        return MainTab.entries.find { tab -> destination?.hasRoute(tab.route::class) == true }
    }

    // 피그마 기준 하단 탭이 보이는 화면: 탭 4개 + 동료 프로필 + 게시물 상세
    @Composable
    fun shouldShowBottomBar(): Boolean {
        val destination = currentDestination() ?: return false
        if (currentTab() != null) return true
        return destination.hasRoute(ColleagueProfile::class) || destination.hasRoute(PostDetail::class)
    }

    fun navigateToTab(tab: MainTab) {
        navController.navigate(tab.route) {
            // 피드가 항상 백스택 맨 아래라 피드 기준으로 정리
            popUpTo<Feed> { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }
}

@Composable
fun rememberMainNavigator(
    navController: NavHostController = rememberNavController(),
): MainNavigator = remember(navController) {
    MainNavigator(navController)
}
