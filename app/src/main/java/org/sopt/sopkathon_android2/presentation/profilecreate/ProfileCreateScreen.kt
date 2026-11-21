package org.sopt.sopkathon_android2.presentation.profilecreate

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.core.common.extensions.clearFocusOnTap
import org.sopt.sopkathon_android2.core.common.extensions.toast
import org.sopt.sopkathon_android2.presentation.profilecreate.component.AppbarBack
import org.sopt.sopkathon_android2.presentation.profilecreate.component.ProfileCreateTextField
import org.sopt.sopkathon_android2.presentation.profilecreate.component.PhotoUploader
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.main.navigation.SignUpComplete
import org.sopt.sopkathon_android2.presentation.profilecreate.ProfileCreateUiState.Companion.NAME_MAX_LENGTH
import org.sopt.sopkathon_android2.presentation.profilecreate.component.ProfileCreatePhotoBottomSheet
import org.sopt.sopkathon_android2.presentation.profilecreate.component.ProfileCreateSaveButton
import org.sopt.sopkathon_android2.presentation.profilecreate.component.ProfileCreateSelectField

// 0.3 프로필 생성
@Composable
fun ProfileCreateRoute(
    navigateUp: () -> Unit,
    onSaveClick: (SignUpComplete) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileCreateViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is ProfileCreateUiEffect.NavigateToSignUpComplete -> onSaveClick(effect.route)
                is ProfileCreateUiEffect.ShowToast -> context.toast(effect.message)
            }
        }
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        uri?.let(viewModel::updatePhoto)
    }

    ProfileCreateScreen(
        uiState = uiState,
        nameState = viewModel.nameState,
        navigateUp = navigateUp,
        onPhotoClick = {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onDepartmentSelect = viewModel::updateDepartment,
        onRankSelect = viewModel::updateRank,
        onSaveClick = viewModel::saveProfile,
        modifier = modifier,
    )
}

@Composable
private fun ProfileCreateScreen(
    uiState: ProfileCreateUiState,
    nameState: TextFieldState,
    navigateUp: () -> Unit,
    onPhotoClick: () -> Unit,
    onDepartmentSelect: (String) -> Unit,
    onRankSelect: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPhotoSheetVisible by rememberSaveable { mutableStateOf(false) }

    if (isPhotoSheetVisible) {
        ProfileCreatePhotoBottomSheet(
            onAlbumClick = {
                isPhotoSheetVisible = false
                onPhotoClick()
            },
            onDismiss = { isPhotoSheetVisible = false },
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white)
            .imePadding()
            .clearFocusOnTap(),
    ) {
        AppbarBack(
            title = "프로필 만들기",
            navigateUp = navigateUp,
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 16.dp, end = 16.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            Text(
                text = "동료들이 알아볼 수 있게\n프로필을 만들어 주세요",
                style = SopkathonTheme.typography.display,
                color = SopkathonTheme.colors.coolGray950,
            )
            Text(
                text = "사진과 이름은 옷입히기 게임 속 아바타에 그대로 쓰여요.",
                style = SopkathonTheme.typography.bodySmall,
                color = SopkathonTheme.colors.coolGray500,
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PhotoUploader(
                    photoUri = uiState.photoUri,
                    onClick = { isPhotoSheetVisible = true },
                )
                Text(
                    text = "얼굴이 잘 보이는 사진을 올려 주세요",
                    style = SopkathonTheme.typography.caption,
                    color = SopkathonTheme.colors.coolGray500,
                )
            }

            ProfileCreateTextField(
                state = nameState,
                label = "이름",
                placeholder = "이름을 입력해 주세요",
                helper = "실명으로 적어 주세요",
                maxLength = NAME_MAX_LENGTH,
            )

            ProfileCreateSelectField(
                value = uiState.department,
                label = "부서",
                placeholder = "부서를 선택해 주세요",
                title = "부서 선택",
                searchPlaceholder = "부서 이름 검색",
                customLabel = "찾는 부서가 없나요? 직접 입력하기",
                options = ProfileCreateUiState.departments,
                onOptionSelect = onDepartmentSelect,
            )

            ProfileCreateSelectField(
                value = uiState.rank,
                label = "직급",
                placeholder = "직급을 선택해 주세요",
                title = "직급 선택",
                customLabel = "찾는 직급이 없나요? 직접 입력하기",
                options = ProfileCreateUiState.ranks,
                onOptionSelect = onRankSelect,
            )
        }

        ProfileCreateSaveButton(
            text = "프로필 저장",
            enabled = uiState.isSaveEnabled,
            onClick = onSaveClick,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
        )
    }
}

@ScreenPreview
@Composable
private fun ProfileCreateScreenPreview() {
    SopkathonTheme {
        ProfileCreateScreen(
            uiState = ProfileCreateUiState(),
            nameState = rememberTextFieldState(),
            navigateUp = {},
            onPhotoClick = {},
            onDepartmentSelect = {},
            onRankSelect = {},
            onSaveClick = {},
        )
    }
}
