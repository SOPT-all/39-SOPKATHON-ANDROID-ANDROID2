package org.sopt.sopkathon_android2.presentation.postdetail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.component.SopkathonSnackbarHost
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.postdetail.model.DummyPostOutfitItems
import org.sopt.sopkathon_android2.presentation.postdetail.model.OutfitItemUiModel

@Composable
fun PostItemSheet(
    item: OutfitItemUiModel,
    onDressUpClick: () -> Unit,
    onShopClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(horizontal = 20.dp).padding(bottom = 24.dp)) {
        SopkathonSnackbarHost()
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            AsyncImage(
                model = item.imageUrl,
                contentDescription = item.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(120.dp).clip(RoundedCornerShape(16.dp))
                    .background(SopkathonTheme.colors.coolGray50),
            )
            Spacer(Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(item.brand, style = SopkathonTheme.typography.caption, color = SopkathonTheme.colors.coolGray500)
                Text(item.name, style = SopkathonTheme.typography.itemName, color = SopkathonTheme.colors.coolGray950)
                Text(item.priceLabel, style = SopkathonTheme.typography.bodySmall, color = SopkathonTheme.colors.coolGray950)
            }
        }
        Spacer(Modifier.height(20.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SopkathonTheme.colors.coolGray50, RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            (listOf("카테고리" to item.category) + item.attributes).forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(label, style = SopkathonTheme.typography.caption, color = SopkathonTheme.colors.coolGray500)
                    Text(value, style = SopkathonTheme.typography.bodySmall, color = SopkathonTheme.colors.coolGray950)
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SheetAction("입혀보기", onDressUpClick, Modifier.weight(1f))
            SheetAction("쇼핑몰에서 보기 ↗", onShopClick, Modifier.weight(1f), primary = true, enabled = item.purchaseUrl != null)
        }
    }
}

@Composable
internal fun SheetAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    primary: Boolean = false,
    enabled: Boolean = true,
) {
    Box(
        modifier = modifier.height(52.dp).clip(RoundedCornerShape(50))
            .background(if (primary && enabled) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray50)
            .noRippleClickable(enabled = enabled, onClick = onClick),
        contentAlignment = androidx.compose.ui.Alignment.Center,
    ) {
        Text(
            text,
            style = SopkathonTheme.typography.button,
            color = if (!enabled) SopkathonTheme.colors.coolGray500 else if (primary) SopkathonTheme.colors.white else SopkathonTheme.colors.coolGray950,
        )
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun PostItemSheetPreview() {
    SopkathonTheme {
        PostItemSheet(DummyPostOutfitItems.items.first(), {}, {})
    }
}
