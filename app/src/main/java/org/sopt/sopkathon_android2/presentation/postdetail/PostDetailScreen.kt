package org.sopt.sopkathon_android2.presentation.postdetail

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

// 5 게시물 상세
@Composable
fun PostDetailRoute(
    postId: Long,
    navigateUp: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    onDressUpClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostDetailViewModel = viewModel(),
) {
    // TODO 서버 연동 시 postId로 데이터 요청
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostDetailScreen(
        uiState = uiState,
        navigateUp = navigateUp,
        onColleagueClick = onColleagueClick,
        onDressUpClick = onDressUpClick,
        modifier = modifier,
    )
}

@Composable
private fun PostDetailScreen(
    uiState: PostDetailUiState,
    navigateUp: () -> Unit,
    onColleagueClick: (Long) -> Unit,
    onDressUpClick: () -> Unit,
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
        TextButton(onClick = { onColleagueClick(1L) }) {
            Text(text = "동료 프로필")
        }
        TextButton(onClick = { onDressUpClick() }) {
            Text(text = "나도 골라주기")
        }
    }
}

@ScreenPreview
@Composable
private fun PostDetailScreenPreview() {
    SopkathonTheme {
        PostDetailScreen(
            uiState = PostDetailUiState(title = "PostDetail"),
            navigateUp = {},
            onColleagueClick = {},
            onDressUpClick = {},
        )
    }
}
