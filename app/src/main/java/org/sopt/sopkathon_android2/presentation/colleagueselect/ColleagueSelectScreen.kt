package org.sopt.sopkathon_android2.presentation.colleagueselect

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

// 2.1 동료 선택
@Composable
fun ColleagueSelectRoute(
    onNextClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ColleagueSelectViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ColleagueSelectScreen(
        uiState = uiState,
        onNextClick = onNextClick,
        modifier = modifier,
    )
}

@Composable
private fun ColleagueSelectScreen(
    uiState: ColleagueSelectUiState,
    onNextClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = uiState.title)

        // 네비게이션 확인용 임시 버튼, 화면 구현 시 교체
        TextButton(onClick = { onNextClick(1L) }) {
            Text(text = "옷 입히기")
        }
    }
}

@ScreenPreview
@Composable
private fun ColleagueSelectScreenPreview() {
    SopkathonTheme {
        ColleagueSelectScreen(
            uiState = ColleagueSelectUiState(title = "ColleagueSelect"),
            onNextClick = {},
        )
    }
}
