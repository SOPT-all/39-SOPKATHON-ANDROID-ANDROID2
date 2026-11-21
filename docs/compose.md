# Compose

## Naming

| Target | Rule | Example |
| --- | --- | --- |
| Function | lowerCamelCase | `fun getUser()` |
| Composable | UpperCamelCase | `fun PostCard()` |
| Variable | lowerCamelCase | `val postCount` |
| Class, interface | UpperCamelCase | `class FeedViewModel` |
| `const val` | SCREAMING_SNAKE_CASE | `const val MAX_COUNT` |
| Screen folder | lowercase | `feed`, `postdetail` |
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
| Status / navigation bar padding | Already applied: `MainNavHost` passes Scaffold `paddingValues` (with the bottom bar height). Don't add `statusBarsPadding()`, `navigationBarsPadding()`, `systemBarsPadding()` in screens or in `ModalBottomSheet` content (the sheet pads its content) |
| System bar icon color | Set once in `MainActivity` `enableEdgeToEdge()` (dark icons, transparent bars). Don't change it in screens |
| Screen background other than white | In `MainNavHost`, `Modifier.background(color).then(screenModifier)` so the color fills behind the system bars |
| Text input screen | `imePadding()` + `verticalScroll(rememberScrollState())`, `clearFocusOnTap()` on the root |
| Text field in a scroll area | `Modifier.bringIntoViewOnFocus()` on the field's outer layout. Keeps the whole field and helper text above the keyboard, not only the cursor line |
| Input at the bottom of the scroll area | Add `scrollToEndOnFocus(scrollState)` before `verticalScroll(scrollState)`. Not when inputs are in the middle |
| Character count, max length | `text.graphemeLength()`, not `length` (이모지를 1자로 셈) |
| Crop an image | `ImageCropper(imageUri, aspectRatio, state = cropperState)` with `rememberImageCropperState()`. On the confirm button (enabled when `cropperState.isLoaded`), call `cropperState.crop()`. It gives a PNG `Uri` in the cache, so a cutout keeps its transparent background. Delete it after upload |
| Move and resize stickers | `StickerCanvas(rememberStickerCanvasState(), Modifier.size(<character size>)) { Sticker(key, size, initialPosition) { } }`. The canvas is the capture area, so size it to the character and put the background outside it. The sticker content uses `fillMaxSize()`. The first call is drawn at the bottom |
| Sticker selection | `onDeleteClick = { key -> }` adds the delete button on the selected sticker, `selectionAction = { key -> }` puts content under it (above it when there is no room in the canvas). Both are drawn outside the canvas and not captured, so don't clip the parent (`clipToBounds`, `verticalScroll`). Wrap stickers from a list in `key(item.key) { Sticker(...) }` |
| Image in a sticker | `AsyncImage(model = ImageRequest.Builder(context).data(uri).size(Size.ORIGINAL).build(), ...)`. Coil loads only at the first size, so an enlarged sticker gets blurry without it |
| Save the sticker canvas as an image | `stickerState.capture()` from `rememberCoroutineScope()`. It gives a transparent PNG `Uri` in the cache. Upload it with `PresignedUrlUploader.uploadPng`, then delete it |
| Snackbar (toast) | `MainScreen` provides `LocalSopkathonSnackbarState`. In `XxxRoute`, get `LocalSopkathonSnackbarState.current` and, for a `ShowXxxSnackbar` effect, call `show(SopkathonSnackbarVisuals(message, type, description, actionLabel), onActionClick = { })`. It runs in the `MainScreen` scope, so it stays on screen when you navigate right after (e.g. upload, then feed). The same snackbar is ignored while it is showing or waiting. In `XxxScreen`, put `SopkathonSnackbarHost(Modifier.align(Alignment.TopCenter))` last in the content `Box` below the top app bar. A screen without the host doesn't show it, but it still closes on time |

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
