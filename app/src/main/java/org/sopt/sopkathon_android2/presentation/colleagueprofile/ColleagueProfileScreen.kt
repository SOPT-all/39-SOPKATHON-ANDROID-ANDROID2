package org.sopt.sopkathon_android2.presentation.colleagueprofile

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

// 4 동료 프로필
@Composable
fun ColleagueProfileRoute(
    userId: Long,
    navigateUp: () -> Unit,
    onPostClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ColleagueProfileViewModel = viewModel(),
) {
    // TODO 서버 연동 시 userId로 데이터 요청
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ColleagueProfileScreen(
        uiState = uiState,
        navigateUp = navigateUp,
        onPostClick = onPostClick,
        modifier = modifier,
    )
}

@Composable
private fun ColleagueProfileScreen(
    uiState: ColleagueProfileUiState,
    navigateUp: () -> Unit,
    onPostClick: (Long) -> Unit,
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
        TextButton(onClick = { onPostClick(1L) }) {
            Text(text = "게시물 상세")
        }
    }
}

@ScreenPreview
@Composable
private fun ColleagueProfileScreenPreview() {
    SopkathonTheme {
        ColleagueProfileScreen(
            uiState = ColleagueProfileUiState(title = "ColleagueProfile"),
            navigateUp = {},
            onPostClick = {},
        )
    }
}
