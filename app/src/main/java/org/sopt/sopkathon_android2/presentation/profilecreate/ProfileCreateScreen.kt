package org.sopt.sopkathon_android2.presentation.profilecreate

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun ProfileCreateRoute(
    modifier: Modifier = Modifier,
    viewModel: ProfileCreateViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ProfileCreateScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun ProfileCreateScreen(
    uiState: ProfileCreateUiState,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = uiState.title)
    }
}

@Preview(showBackground = true)
@Composable
private fun ProfileCreateScreenPreview() {
    SopkathonTheme {
        ProfileCreateScreen(uiState = ProfileCreateUiState(title = "ProfileCreate"))
    }
}
