# Network

| Item | Where |
| --- | --- |
| Swagger | Server URL + `swagger-ui/index.html`, JSON at `v3/api-docs` |
| Response wrapper | `core/network/BaseResponse.kt` (`status`, `code`, `message`, `data`) |
| Service | `ServiceModule.apiService` |

The repository is public. Never write the server URL in code or docs.

## New API

| Step | File | Add |
| --- | --- | --- |
| 1 | `data/dto/request/XxxRequestDto.kt` | Request body, if any |
| 2 | `data/dto/response/XxxResponseDto.kt` | The `data` field of the response |
| 3 | `data/service/ApiService.kt` | Function returning `BaseResponse<XxxResponseDto>` |
| 4 | `presentation/<screen>/model/` | `XxxUiModel` + `toUiModel()` |
| 5 | `XxxViewModel.kt` | Call inside `suspendRunCatching` |

## DTO

```kotlin
@Serializable
data class XxxResponseDto(
    @SerialName("id") // Swagger 키 그대로
    val id: Long,
    @SerialName("title")
    val title: String,
    @SerialName("imageUrl")
    val imageUrl: String?, // Swagger에서 null 가능하면 ?
)
```

## ApiService

| Method | Function name |
| --- | --- |
| GET | `get<What>` |
| POST | `post<What>` |
| PUT | `put<What>` |
| PATCH | `patch<What>` |
| DELETE | `delete<What>` |

```kotlin
interface ApiService {
    @GET("/api/v1/xxx/{id}")
    suspend fun getXxx(
        @Path("id") id: Long,
    ): BaseResponse<XxxResponseDto>

    @POST("/api/v1/xxx")
    suspend fun postXxx(
        @Body request: XxxRequestDto,
    ): BaseResponse<XxxResponseDto>

    @DELETE("/api/v1/xxx/{id}")
    suspend fun deleteXxx(
        @Path("id") id: Long,
    ): BaseResponse<Unit> // data 없는 응답
}
```

## UI Model

```kotlin
// presentation/<screen>/model/XxxUiModel.kt
data class XxxUiModel(
    val id: Long,
    val title: String,
    val imageUrl: String,
)

fun XxxResponseDto.toUiModel() = XxxUiModel(
    id = id,
    title = title,
    imageUrl = imageUrl.orEmpty(),
)
```

## ViewModel

```kotlin
fun getXxx(id: Long) {
    if (_uiState.value.isLoading) return // 연타 시 중복 요청 방지

    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        suspendRunCatching { ServiceModule.apiService.getXxx(id) }
            .onSuccess { response ->
                _uiState.update { it.copy(isLoading = false, item = response.data?.toUiModel()) }
            }
            .onFailure { throwable ->
                Log.e("XxxViewModel", "조회 실패", throwable) // 에러 로그는 ViewModel에서
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(XxxUiEffect.ShowErrorToast)
            }
    }
}
```

## Image Upload

The server gives a presigned URL, and the app uploads the image to it directly.

| Step | Do |
| --- | --- |
| 1 | Get the presigned URL from the server. If it asks for a Content-Type, send `PresignedUrlUploader.CONTENT_TYPE` (`PNG_CONTENT_TYPE` for a transparent image) |
| 2 | `PresignedUrlUploader.upload(context, presignedUrl, uri)`. A transparent PNG (capture, cutout): `uploadPng(context, presignedUrl, uri)` |
| 3 | Send the image key (or URL) to the server |

`upload` resizes the image and saves it as JPEG before uploading. `uploadPng` uploads the file as it is, so the transparent background stays.

`ImageCropperState.crop()` and `StickerCanvasState.capture()` save a new file in the cache each time. Delete it after the upload succeeds: `uri.toFile().delete()`.

```kotlin
// XxxRoute
val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
    if (uri != null) viewModel.uploadImage(uri)
}

// 사진 선택 버튼 onClick
photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
```

```kotlin
// Context가 필요해서 AndroidViewModel
class XxxViewModel(application: Application) : AndroidViewModel(application) {
    fun uploadImage(uri: Uri) {
        viewModelScope.launch {
            suspendRunCatching {
                val url = requireNotNull(ServiceModule.apiService.postPresignedUrl(...).data)
                PresignedUrlUploader.upload(getApplication(), url.presignedUrl, uri)
                ServiceModule.apiService.postXxx(XxxRequestDto(imageKey = url.key))
            }.onFailure { throwable ->
                Log.e("XxxViewModel", "이미지 업로드 실패", throwable)
            }
        }
    }
}
```

## Cursor Paging

List APIs page with a cursor. Send `cursor` (`null` for the first page) and `limit`. The response `data` is `CursorResponseDto<T>`: `items`, `nextCursor`, `hasMore`. The last page has `nextCursor = null` and `hasMore = false`.

| API | Note |
| --- | --- |
| `GET /api/v1/users/{userId}/feed` | `sort`: `LATEST` or `POPULAR` |
| `GET /api/v1/users/{userId}/posts/{postId}/comments` | `X-User-Id` header required |
| `GET /api/v1/users` | Never call with an empty `keyword` (400 `INVALID_KEYWORD`) |

