package org.sopt.sopkathon_android2.presentation.dressup

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

// 2.2 옷 입히기
@Composable
fun DressUpRoute(
    colleagueId: Long,
    navigateUp: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DressUpViewModel = viewModel(),
) {
    // TODO 서버 연동 시 colleagueId로 데이터 요청
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    DressUpScreen(
        uiState = uiState,
        navigateUp = navigateUp,
        onCompleteClick = onCompleteClick,
        modifier = modifier,
    )
}

@Composable
private fun DressUpScreen(
    uiState: DressUpUiState,
    navigateUp: () -> Unit,
    onCompleteClick: () -> Unit,
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
        TextButton(onClick = { onCompleteClick() }) {
            Text(text = "코디 완성")
        }
    }
}

@ScreenPreview
@Composable
private fun DressUpScreenPreview() {
    SopkathonTheme {
        DressUpScreen(
            uiState = DressUpUiState(title = "DressUp"),
            navigateUp = {},
            onCompleteClick = {},
        )
    }
}
