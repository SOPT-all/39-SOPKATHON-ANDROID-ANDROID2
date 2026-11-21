package org.sopt.sopkathon_android2.presentation.dressup.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

/**
 * 하단 "구매 링크" + "코디 완성"
 *
 * @param incompleteCount 이름이나 링크가 빈 옷 수. 0이면 배지 없음
 */
@Composable
fun DressUpBottomActions(
    incompleteCount: Int,
    isCompleteEnabled: Boolean,
    onPurchaseLinkClick: () -> Unit,
    onCompleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PurchaseLinkButton(
            incompleteCount = incompleteCount,
            onClick = onPurchaseLinkClick,
        )
        DressUpFilledButton(
            text = "코디 완성",
            onClick = onCompleteClick,
            isEnabled = isCompleteEnabled,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PurchaseLinkButton(
    incompleteCount: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = SopkathonTheme.colors

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .width(120.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(colors.coolGray100)
                .noRippleClickable(onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_dressup_link_20),
                contentDescription = null,
                tint = Color.Unspecified,
            )
            Text(
                text = "구매 링크",
                style = SopkathonTheme.typography.buttonLarge.copy(fontSize = 15.sp),
                color = colors.coolGray950,
            )
        }
        if (incompleteCount > 0) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-2).dp, y = (-6).dp)
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(colors.pink400),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = incompleteCount.toString(),
                    style = SopkathonTheme.typography.buttonSmall.copy(fontSize = 11.sp),
                    color = colors.white,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DressUpBottomActionsPreview() {
    SopkathonTheme {
        DressUpBottomActions(
            incompleteCount = 2,
            isCompleteEnabled = true,
            onPurchaseLinkClick = {},
            onCompleteClick = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}
