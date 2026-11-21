package org.sopt.sopkathon_android2.presentation.upload

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

@Composable
fun PostUploadRoute(
    colleagueId: Long,
    navigateUp: () -> Unit,
    onUploadClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PostUploadViewModel = viewModel(),
) {
    // TODO 서버 연동 시 colleagueId로 데이터 요청
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostUploadScreen(
        uiState = uiState,
        navigateUp = navigateUp,
        onUploadClick = onUploadClick,
        modifier = modifier,
    )
}

@Composable
private fun PostUploadScreen(
    uiState: PostUploadUiState,
    navigateUp: () -> Unit,
    onUploadClick: () -> Unit,
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
        TextButton(onClick = { onUploadClick() }) {
            Text(text = "게시하기")
        }
    }
}

@ScreenPreview
@Composable
private fun PostUploadScreenPreview() {
    SopkathonTheme {
        PostUploadScreen(
            uiState = PostUploadUiState(title = "PostUpload"),
            navigateUp = {},
            onUploadClick = {},
        )
    }
}
