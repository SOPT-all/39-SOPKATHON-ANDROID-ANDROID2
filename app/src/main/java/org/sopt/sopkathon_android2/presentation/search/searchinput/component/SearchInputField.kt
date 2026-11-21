package org.sopt.sopkathon_android2.presentation.search.searchinput.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.common.extensions.noRippleClickable
import org.sopt.sopkathon_android2.core.designsystem.component.textfield.SopkathonBasicTextField
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme

@Composable
fun SearchInputField(
    state: TextFieldState,
    onSearch: () -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .height(48.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SopkathonTheme.colors.coolGray50)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search_search_24),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(24.dp),
        )
        SopkathonBasicTextField(
            state = state,
            textColor = SopkathonTheme.colors.coolGray950,
            textStyle = SopkathonTheme.typography.body,
            placeholder = "이름, 직급으로 검색",
            placeholderColor = SopkathonTheme.colors.coolGray500,
            placeholderStyle = SopkathonTheme.typography.body,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            onKeyboardAction = { onSearch() },
            modifier = Modifier.weight(1f).focusRequester(focusRequester),
        )
        if (state.text.isNotEmpty()) {
            Icon(
                painter = painterResource(R.drawable.ic_search_clear_20),
                contentDescription = "지우기",
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(20.dp)
                    .noRippleClickable { state.clearText() },
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360)
@Composable
private fun SearchInputFieldPreview() {
    SopkathonTheme {
        SearchInputField(
            state = rememberTextFieldState("박정"),
            onSearch = {},
            focusRequester = remember { FocusRequester() },
            modifier = Modifier.padding(16.dp),
        )
    }
}
