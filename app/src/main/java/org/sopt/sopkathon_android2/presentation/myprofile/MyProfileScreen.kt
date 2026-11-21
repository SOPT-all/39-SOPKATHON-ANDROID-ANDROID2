package org.sopt.sopkathon_android2.presentation.myprofile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

// 3 내 프로필
@Composable
fun MyProfileRoute(
    modifier: Modifier = Modifier,
    viewModel: MyProfileViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MyProfileScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun MyProfileScreen(
    uiState: MyProfileUiState,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = uiState.title)

    }
}

@ScreenPreview
@Composable
private fun MyProfileScreenPreview() {
    SopkathonTheme {
        MyProfileScreen(
            uiState = MyProfileUiState(title = "MyProfile"),
        )
    }
}
