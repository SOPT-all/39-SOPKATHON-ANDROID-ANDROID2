package org.sopt.sopkathon_android2.presentation.feed.component

import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

enum class FeedScope(val label: String) {
    DEPARTMENT("우리 부서"),
    COMPANY("회사 전체"),
    POPULAR("인기"),
}

@Composable
fun FeedScopeTabs(
    selectedScope: FeedScope,
    onScopeClick: (FeedScope) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        FeedScope.entries.forEach { scope ->
            FeedScopeChip(
                label = scope.label,
                isSelected = scope == selectedScope,
                onClick = { onScopeClick(scope) },
            )
        }
    }
}

@Composable
private fun FeedScopeChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        modifier = modifier
            .background(
                color = if (isSelected) SopkathonTheme.colors.coolGray950 else SopkathonTheme.colors.coolGray50,
                shape = RoundedCornerShape(percent = 50),
            )
            .selectable(
                selected = isSelected,
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                role = Role.Tab,
                onClick = onClick,
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        color = if (isSelected) SopkathonTheme.colors.white else SopkathonTheme.colors.coolGray500,
        style = SopkathonTheme.typography.tab,
        maxLines = 1,
    )
}

@Preview(showBackground = true)
@Composable
private fun FeedScopeTabsPreview() {
    var selectedScope by rememberSaveable { mutableStateOf(FeedScope.DEPARTMENT) }
    SopkathonTheme {
        FeedScopeTabs(
            selectedScope = selectedScope,
            onScopeClick = { selectedScope = it },
            modifier = Modifier.padding(16.dp),
        )
    }
}
