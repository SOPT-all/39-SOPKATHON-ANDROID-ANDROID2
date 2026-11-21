package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeCategory
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiState

private val ExpandedWidth = 204.dp

@Composable
internal fun ExpandedWardrobe(
    uiState: WardrobeUiState,
    selectedKeys: Set<String>,
    onCategoryChange: (WardrobeCategory) -> Unit,
    onCollapseClick: () -> Unit,
    onAddClick: () -> Unit,
    onClothesClick: (WardrobeClothes) -> Unit,
) {
    val colors = SopkathonTheme.colors

    Box(
        modifier = Modifier
            .width(ExpandedWidth)
            .fillMaxHeight(),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.padding(start = 12.dp, top = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                WardrobeCategory.entries.forEach { category ->
                    CategoryTab(
                        label = category.label,
                        isSelected = category == uiState.category,
                        onClick = { onCategoryChange(category) },
                    )
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(start = 12.dp, end = 16.dp, top = 14.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                item(key = "add") {
                    ExpandedGridItem(label = "옷 추가", isSelected = false) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.white)
                                .dashedBorder(width = 1.5.dp, color = colors.coolGray300, cornerRadius = 14.dp)
                                .noRippleClickable(onClick = onAddClick),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = "+",
                                style = SopkathonTheme.typography.display,
                                color = colors.coolGray500,
                            )
                        }
                    }
                }
                items(items = uiState.clothes, key = { it.key }) { clothes ->
                    val isSelected = clothes.key in selectedKeys
                    ExpandedGridItem(label = clothes.displayName, isSelected = isSelected) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .background(colors.white)
                                .selectedBorder(isSelected = isSelected, color = colors.coolGray950, cornerRadius = 14.dp)
                                .noRippleClickable { onClothesClick(clothes) }
                                .padding(horizontal = 10.dp, vertical = 9.dp),
                        ) {
                            ClothesImage(clothes = clothes)
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 16.dp, height = 48.dp)
                .noRippleClickable(onClick = onCollapseClick),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 4.dp, height = 40.dp)
                    .background(colors.coolGray300, RoundedCornerShape(2.dp)),
            )
        }
    }
}

@Composable
private fun ExpandedGridItem(
    label: String,
    isSelected: Boolean,
    content: @Composable () -> Unit,
) {
    Column {
        content()
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = SopkathonTheme.typography.buttonSmall,
            color = if (isSelected) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray500,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 2.dp),
        )
    }
}
