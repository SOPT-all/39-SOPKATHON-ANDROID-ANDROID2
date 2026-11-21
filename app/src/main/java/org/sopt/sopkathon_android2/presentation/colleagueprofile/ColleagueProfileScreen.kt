package org.sopt.sopkathon_android2.presentation.colleagueprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.toast
import org.sopt.sopkathon_android2.core.designsystem.component.SopkathonEmptyState
import org.sopt.sopkathon_android2.core.designsystem.component.post.PostGridCard
import org.sopt.sopkathon_android2.core.designsystem.component.profile.ProfileHeader
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.colleagueprofile.component.ColleagueProfileAppBar
import org.sopt.sopkathon_android2.presentation.colleagueprofile.model.ColleagueOutfitUiModel
import org.sopt.sopkathon_android2.presentation.main.navigation.DressUp

// 4 동료 프로필
@Composable
fun ColleagueProfileRoute(
    userId: Long,
    navigateUp: () -> Unit,
    onPostClick: (Long) -> Unit,
    onCoordiClick: (DressUp) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ColleagueProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // 게시물 상세 등에서 돌아왔을 때 바로 반영되게 화면이 보일 때마다 다시 조회
    LifecycleResumeEffect(userId) {
        viewModel.getProfile(userId)
        onPauseOrDispose { }
    }
    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ColleagueProfileUiEffect.ShowToast -> context.toast(effect.message)
            }
        }
    }

    // TODO 더보기는 기능 정해지면 연결
    ColleagueProfileScreen(
        uiState = uiState,
        navigateUp = navigateUp,
        onMoreClick = {},
        onPostClick = onPostClick,
        onCoordiClick = {
            onCoordiClick(
                DressUp(
                    colleagueId = userId,
                    name = uiState.name,
                    position = uiState.rank,
                    profileImageUrl = uiState.profileImageUrl.ifBlank { null },
                ),
            )
        },
        modifier = modifier,
    )
}

@Composable
private fun ColleagueProfileScreen(
    uiState: ColleagueProfileUiState,
    navigateUp: () -> Unit,
    onMoreClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    onCoordiClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white),
    ) {
        ColleagueProfileAppBar(onBackClick = navigateUp, onMoreClick = onMoreClick)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ProfileHeader(
                    name = uiState.name,
                    rank = uiState.rank,
                    department = uiState.department,
                    profileImageUrl = uiState.profileImageUrl,
                    modifier = Modifier.padding(bottom = 24.dp),
                )
            }
            // 불러오기 전 "님 코디"만 보이지 않게 이름 받은 뒤 노출
            if (uiState.name.isBlank()) return@LazyVerticalGrid
            outfitSection(
                title = "${uiState.firstName}님 코디",
                outfits = uiState.receivedOutfits,
                onPostClick = onPostClick,
                emptyTitle = "아직 받은 코디가 없어요",
                emptyDescription = "${uiState.firstName}님에게 첫 코디를 골라줘 보세요",
                emptyActionText = "코디 만들기",
                onEmptyActionClick = onCoordiClick,
            )
            outfitSection(
                title = "${uiState.firstName}님이 만든 코디",
                outfits = uiState.createdOutfits,
                onPostClick = onPostClick,
                emptyTitle = "아직 만든 코디가 없어요",
                emptyDescription = "${uiState.firstName}님이 코디를 만들면 여기에 모여요",
            )
        }
    }
}

private fun LazyGridScope.outfitSection(
    title: String,
    outfits: List<ColleagueOutfitUiModel>,
    onPostClick: (Long) -> Unit,
    emptyTitle: String,
    emptyDescription: String,
    emptyActionText: String? = null,
    onEmptyActionClick: () -> Unit = {},
) {
    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            text = title,
            style = SopkathonTheme.typography.subtitle,
            color = SopkathonTheme.colors.coolGray950,
            modifier = Modifier.padding(bottom = 10.dp),
        )
    }
    if (outfits.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            SopkathonEmptyState(
                iconRes = R.drawable.ic_colleagueprofile_hanger_32,
                title = emptyTitle,
                description = emptyDescription,
                actionText = emptyActionText,
                onActionClick = onEmptyActionClick,
                modifier = Modifier.padding(bottom = 10.dp),
            )
        }
    }
    items(outfits, key = { "$title:${it.postId}" }) { outfit ->
        PostGridCard(
            title = outfit.title,
            imageUrl = outfit.imageUrl,
            likeCount = outfit.likeCount,
            onClick = { onPostClick(outfit.postId) },
            modifier = Modifier.padding(bottom = 10.dp),
        )
    }
}

@ScreenPreview
@Composable
private fun ColleagueProfileScreenPreview() {
    SopkathonTheme {
        ColleagueProfileScreen(
            uiState = ColleagueProfileUiState.dummy,
            navigateUp = {},
            onMoreClick = {},
            onPostClick = {},
            onCoordiClick = {},
        )
    }
}
