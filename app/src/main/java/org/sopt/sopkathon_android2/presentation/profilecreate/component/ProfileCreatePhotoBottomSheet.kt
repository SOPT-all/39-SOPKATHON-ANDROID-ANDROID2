package org.sopt.sopkathon_android2.presentation.profilecreate.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 프로필 사진 올리기 바텀시트
 *
 * 피그마의 카메라로 찍기는 제외하고 앨범에서 고르기만 제공
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileCreatePhotoBottomSheet(
    onAlbumClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        containerColor = SopkathonTheme.colors.white,
        scrimColor = Color(0x73000000),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 8.dp)
                    .size(width = 36.dp, height = 4.dp)
                    .background(SopkathonTheme.colors.coolGray200, RoundedCornerShape(2.dp)),
            )
        },
    ) {
        Column(modifier = Modifier.padding(bottom = 24.dp)) {
            Column(
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "프로필 사진 올리기",
                    style = SopkathonTheme.typography.buttonLarge.copy(fontSize = 18.sp, lineHeight = 27.sp),
                    color = SopkathonTheme.colors.coolGray950,
                )
                Text(
                    text = "정면 얼굴이 잘 보이는 사진이 좋아요. 옷 입히기 캐릭터 얼굴로 쓰여요.",
                    style = SopkathonTheme.typography.bodySmall,
                    color = SopkathonTheme.colors.coolGray500,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .noRippleClickable(onClick = onAlbumClick)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(SopkathonTheme.colors.coolGray50, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(text = "🖼", fontSize = 17.sp)
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "앨범에서 고르기",
                        style = SopkathonTheme.typography.buttonLarge,
                        color = SopkathonTheme.colors.coolGray950,
                    )
                    Text(
                        text = "갤러리에 있는 사진을 골라요",
                        style = SopkathonTheme.typography.caption.copy(fontSize = 13.sp, lineHeight = 19.5.sp),
                        color = SopkathonTheme.colors.coolGray500,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp)
                    .fillMaxWidth()
                    .background(SopkathonTheme.colors.coolGray50, RoundedCornerShape(28.dp))
                    .noRippleClickable(onClick = onDismiss)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "취소",
                    style = SopkathonTheme.typography.buttonLarge,
                    color = SopkathonTheme.colors.coolGray950,
                )
            }
        }
    }
}
