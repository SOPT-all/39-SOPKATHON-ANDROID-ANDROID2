package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import coil3.ColorImage
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.AsyncImagePreviewHandler
import coil3.compose.LocalAsyncImagePreviewHandler
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeCategory
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeUiState

private val CollapsedShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp)
private val ExpandedShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp)
private val CollapsedShadow = Shadow(radius = 6.dp, color = Color.Black.copy(alpha = 0.06f), offset = DpOffset(2.dp, 0.dp))
private val ExpandedShadow = Shadow(radius = 20.dp, color = Color.Black.copy(alpha = 0.08f), offset = DpOffset(4.dp, 0.dp))
private val DragThreshold = 24.dp

@Composable
fun WardrobeDrawer(
    uiState: WardrobeUiState,
    selectedKeys: Set<String>,
    onCategoryChange: (WardrobeCategory) -> Unit,
    onExpandedChange: (Boolean) -> Unit,
    onAddClick: () -> Unit,
    onClothesClick: (WardrobeClothes) -> Unit,
    modifier: Modifier = Modifier,
) {
    val isExpanded = uiState.isExpanded
    val shape = if (isExpanded) ExpandedShape else CollapsedShape
    val currentIsExpanded by rememberUpdatedState(isExpanded)
    val currentOnExpandedChange by rememberUpdatedState(onExpandedChange)

    AnimatedContent(
        targetState = isExpanded,
        modifier = modifier
            .fillMaxHeight()
            .dropShadow(shape = shape, shadow = if (isExpanded) ExpandedShadow else CollapsedShadow)
            .clip(shape)
            .background(SopkathonTheme.colors.white)
            .pointerInput(Unit) {
                val threshold = DragThreshold.toPx()
                var dragAmount = 0f
                detectHorizontalDragGestures(
                    onDragStart = { dragAmount = 0f },
                    onDragEnd = {
                        when {
                            dragAmount > threshold && !currentIsExpanded -> currentOnExpandedChange(true)
                            dragAmount < -threshold && currentIsExpanded -> currentOnExpandedChange(false)
                        }
                    },
                ) { change, amount ->
                    change.consume()
                    dragAmount += amount
                }
            },
        transitionSpec = { fadeIn() togetherWith fadeOut() using SizeTransform(clip = true) },
        label = "WardrobeDrawer",
    ) { expanded ->
        if (expanded) {
            ExpandedWardrobe(
                uiState = uiState,
                selectedKeys = selectedKeys,
                onCategoryChange = onCategoryChange,
                onCollapseClick = { onExpandedChange(false) },
                onAddClick = onAddClick,
                onClothesClick = onClothesClick,
            )
        } else {
            CollapsedWardrobe(
                uiState = uiState,
                selectedKeys = selectedKeys,
                onCategoryClick = {
                    onCategoryChange(
                        if (uiState.category == WardrobeCategory.TOP) WardrobeCategory.BOTTOM else WardrobeCategory.TOP,
                    )
                },
                onExpandClick = { onExpandedChange(true) },
                onAddClick = onAddClick,
                onClothesClick = onClothesClick,
            )
        }
    }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
internal fun WardrobePreviewTheme(content: @Composable () -> Unit) {
    SopkathonTheme {
        val color = SopkathonTheme.colors.coolGray200.toArgb()
        CompositionLocalProvider(
            LocalAsyncImagePreviewHandler provides AsyncImagePreviewHandler { ColorImage(color) },
            content = content,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8, widthDp = 360, heightDp = 600)
@Composable
private fun WardrobeDrawerCollapsedPreview() {
    WardrobePreviewTheme {
        WardrobeDrawer(
            uiState = WardrobeUiState.dummy,
            selectedKeys = setOf(WardrobeUiState.dummy.tops.first().key),
            onCategoryChange = {},
            onExpandedChange = {},
            onAddClick = {},
            onClothesClick = {},
            modifier = Modifier.padding(vertical = 20.dp).height(560.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF3F5F8, widthDp = 360, heightDp = 600)
@Composable
private fun WardrobeDrawerExpandedPreview() {
    WardrobePreviewTheme {
        WardrobeDrawer(
            uiState = WardrobeUiState.dummy.copy(category = WardrobeCategory.BOTTOM, isExpanded = true),
            selectedKeys = setOf(WardrobeUiState.dummy.bottoms.first().key),
            onCategoryChange = {},
            onExpandedChange = {},
            onAddClick = {},
            onClothesClick = {},
            modifier = Modifier.padding(vertical = 20.dp).height(560.dp),
        )
    }
}
