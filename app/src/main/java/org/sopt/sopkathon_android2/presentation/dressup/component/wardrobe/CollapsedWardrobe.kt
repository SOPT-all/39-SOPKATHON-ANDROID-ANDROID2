package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiState

private val CollapsedWidth = 76.dp

@Composable
internal fun CollapsedWardrobe(
    uiState: WardrobeUiState,
    selectedKeys: Set<String>,
    onCategoryClick: () -> Unit,
    onExpandClick: () -> Unit,
    onAddClick: () -> Unit,
    onClothesClick: (WardrobeClothes) -> Unit,
) {
    val colors = SopkathonTheme.colors

    Box(
        modifier = Modifier
            .width(CollapsedWidth)
            .fillMaxHeight(),
    ) {
        Column(
            modifier = Modifier
                .padding(start = 12.dp, top = 14.dp)
                .width(52.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 펼침 상태 칩과 같은 시작 위치에 두어 접고 펼 때 칩이 밀리지 않게 함
            Column(
                modifier = Modifier
                    .align(Alignment.Start)
                    .noRippleClickable(onClick = onCategoryClick),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CategoryTab(
                    label = uiState.category.label,
                    isSelected = true,
                    onClick = onCategoryClick,
                )
                Icon(
                    painter = painterResource(R.drawable.ic_dressup_chevrondown_24),
                    contentDescription = null,
                    tint = Color.Unspecified,
                )
            }

            Box(
                modifier = Modifier
                    .padding(top = 6.dp)
                    .size(52.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(colors.white)
                    .dashedBorder(width = 1.5.dp, color = colors.coolGray300, cornerRadius = 12.dp)
                    .noRippleClickable(onClick = onAddClick),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_dressup_plus_24),
                    contentDescription = "옷 추가",
                    tint = Color.Unspecified,
                )
            }

            LazyColumn(
                modifier = Modifier.padding(top = 12.dp),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(items = uiState.clothes, key = { it.key }) { clothes ->
                    val isSelected = clothes.key in selectedKeys
                    Box(
                        modifier = Modifier
                            .size(width = 52.dp, height = 56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) colors.white else colors.coolGray50)
                            .selectedBorder(isSelected = isSelected, color = colors.coolGray950, cornerRadius = 12.dp)
                            .noRippleClickable { onClothesClick(clothes) }
                            .padding(horizontal = 5.dp, vertical = 6.dp),
                    ) {
                        ClothesImage(clothes = clothes)
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .offset(y = 13.dp)
                .size(width = 12.dp, height = 66.dp)
                .noRippleClickable(onClick = onExpandClick),
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 4.dp)
                    .size(width = 4.dp, height = 40.dp)
                    .background(colors.coolGray150, RoundedCornerShape(2.dp)),
            )
            Icon(
                painter = painterResource(R.drawable.ic_dressup_chevronright_20),
                contentDescription = "옷장 펼치기",
                tint = Color.Unspecified,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .offset(x = (-6).dp),
            )
        }
    }
}
