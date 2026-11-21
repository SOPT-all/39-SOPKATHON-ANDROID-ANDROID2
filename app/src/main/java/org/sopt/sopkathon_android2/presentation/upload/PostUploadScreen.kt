package org.sopt.sopkathon_android2.presentation.upload

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.common.extensions.clearFocusOnTap
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.main.navigation.PostUpload
import org.sopt.sopkathon_android2.presentation.upload.component.PostUploadButton
import org.sopt.sopkathon_android2.presentation.upload.component.PostUploadNotice
import org.sopt.sopkathon_android2.presentation.upload.component.PostUploadOutfitSummary
import org.sopt.sopkathon_android2.presentation.upload.component.PostUploadTextField
import org.sopt.sopkathon_android2.presentation.upload.component.PostUploadTopBar
import org.sopt.sopkathon_android2.presentation.upload.component.PostVisibilitySelector
import org.sopt.sopkathon_android2.presentation.upload.model.PostVisibility

// 2.3 코디 게시하기
@Composable
fun PostUploadRoute(
    postUpload: PostUpload,
    navigateUp: () -> Unit,
    navigateToFeed: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostUploadViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(postUpload) { viewModel.setOutfit(postUpload) }

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                PostUploadUiEffect.NavigateToFeed -> navigateToFeed()
            }
        }
    }

    PostUploadScreen(
        uiState = uiState,
        titleState = viewModel.titleState,
        descriptionState = viewModel.descriptionState,
        navigateUp = navigateUp,
        onVisibilityClick = viewModel::onVisibilityClick,
        onPostClick = viewModel::onPostClick,
        modifier = modifier,
    )
}

@Composable
private fun PostUploadScreen(
    uiState: PostUploadUiState,
    titleState: TextFieldState,
    descriptionState: TextFieldState,
    navigateUp: () -> Unit,
    onVisibilityClick: (PostVisibility) -> Unit,
    onPostClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white)
            .imePadding()
            .clearFocusOnTap(),
    ) {
        PostUploadTopBar(
            title = "코디 게시하기",
            navigateUp = navigateUp,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, top = 4.dp, end = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PostUploadOutfitSummary(
                imageUri = uiState.imageUri,
                colleagueName = uiState.colleagueName,
                colleaguePosition = uiState.colleaguePosition,
                items = uiState.items,
            )

            PostUploadTextField(
                state = titleState,
                label = "제목",
                placeholder = "제목을 입력해 주세요",
                maxLength = PostUploadUiState.TITLE_MAX_LENGTH,
                imeAction = ImeAction.Next,
            )

            PostUploadTextField(
                state = descriptionState,
                label = "한 줄 소개 (선택)",
                placeholder = "${uiState.colleaguePosition}님께 어울릴 코디를 소개해 주세요",
                maxLength = PostUploadUiState.DESCRIPTION_MAX_LENGTH,
                isSingleLine = false,
                imeAction = ImeAction.Done,
            )

            PostVisibilitySelector(
                selected = uiState.visibility,
                onSelect = onVisibilityClick,
            )

            if (uiState.incompleteCategoryLabels.isNotEmpty()) {
                PostUploadNotice(categoryLabels = uiState.incompleteCategoryLabels)
            }
        }

        PostUploadButton(
            text = "피드에 게시하기",
            isEnabled = uiState.isPostEnabled,
            onClick = onPostClick,
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 24.dp),
        )
    }
}

@ScreenPreview
@Composable
private fun PostUploadScreenPreview() {
    SopkathonTheme {
        PostUploadScreen(
            uiState = PostUploadUiState.dummy,
            titleState = rememberTextFieldState(PostUploadUiState.dummy.title),
            descriptionState = rememberTextFieldState(),
            navigateUp = {},
            onVisibilityClick = {},
            onPostClick = {},
        )
    }
}
