# Compose

## Naming

| Target | Rule | Example |
| --- | --- | --- |
| Function | lowerCamelCase | `fun getUser()` |
| Composable | UpperCamelCase | `fun PostCard()` |
| Variable | lowerCamelCase | `val postCount` |
| Class, interface | UpperCamelCase | `class HomeViewModel` |
| `const val` | SCREAMING_SNAKE_CASE | `const val MAX_COUNT` |
| Screen folder | lowercase | `home`, `missiondetail` |
| UI model | `XxxUiModel` | `PostUiModel` |
| Navigation | `navigateToXxx()` | `navigateToHome()` |
| Drawable | Name from the designer | |

## Parameters

```kotlin
@Composable
fun PostCard(
    title: String,                 // 1. 필수 데이터
    onClick: () -> Unit,           // 2. 필수 콜백
    modifier: Modifier = Modifier, // 3. modifier (첫 번째 기본값 파라미터)
    isLiked: Boolean = false,      // 4. 선택 파라미터
)
```

- `modifier` goes to the root layout only. Children get a new `Modifier`.

## Component Location

| Used by | Location |
| --- | --- |
| 1 screen | `presentation/<screen>/component/` |
| 2+ screens | `core/designsystem/component/` |

## Design Tokens

| Value | Use |
| --- | --- |
| Color | `SopkathonTheme.colors.xxx` |
| Text style | `SopkathonTheme.typography.xxx` |
| Figma decimal size | Round to the nearest multiple of 4 (`13.5dp` → `12.dp`) |

- `SopkathonTheme` lives in `core/designsystem/theme/` and is added when the design system is shared.
- No `Color(0xFF...)` or raw `TextStyle(...)` in screens. Add it to the theme first.

## Common

| Need | Use |
| --- | --- |
| Click without ripple | `Modifier.noRippleClickable { }` |
| Image from URL | Coil `AsyncImage(model = url, contentDescription = null)` |
| Status / navigation bar padding | Already applied: `MainNavHost` passes Scaffold `paddingValues`. Don't add `statusBarsPadding()` again |
| Text input screen | `imePadding()` + `verticalScroll(rememberScrollState())`, `clearFocusOnTap()` on the root |
| Input at the bottom of the scroll area | Add `scrollToEndOnFocus(scrollState)` before `verticalScroll(scrollState)`. Not when inputs are in the middle |
| Character count, max length | `text.graphemeLength()`, not `length` (이모지를 1자로 셈) |

## Preview

```kotlin
@ScreenPreview
@Composable
private fun XxxScreenPreview() {
    SopkathonTheme {
        XxxScreen(uiState = XxxUiState.dummy, onItemClick = {})
    }
}
```

| Item | Rule |
| --- | --- |
| Annotation | Screen: `@ScreenPreview` (360x800). Component: `@Preview(showBackground = true)` |
| Function | `private`, `<Name>Preview` |
| Screen | Preview `XxxScreen`, not `XxxRoute` |
| Theme | Wrap in `SopkathonTheme` |
| Data | `XxxUiState.dummy` |
