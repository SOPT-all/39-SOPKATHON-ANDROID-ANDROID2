package org.sopt.sopkathon_android2.presentation.myprofile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.toast
import org.sopt.sopkathon_android2.core.designsystem.component.SopkathonEmptyState
import org.sopt.sopkathon_android2.core.designsystem.component.post.PostGridCard
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.myprofile.component.MyProfileAppBar
import org.sopt.sopkathon_android2.presentation.myprofile.component.MyProfileEditButton
import org.sopt.sopkathon_android2.core.designsystem.component.profile.ProfileHeader
import org.sopt.sopkathon_android2.presentation.myprofile.model.MyProfileOutfitUiModel

// 3 내 프로필
@Composable
fun MyProfileRoute(
    onCoordiClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MyProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is MyProfileUiEffect.ShowToast -> context.toast(effect.message)
            }
        }
    }

    // TODO 더보기, 프로필 수정, 게시물 상세 이동은 기능 정해지면 연결
    MyProfileScreen(
        uiState = uiState,
        onMoreClick = {},
        onEditClick = {},
        onPostClick = {},
        onCoordiClick = onCoordiClick,
        modifier = modifier,
    )
}

@Composable
private fun MyProfileScreen(
    uiState: MyProfileUiState,
    onMoreClick: () -> Unit,
    onEditClick: () -> Unit,
    onPostClick: (Long) -> Unit,
    onCoordiClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white),
    ) {
        MyProfileAppBar(onMoreClick = onMoreClick)

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    ProfileHeader(
                        name = uiState.name,
                        rank = uiState.rank,
                        department = uiState.department,
                        profileImageUrl = uiState.profileImageUrl,
                    )
                    MyProfileEditButton(
                        onClick = onEditClick,
                        modifier = Modifier.padding(top = 24.dp),
                    )
                }
            }
            outfitSection(
                title = "나의 코디",
                outfits = uiState.myOutfits,
                onPostClick = onPostClick,
                emptyTitle = "아직 받은 코디가 없어요",
                emptyDescription = "동료가 골라준 코디가 여기에 모여요",
            )
            outfitSection(
                title = "내가 만든 코디",
                outfits = uiState.createdOutfits,
                onPostClick = onPostClick,
                emptyTitle = "아직 만든 코디가 없어요",
                emptyDescription = "동료에게 첫 코디를 골라줘 보세요",
                emptyActionText = "코디 만들기",
                onEmptyActionClick = onCoordiClick,
            )
        }
    }
}

private fun LazyGridScope.outfitSection(
    title: String,
    outfits: List<MyProfileOutfitUiModel>,
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
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
    if (outfits.isEmpty()) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            SopkathonEmptyState(
                iconRes = R.drawable.ic_myprofile_hanger_32,
                title = emptyTitle,
                description = emptyDescription,
                actionText = emptyActionText,
                onActionClick = onEmptyActionClick,
                modifier = Modifier.padding(bottom = 24.dp),
            )
        }
    }
    items(outfits, key = { "$title:${it.postId}" }) { outfit ->
        PostGridCard(
            title = outfit.title,
            imageUrl = outfit.imageUrl,
            likeCount = outfit.likeCount,
            onClick = { onPostClick(outfit.postId) },
            modifier = Modifier.padding(bottom = 24.dp),
        )
    }
}

@ScreenPreview
@Composable
private fun MyProfileScreenPreview() {
    SopkathonTheme {
        MyProfileScreen(
            uiState = MyProfileUiState.dummy,
            onMoreClick = {},
            onEditClick = {},
            onPostClick = {},
            onCoordiClick = {},
        )
    }
}
