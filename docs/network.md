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

## Don't / Do

| Don't | Do |
| --- | --- |
| `runCatching { }` around a suspend call | `suspendRunCatching { }` (코루틴 취소는 그대로 전달) |
| Image upload through `ApiService` | `PresignedUrlUploader` |
| Server URL in code | `BuildConfig.BASE_URL` |
| Kotlin name different from the key without `@SerialName` | `@SerialName` with the Swagger key |
| DTO in UiState | `toUiModel()` |
| Calling the API in a Composable | Call in the ViewModel |
