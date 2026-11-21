package org.sopt.sopkathon_android2.presentation.consent

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.consent.component.ConsentCheckRow
import org.sopt.sopkathon_android2.presentation.consent.component.ConsentInfoItem

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
        onCheckClick = viewModel::toggleConsent,
        // 약관 바텀시트 나오면 연결
        onDetailClick = {},
        onAgreeClick = onAgreeClick,
        onSkipClick = onSkipClick,
        modifier = modifier,
    )
}

@Composable
private fun ConsentScreen(
    uiState: ConsentUiState,
    onCheckClick: () -> Unit,
    onDetailClick: () -> Unit,
    onAgreeClick: () -> Unit,
    onSkipClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SopkathonTheme.colors.white),
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp),
        ) {
            Text(
                text = "부꾸부꾸",
                style = SopkathonTheme.typography.subtitle,
                color = SopkathonTheme.colors.coolGray950,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "동료가 나를 꾸밀 수 있도록\n사진 사용에 동의해 주세요",
                style = SopkathonTheme.typography.display,
                color = SopkathonTheme.colors.coolGray950,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "사진은 코디 캐릭터의 얼굴로만 쓰여요.",
                style = SopkathonTheme.typography.bodySmall,
                color = SopkathonTheme.colors.coolGray500,
                modifier = Modifier.padding(horizontal = 20.dp),
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(132.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(SopkathonTheme.colors.coolGray50),
            ) {
                Image(
                    painter = painterResource(R.drawable.img_consent_preview),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Column(
                modifier = Modifier.padding(horizontal = 16.dp),
            ) {
                ConsentInfoItem(
                    iconRes = R.drawable.ic_consent_photo_24,
                    title = "프로필 사진",
                    description = "동료 목록과 피드 썸네일에만 보여요.",
                )
                ConsentInfoItem(
                    iconRes = R.drawable.ic_consent_hanger_24,
                    title = "코디",
                    description = "얼굴은 그대로, 옷만 갈아입혀요.",
                )
                ConsentInfoItem(
                    iconRes = R.drawable.ic_consent_undo_24,
                    title = "동의 철회",
                    description = "내 프로필에서 언제든 철회할 수 있어요.",
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(SopkathonTheme.colors.coolGray100),
            )
        }

        // 하단 동의 영역 고정
        Column(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, end = 16.dp, bottom = 10.dp),
        ) {
            ConsentCheckRow(
                text = "(필수) 사진 사용에 동의합니다",
                isChecked = uiState.isConsentChecked,
                onCheckClick = onCheckClick,
                onDetailClick = onDetailClick,
            )

            Spacer(modifier = Modifier.height(8.dp))

            ConsentStartButton(
                isEnabled = uiState.isConsentChecked,
                onClick = onAgreeClick,
            )

            Spacer(modifier = Modifier.height(2.dp))

            ConsentSkipButton(
                onClick = onSkipClick,
            )
        }
    }
}

@Composable
private fun ConsentStartButton(
    isEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isEnabled) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray150)
            .noRippleClickable(enabled = isEnabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "동의하고 시작하기",
            style = SopkathonTheme.typography.buttonLarge,
            color = if (isEnabled) SopkathonTheme.colors.white else SopkathonTheme.colors.coolGray300,
        )
    }
}

@Composable
private fun ConsentSkipButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(40.dp)
            .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "동의하지 않고 둘러보기",
            style = SopkathonTheme.typography.button,
            color = SopkathonTheme.colors.coolGray500,
        )
    }
}

@ScreenPreview
@Composable
private fun ConsentScreenPreview() {
    SopkathonTheme {
        ConsentScreen(
            uiState = ConsentUiState(),
            onCheckClick = {},
            onDetailClick = {},
            onAgreeClick = {},
            onSkipClick = {},
        )
    }
}

@ScreenPreview
@Composable
private fun ConsentScreenCheckedPreview() {
    SopkathonTheme {
        ConsentScreen(
            uiState = ConsentUiState(isConsentChecked = true),
            onCheckClick = {},
            onDetailClick = {},
            onAgreeClick = {},
            onSkipClick = {},
        )
    }
}

