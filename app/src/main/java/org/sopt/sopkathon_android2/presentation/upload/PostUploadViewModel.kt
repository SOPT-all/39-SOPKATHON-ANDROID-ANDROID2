package org.sopt.sopkathon_android2.presentation.upload

import android.content.ContentResolver
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.dto.request.PostCreateRequestDto
import org.sopt.sopkathon_android2.presentation.main.navigation.PostUpload
import org.sopt.sopkathon_android2.presentation.upload.model.PostUploadItemUiModel
import org.sopt.sopkathon_android2.presentation.upload.model.PostVisibility

class PostUploadViewModel : ViewModel() {
    val titleState = TextFieldState()
    val descriptionState = TextFieldState()

    private val _uiState = MutableStateFlow(PostUploadUiState())
    val uiState: StateFlow<PostUploadUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<PostUploadUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<PostUploadUiEffect> = _uiEffect.receiveAsFlow()

    init {
        viewModelScope.launch {
            snapshotFlow { titleState.text.toString() }.collect { title ->
                _uiState.update { it.copy(title = title) }
            }
        }
    }

    fun setOutfit(postUpload: PostUpload) {
        _uiState.update {
            it.copy(
                colleagueId = postUpload.colleagueId,
                colleagueName = postUpload.colleagueName,
                colleaguePosition = postUpload.colleaguePosition,
                imageKey = postUpload.imageKey,
                imageUri = postUpload.imageUri,
                items = postUpload.clothesIds.mapIndexed { index, clothesId ->
                    PostUploadItemUiModel(
                        clothesId = clothesId,
                        name = postUpload.itemNames[index],
                        categoryLabel = postUpload.itemCategoryLabels[index],
                        isInfoComplete = postUpload.itemInfoCompletes[index],
                    )
                },
            )
        }
    }

    fun onVisibilityClick(visibility: PostVisibility) {
        _uiState.update { it.copy(visibility = visibility) }
    }

    fun onPostClick() {
        val state = _uiState.value
        if (!state.isPostEnabled) return // 연타 시 중복 게시 방지

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            suspendRunCatching {
                ServiceModule.apiService.postPost(
                    userId = AUTHOR_ID,
                    request = PostCreateRequestDto(
                        title = titleState.text.toString().trim(),
                        imageKey = state.imageKey,
                        description = descriptionState.text.toString().trim().ifEmpty { null },
                        visibility = state.visibility.name,
                        targetUserId = state.colleagueId,
                        clothesIds = state.items.map { it.clothesId },
                    ),
                )
            }.onSuccess {
                // 화면을 나갈 때까지 isLoading 유지, 다시 누르지 못하게
                _uiEffect.send(PostUploadUiEffect.NavigateToFeed)
            }.onFailure { throwable ->
                Log.e("PostUploadViewModel", "코디 게시 실패", throwable)
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    // 2.2에서 업로드가 끝난 캡처 PNG. 썸네일로 쓰다가 2.3을 나갈 때 삭제
    override fun onCleared() {
        val imageUri = _uiState.value.imageUri.toUri()
        if (imageUri.scheme == ContentResolver.SCHEME_FILE) imageUri.toFile().delete()
    }

    companion object {
        // 서버에 1번 사용자가 없어 피드 조회와 같은 백엔드 테스트 사용자로 둔 임시 값
        private const val AUTHOR_ID = 3L
    }
}
