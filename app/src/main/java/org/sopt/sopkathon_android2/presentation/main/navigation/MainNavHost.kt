package org.sopt.sopkathon_android2.presentation.main.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import org.sopt.sopkathon_android2.presentation.colleagueprofile.ColleagueProfileRoute
import org.sopt.sopkathon_android2.presentation.colleagueselect.ColleagueSelectRoute
import org.sopt.sopkathon_android2.presentation.consent.ConsentRoute
import org.sopt.sopkathon_android2.presentation.dressup.DressUpRoute
import org.sopt.sopkathon_android2.presentation.feed.FeedRoute
import org.sopt.sopkathon_android2.presentation.myprofile.MyProfileRoute
import org.sopt.sopkathon_android2.presentation.postdetail.PostDetailRoute
import org.sopt.sopkathon_android2.presentation.profilecreate.ProfileCreateRoute
import org.sopt.sopkathon_android2.presentation.search.SearchRoute
import org.sopt.sopkathon_android2.presentation.upload.PostUploadRoute

@Composable
fun MainNavHost(
    navigator: MainNavigator,
    paddingValues: PaddingValues,
    modifier: Modifier = Modifier,
) {
    val navController = navigator.navController
    val screenModifier = Modifier.padding(paddingValues)

    NavHost(
        navController = navController,
        startDestination = navigator.startDestination,
        modifier = modifier
            .fillMaxSize()
            .consumeWindowInsets(paddingValues), // 키보드 올라올 때 하단 패딩 소비
    ) {
        // 0.2 → 0.3 (01), 미동의 시 피드 둘러보기 (B1)
        composable<Consent> {
            ConsentRoute(
                onAgreeClick = navController::navigateToProfileCreate,
                onSkipClick = navController::navigateToFeedFromOnboarding,
                modifier = screenModifier,
            )
        }

        // 0.3 → 1 (02)
        composable<ProfileCreate> {
            ProfileCreateRoute(
                navigateUp = navController::navigateUp,
                onSaveClick = navController::navigateToFeedFromOnboarding,
                modifier = screenModifier,
            )
        }

        // 1 → 동료 프로필 (B2), 게시물 상세
        composable<Feed> {
            FeedRoute(
                onColleagueClick = navController::navigateToColleagueProfile,
                onPostClick = navController::navigateToPostDetail,
                modifier = screenModifier,
            )
        }

        // 6 → 동료 프로필 / 게시물 상세 (B3, B5)
        composable<Search> {
            SearchRoute(
                onColleagueClick = navController::navigateToColleagueProfile,
                onPostClick = navController::navigateToPostDetail,
                modifier = screenModifier,
            )
        }

        // 2.1 → 2.2 (05)
        composable<ColleagueSelect> {
            ColleagueSelectRoute(
                onNextClick = navController::navigateToDressUp,
                modifier = screenModifier,
            )
        }

        // 2.2 → 2.3 (06)
        composable<DressUp> { backStackEntry ->
            val dressUp = backStackEntry.toRoute<DressUp>()
            DressUpRoute(
                colleagueId = dressUp.colleagueId,
                navigateUp = navController::navigateUp,
                onCompleteClick = { navController.navigateToPostUpload(dressUp.colleagueId) },
                modifier = screenModifier,
            )
        }

        // 2.3 → 1 (07)
        composable<PostUpload> { backStackEntry ->
            val postUpload = backStackEntry.toRoute<PostUpload>()
            PostUploadRoute(
                colleagueId = postUpload.colleagueId,
                navigateUp = navController::navigateUp,
                onUploadClick = navController::navigateToFeedAfterUpload,
                modifier = screenModifier,
            )
        }

        composable<MyProfile> {
            MyProfileRoute(
                modifier = screenModifier,
            )
        }

        // 4 → 5 (08)
        composable<ColleagueProfile> { backStackEntry ->
            val colleagueProfile = backStackEntry.toRoute<ColleagueProfile>()
            ColleagueProfileRoute(
                userId = colleagueProfile.userId,
                navigateUp = navController::navigateUp,
                onPostClick = navController::navigateToPostDetail,
                modifier = screenModifier,
            )
        }

        // 5 → 동료 프로필, '나도 골라주기' → 2.1 (04), 댓글 바텀시트는 화면 안에서 처리 (09)
        composable<PostDetail> { backStackEntry ->
            val postDetail = backStackEntry.toRoute<PostDetail>()
            PostDetailRoute(
                postId = postDetail.postId,
                navigateUp = navController::navigateUp,
                onColleagueClick = navController::navigateToColleagueProfile,
                onDressUpClick = navController::navigateToColleagueSelect,
                modifier = screenModifier,
            )
        }
    }
}
