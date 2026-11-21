# Screen

## New Screen

| Step | File | Add |
| --- | --- | --- |
| 1 | `presentation/<screen>/` | `XxxContract.kt`, `XxxScreen.kt`, `XxxViewModel.kt` + `component/` (copy `feed`) |
| 2 | `MainRoute.kt` | `@Serializable data object Xxx : Route` (with args: `data class Xxx(val id: Long) : Route`) |
| 3 | `MainNavigator.kt` | `fun NavController.navigateToXxx() { navigate(Xxx) }` |
| 4 | `MainNavHost.kt` | `composable<Xxx> { XxxRoute(...) }` |
| 5 | Route of the screen that opens it | `navigateToXxx: () -> Unit` parameter |

| File | Holds |
| --- | --- |
| `XxxContract.kt` | `XxxUiState` (+ `dummy`), `XxxUiEffect` |
| `XxxScreen.kt` | `XxxRoute` + `XxxScreen` + Preview |
| `XxxViewModel.kt` | State, effects, API calls |
| `component/` | Composables used only in this screen |
| `model/` | UI models used only in this screen (`XxxUiModel`) |

## Contract

```kotlin
data class XxxUiState(
    val items: List<XxxUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val isDeleteDialogVisible: Boolean = false, // 다이얼로그 노출은 상태로
) {
    companion object {
        // Preview, 서버 연결 전에 쓰는 예시 데이터
        val dummy = XxxUiState(
            items = listOf(XxxUiModel(id = 1L, title = "예시")),
        )
    }
}

// 한 번만 일어나는 일 (화면 이동, 토스트). 없는 화면은 만들지 않음
sealed interface XxxUiEffect {
    data class NavigateToDetail(val id: Long) : XxxUiEffect
    data object ShowErrorToast : XxxUiEffect
}
```

## ViewModel

```kotlin
class XxxViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(XxxUiState())
    val uiState: StateFlow<XxxUiState> = _uiState.asStateFlow()

    // UiEffect가 있는 화면만 추가
    private val _uiEffect = Channel<XxxUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<XxxUiEffect> = _uiEffect.receiveAsFlow()

    fun onItemClick(id: Long) {
        viewModelScope.launch {
            _uiEffect.send(XxxUiEffect.NavigateToDetail(id))
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }
}
```

API calls: [network](network.md)

## Route, Screen, Preview

```kotlin
@Composable
fun XxxRoute(
    navigateToDetail: (Long) -> Unit, // 화면 이동은 NavHost에서 받은 람다로
    modifier: Modifier = Modifier,
    viewModel: XxxViewModel = viewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is XxxUiEffect.NavigateToDetail -> navigateToDetail(effect.id)
                XxxUiEffect.ShowErrorToast -> context.toast("다시 시도해주세요")
            }
        }
    }

    XxxScreen(
        uiState = uiState,
        onItemClick = viewModel::onItemClick,
        onDeleteClick = viewModel::onDeleteClick,
        modifier = modifier,
    )
}

// ViewModel 없이 상태 + 람다만 받는 순수 UI
@Composable
private fun XxxScreen(
    uiState: XxxUiState,
    onItemClick: (Long) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // ...
}

@ScreenPreview
@Composable
private fun XxxScreenPreview() {
    SopkathonTheme {
        XxxScreen(
            uiState = XxxUiState.dummy,
            onItemClick = {},
            onDeleteClick = {},
        )
    }
}
```

## NavHost

```kotlin
composable<Xxx> {
    XxxRoute(
        navigateToDetail = { id -> navigator.navController.navigateToDetail(id) },
        modifier = Modifier.padding(paddingValues),
    )
}

// 인자 있는 화면: Route가 id를 받고, 화면에서 불러옴
composable<Detail> { backStackEntry ->
    val detail = backStackEntry.toRoute<Detail>()
    DetailRoute(
        id = detail.id,
        navigateUp = navigator.navController::navigateUp,
        modifier = Modifier.padding(paddingValues),
    )
}

// DetailRoute 안
LaunchedEffect(id) { viewModel.getDetail(id) }
```

## Don't / Do

| Don't | Do |
| --- | --- |
| `navController` inside a Screen | `navigateToXxx` lambdas on the Route |
| ViewModel inside `XxxScreen` | ViewModel only in `XxxRoute` |
| Toast or navigation flag in UiState | `XxxUiEffect` |
| `MutableSharedFlow` for effects | `Channel(Channel.BUFFERED).receiveAsFlow()` |
| Dialog visibility as an effect | `isXxxDialogVisible` in UiState |
| DTO in UiState | UI model in `model/` ([network](network.md)) |
