package org.sopt.sopkathon_android2.presentation.consent

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

// 0.2 사진 사용 동의
@Composable
fun ConsentRoute(
    onAgreeClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ConsentViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ConsentScreen(
        uiState = uiState,
        onAgreeClick = onAgreeClick,
        onSkipClick = onSkipClick,
        modifier = modifier,
    )
}

@Composable
private fun ConsentScreen(
    uiState: ConsentUiState,
    onAgreeClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = uiState.title)

        // 네비게이션 확인용 임시 버튼, 화면 구현 시 교체
        TextButton(onClick = { onAgreeClick() }) {
            Text(text = "동의하고 시작하기")
        }
        TextButton(onClick = { onSkipClick() }) {
            Text(text = "둘러보기")
        }
    }
}

@ScreenPreview
@Composable
private fun ConsentScreenPreview() {
    SopkathonTheme {
        ConsentScreen(
            uiState = ConsentUiState(title = "Consent"),
            onAgreeClick = {},
            onSkipClick = {},
        )
    }
}
