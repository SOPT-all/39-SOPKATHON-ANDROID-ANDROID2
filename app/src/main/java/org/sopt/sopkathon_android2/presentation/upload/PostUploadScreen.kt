package org.sopt.sopkathon_android2.presentation.upload

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
fun PostUploadRoute(
    modifier: Modifier = Modifier,
    viewModel: PostUploadViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    PostUploadScreen(
        uiState = uiState,
        modifier = modifier,
    )
}

@Composable
private fun PostUploadScreen(
    uiState: PostUploadUiState,
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
private fun PostUploadScreenPreview() {
    SopkathonTheme {
        PostUploadScreen(uiState = PostUploadUiState(title = "PostUpload"))
    }
}