| Step | File | Add |
| --- | --- | --- |
| 1 | `data/dto/response/XxxResponseDto.kt` | One element of `items` |
| 2 | `data/service/ApiService.kt` | `cursor`, `limit` queries, returning `BaseResponse<CursorResponseDto<XxxResponseDto>>` |
| 3 | `XxxContract.kt` | `items`, `nextCursor`, `hasMore`, `isLoading` in UiState |
| 4 | `XxxViewModel.kt` | `getXxxList(isRefresh)` (search: [Search](#search)) |
| 5 | `XxxScreen.kt` | `LoadMoreEffect` on the state of the `LazyColumn` or `LazyVerticalGrid` |

```kotlin
// ApiService
@GET("/api/v1/xxx")
suspend fun getXxxList(
    @Query("cursor") cursor: String?, // null이면 쿼리에서 빠짐
    @Query("limit") limit: Int,
): BaseResponse<CursorResponseDto<XxxResponseDto>>
```

```kotlin
// XxxContract
data class XxxUiState(
    val items: List<XxxUiModel> = emptyList(),
    val nextCursor: String? = null,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
)
```

```kotlin
// XxxViewModel
init {
    getXxxList(isRefresh = true)
}

fun getXxxList(isRefresh: Boolean = false) {
    val state = _uiState.value
    if (state.isLoading) return // 연타, 요청 중 새로고침 무시
    if (!isRefresh && !state.hasMore) return // 마지막 페이지 뒤 요청 방지

    viewModelScope.launch {
        _uiState.update { it.copy(isLoading = true) }

        suspendRunCatching {
            requireNotNull(
                ServiceModule.apiService.getXxxList(
                    cursor = if (isRefresh) null else state.nextCursor,
                    limit = PAGE_SIZE,
                ).data,
            )
        }.onSuccess { page ->
            val newItems = page.items.map { it.toUiModel() }
            _uiState.update {
                it.copy(
                    // 인기순처럼 순서가 바뀌는 목록에서 같은 항목이 두 페이지에 와도 key 중복 크래시 방지
                    items = (if (isRefresh) newItems else it.items + newItems).distinctBy { item -> item.id },
                    nextCursor = page.nextCursor,
                    hasMore = page.hasMore,
                    isLoading = false,
                )
            }
        }.onFailure { throwable ->
            Log.e("XxxViewModel", "목록 조회 실패", throwable)
            _uiState.update { it.copy(isLoading = false) }
            _uiEffect.send(XxxUiEffect.ShowErrorToast)
        }
    }
}

companion object {
    private const val PAGE_SIZE = 20
}
```

```kotlin
// XxxScreen
val listState = rememberLazyListState()

LazyColumn(state = listState) {
    items(items = uiState.items, key = { it.id }) { item ->
        XxxItem(item = item)
    }
}

listState.LoadMoreEffect(
    items = uiState.items,
    isLoading = uiState.isLoading,
    onLoadMore = onLoadMore, // Route에서 { viewModel.getXxxList() }
)
```

A grid uses `rememberLazyGridState()`, `LazyVerticalGrid(state = gridState)`, and `gridState.LoadMoreEffect(...)` with the same arguments.

| Rule | Why |
| --- | --- |
| Pass only the server `items`, not headers or a loading item | `LoadMoreEffect` calls `onLoadMore` once per `items`. After a failure `items` stays the same, so it waits until the user scrolls away and back instead of retrying in a loop |
| Default `threshold` is 20 (one page) | The next page arrives before the user reaches the end. The second page is requested right after the first one shows |
| Give the screen a retry (button or pull to refresh) that calls `getXxxList(isRefresh = true)` | `LoadMoreEffect` never loads the first page, so a failed first page stays empty |

### Search

A new keyword has to replace a request that is still running, so search cancels the previous `Job` instead of ignoring the call.

```kotlin
// SearchViewModel
private var searchJob: Job? = null

fun searchXxx(isRefresh: Boolean = false) {
    val state = _uiState.value
    if (!isRefresh && (state.isLoading || !state.hasMore)) return // 새 검색어는 요청 중이어도 진행

    searchJob?.cancel() // 이전 검색어 응답이 새 결과를 덮는 것 방지
    searchJob = viewModelScope.launch {
        // getXxxList의 launch 블록과 같음
    }
}
```

## Don't / Do

| Don't | Do |
| --- | --- |
| `runCatching { }` around a suspend call | `suspendRunCatching { }` (코루틴 취소는 그대로 전달) |
| Image upload through `ApiService` | `PresignedUrlUploader` |
| Server URL in code | `BuildConfig.BASE_URL` |
| Kotlin name different from the key without `@SerialName` | `@SerialName` with the Swagger key |
| DTO in UiState | `toUiModel()` |
| Calling the API in a Composable | Call in the ViewModel |
