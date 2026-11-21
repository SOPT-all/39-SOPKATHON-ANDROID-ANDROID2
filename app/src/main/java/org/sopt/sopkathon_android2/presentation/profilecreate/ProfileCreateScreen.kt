package org.sopt.sopkathon_android2.presentation.profilecreate

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

// 0.3 프로필 생성
@Composable
fun ProfileCreateRoute(
    navigateUp: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileCreateViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileCreateScreen(
        uiState = uiState,
        navigateUp = navigateUp,
        onSaveClick = onSaveClick,
        modifier = modifier,
    )
}

@Composable
private fun ProfileCreateScreen(
    uiState: ProfileCreateUiState,
    navigateUp: () -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = uiState.title)

        // 네비게이션 확인용 임시 버튼, 화면 구현 시 교체
        TextButton(onClick = { navigateUp() }) {
            Text(text = "뒤로")
        }
        TextButton(onClick = { onSaveClick() }) {
            Text(text = "프로필 저장")
        }
    }
}

@ScreenPreview
@Composable
private fun ProfileCreateScreenPreview() {
    SopkathonTheme {
        ProfileCreateScreen(
            uiState = ProfileCreateUiState(title = "ProfileCreate"),
            navigateUp = {},
            onSaveClick = {},
        )
    }
}
