package org.sopt.sopkathon_android2.presentation.myprofile

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
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = uiState.title)
    }
}

@Preview(showBackground = true)
@Composable
private fun MyProfileScreenPreview() {
    SopkathonTheme {
        MyProfileScreen(uiState = MyProfileUiState(title = "MyProfile"))
    }
}
