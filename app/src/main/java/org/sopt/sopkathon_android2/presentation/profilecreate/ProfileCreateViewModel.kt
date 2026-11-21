package org.sopt.sopkathon_android2.presentation.profilecreate

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
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
import org.sopt.sopkathon_android2.core.network.PresignedUrlUploader
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.dto.request.PresignedUrlRequestDto
import org.sopt.sopkathon_android2.data.dto.request.UserCreateRequestDto
import org.sopt.sopkathon_android2.data.local.UserPreferences
import org.sopt.sopkathon_android2.presentation.main.navigation.SignUpComplete
import retrofit2.HttpException

// 사진 업로드에 Context가 필요해서 AndroidViewModel
class ProfileCreateViewModel(application: Application) : AndroidViewModel(application) {
    val nameState = TextFieldState()

    private val _uiState = MutableStateFlow(ProfileCreateUiState())
    val uiState: StateFlow<ProfileCreateUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ProfileCreateUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<ProfileCreateUiEffect> = _uiEffect.receiveAsFlow()

    init {
        // 저장 버튼 활성화 판단용으로 입력값을 uiState에 반영
        viewModelScope.launch {
            snapshotFlow { nameState.text.toString() }.collect { name ->
                _uiState.update { it.copy(name = name) }
            }
        }
    }

    fun updatePhoto(uri: Uri) {
        _uiState.update { it.copy(photoUri = uri) }
    }

    fun updateDepartment(department: String) {
        _uiState.update { it.copy(department = department) }
    }

    fun updateRank(rank: String) {
        _uiState.update { it.copy(rank = rank) }
    }

    fun saveProfile() {
        val state = _uiState.value
        if (!state.isSaveEnabled) return // 연타 시 중복 요청 방지
        val photoUri = state.photoUri ?: return
        val departmentCode = ProfileCreateUiState.departmentCodes[state.department] ?: return
        val positionCode = ProfileCreateUiState.rankCodes[state.rank] ?: return

        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            suspendRunCatching {
                val presigned = requireNotNull(
                    ServiceModule.apiService.postPresignedUrl(
                        PresignedUrlRequestDto(contentType = PresignedUrlUploader.CONTENT_TYPE),
                    ).data,
                )
                PresignedUrlUploader.upload(getApplication(), presigned.presignedUrl, photoUri)
                requireNotNull(
                    ServiceModule.apiService.postUser(
                        UserCreateRequestDto(
                            name = state.name.trim(),
                            department = departmentCode,
                            position = positionCode,
                            profileImageKey = presigned.imageKey,
                        ),
                    ).data,
                )
            }.onSuccess { response ->
                UserPreferences(getApplication()).userId = response.userId
                _uiState.update { it.copy(isSaving = false) }
                _uiEffect.send(
                    ProfileCreateUiEffect.NavigateToSignUpComplete(
                        SignUpComplete(
                            name = state.name.trim(),
                            department = state.department,
                            rank = state.rank,
                            photoUri = photoUri.toString(),
                        ),
                    ),
                )
            }.onFailure { throwable ->
                Log.e("ProfileCreateViewModel", "프로필 생성 실패", throwable)
                _uiState.update { it.copy(isSaving = false) }
                val message = if (throwable is HttpException && throwable.code() == 409) {
                    "이미 프로필을 만들었어요"
                } else {
                    "프로필을 저장하지 못했어요. 다시 시도해주세요"
                }
                _uiEffect.send(ProfileCreateUiEffect.ShowToast(message))
            }
        }
    }
}
