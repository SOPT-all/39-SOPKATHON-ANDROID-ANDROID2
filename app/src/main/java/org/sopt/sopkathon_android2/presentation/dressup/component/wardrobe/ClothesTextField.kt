package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.core.designsystem.component.textfield.SopkathonBasicTextField
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

private val FIELD_KEYBOARD_MARGIN = 16.dp

@Composable
internal fun ClothesTextField(
    state: TextFieldState,
    label: String,
    placeholder: String,
    keyboardOptions: KeyboardOptions,
) {
    val colors = SopkathonTheme.colors
    val shape = RoundedCornerShape(12.dp)
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val isFilled = state.text.isNotEmpty()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    var fieldSize by remember { mutableStateOf(IntSize.Zero) }
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    // 기본 동작은 커서 줄만 보여서 키보드가 올라오는 동안 입력칸 전체와 아래 여백까지 계속 맞춤
    LaunchedEffect(isFocused, imeBottom) {
        if (isFocused && imeBottom > 0) {
            val bottomMargin = with(density) { FIELD_KEYBOARD_MARGIN.toPx() }
            bringIntoViewRequester.bringIntoView(
                Rect(0f, 0f, fieldSize.width.toFloat(), fieldSize.height + bottomMargin),
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged { fieldSize = it }
            .bringIntoViewRequester(bringIntoViewRequester)
            .clip(shape)
            .background(if (isFocused || isFilled) colors.white else colors.coolGray50)
            .then(
                when {
                    isFocused -> Modifier.border(1.dp, colors.coolGray950, shape)
                    isFilled -> Modifier.border(1.dp, colors.coolGray200, shape)
                    else -> Modifier
                },
            )
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        Text(
            text = label,
            style = SopkathonTheme.typography.caption,
            color = colors.coolGray500,
        )
        SopkathonBasicTextField(
            state = state,
            textColor = colors.coolGray950,
            textStyle = SopkathonTheme.typography.body,
            placeholder = placeholder,
            placeholderColor = colors.coolGray300,
            placeholderStyle = SopkathonTheme.typography.body,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = keyboardOptions,
            interactionSource = interactionSource,
        )
    }
}
