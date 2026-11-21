package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
internal fun ClothesUploader(
    cutoutUri: Uri?,
    isProcessing: Boolean,
    onCameraClick: () -> Unit,
    onGalleryClick: () -> Unit,
    onReselectClick: () -> Unit,
) {
    val colors = SopkathonTheme.colors
    val areaModifier = Modifier
        .fillMaxWidth()
        .height(188.dp)
        .clip(RoundedCornerShape(12.dp))

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.white, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (cutoutUri == null) {
            Box(
                modifier = areaModifier
                    .background(colors.coolGray50)
                    .dashedBorder(width = 1.5.dp, color = colors.coolGray300, cornerRadius = 12.dp)
                    .noRippleClickable(enabled = !isProcessing, onClick = onGalleryClick),
                contentAlignment = Alignment.Center,
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = colors.coolGray950,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(colors.white, CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_dressup_plus_24),
                                contentDescription = null,
                                tint = colors.coolGray950,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "옷 사진을 올려 주세요",
                            style = SopkathonTheme.typography.buttonLarge,
                            color = colors.coolGray950,
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "배경은 자동으로 지워져요",
                            style = SopkathonTheme.typography.caption,
                            color = colors.coolGray500,
                        )
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                UploaderButton(
                    text = "📷  카메라로 찍기",
                    onClick = onCameraClick,
                    modifier = Modifier.weight(1f),
                    isEnabled = !isProcessing,
                )
                UploaderButton(
                    text = "🖼  앨범에서 고르기",
                    onClick = onGalleryClick,
                    modifier = Modifier.weight(1f),
                    isEnabled = !isProcessing,
                )
            }
        } else {
            Box(
                modifier = areaModifier
                    .background(colors.white)
                    .border(1.dp, colors.coolGray100, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = cutoutUri,
                    contentDescription = null,
                    modifier = Modifier.size(150.dp),
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(colors.coolGray950, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp),
                ) {
                    Text(
                        text = "✓ 배경 제거 완료",
                        style = SopkathonTheme.typography.buttonSmall,
                        color = colors.white,
                    )
                }
            }
            UploaderButton(
                text = "다시 선택",
                onClick = onReselectClick,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun UploaderButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(SopkathonTheme.colors.coolGray100)
            .noRippleClickable(enabled = isEnabled, onClick = onClick)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = SopkathonTheme.typography.button,
            color = SopkathonTheme.colors.coolGray950,
        )
    }
}
