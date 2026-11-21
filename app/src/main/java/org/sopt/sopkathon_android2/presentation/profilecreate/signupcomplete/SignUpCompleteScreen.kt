package org.sopt.sopkathon_android2.presentation.profilecreate.signupcomplete

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.preview.ScreenPreview
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

// 0.3-7 가입 완료
@Composable
fun SignUpCompleteRoute(
    name: String,
    department: String,
    rank: String,
    photoUri: String,
    onFeedClick: () -> Unit,
    onFirstCoordiClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SignUpCompleteScreen(
        name = name,
        department = department,
        rank = rank,
        photoUri = photoUri,
        onFeedClick = onFeedClick,
        onFirstCoordiClick = onFirstCoordiClick,
        modifier = modifier,
    )
}

@Composable
private fun SignUpCompleteScreen(
    name: String,
    department: String,
    rank: String,
    photoUri: String,
    onFeedClick: () -> Unit,
    onFirstCoordiClick: () -> Unit,
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
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(SopkathonTheme.colors.coolGray150),
                contentScale = ContentScale.Crop,
            )
            Text(
                text = "${name}님, 환영해요!",
                style = SopkathonTheme.typography.display.copy(fontSize = 26.sp, lineHeight = 39.sp),
                color = SopkathonTheme.colors.coolGray950,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "$department · $rank",
                style = SopkathonTheme.typography.subtitle,
                color = SopkathonTheme.colors.coolGray500,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "프로필이 만들어졌어요.\n동료들의 코디를 구경하거나\n부장님께 첫 코디를 입혀 보세요.",
                style = SopkathonTheme.typography.bodySmall.copy(lineHeight = 21.sp),
                color = SopkathonTheme.colors.coolGray500,
                textAlign = TextAlign.Center,
            )
        }

        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(SopkathonTheme.colors.coolGray950)
                    .noRippleClickable(onClick = onFeedClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "피드 둘러보기",
                    style = SopkathonTheme.typography.buttonLarge,
                    color = SopkathonTheme.colors.white,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .noRippleClickable(onClick = onFirstCoordiClick),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "첫 코디 만들기",
                    style = SopkathonTheme.typography.button.copy(fontSize = 15.sp),
                    color = SopkathonTheme.colors.coolGray950,
                )
            }
        }
    }
}

@ScreenPreview
@Composable
private fun SignUpCompleteScreenPreview() {
    SopkathonTheme {
        SignUpCompleteScreen(
            name = "김민지",
            department = "마케팅팀",
            rank = "대리",
            photoUri = "",
            onFeedClick = {},
            onFirstCoordiClick = {},
        )
    }
}
