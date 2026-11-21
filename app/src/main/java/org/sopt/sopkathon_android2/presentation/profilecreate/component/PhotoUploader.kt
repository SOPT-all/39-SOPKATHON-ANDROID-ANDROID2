package org.sopt.sopkathon_android2.presentation.profilecreate.component

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 프로필 사진 첨부
 *
 * 사진 없으면 점선 원, 있으면 사진. 카메라 배지는 항상 검정
 */
@Composable
fun PhotoUploader(
    photoUri: Uri?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors

    Box(
        modifier = modifier
            .size(104.dp)
            .noRippleClickable(onClick = onClick),
    ) {
        if (photoUri == null) {
            Image(
                painter = painterResource(R.drawable.img_profilecreate_photo_empty_96),
                contentDescription = null,
                modifier = Modifier.size(96.dp),
            )
        } else {
            AsyncImage(
                model = photoUri,
                contentDescription = null,
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(colors.coolGray150),
                contentScale = ContentScale.Crop,
            )
        }

        Box(
            modifier = Modifier
                .offset(x = 68.dp, y = 66.dp)
                .size(32.dp)
                .clip(CircleShape)
                .background(colors.coolGray950)
                .border(2.dp, colors.white, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_profilecreate_camera_16),
                contentDescription = null,
                tint = Color.Unspecified,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhotoUploaderPreview() {
    SopkathonTheme {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            PhotoUploader(photoUri = null, onClick = {})
            PhotoUploader(photoUri = Uri.EMPTY, onClick = {})
        }
    }
}
