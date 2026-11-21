package org.sopt.sopkathon_android2.presentation.myprofile

import android.app.Application
import android.util.Log
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
import org.sopt.sopkathon_android2.BuildConfig
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.local.UserPreferences
import org.sopt.sopkathon_android2.presentation.myprofile.model.toUiModel

// 저장된 userId를 읽느라 AndroidViewModel
class MyProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(MyProfileUiState())
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<MyProfileUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<MyProfileUiEffect> = _uiEffect.receiveAsFlow()

    init {
        getMyProfile()
    }

    fun getMyProfile() {
        if (_uiState.value.isLoading) return
        // 프로필 생성 때 저장한 값, 없으면 local.properties의 개발용 값
        val userId = UserPreferences(getApplication()).userId ?: BuildConfig.FEED_USER_ID
        if (userId <= 0) {
            _uiEffect.trySend(MyProfileUiEffect.ShowToast("프로필을 먼저 만들어주세요"))
            return
        }

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            suspendRunCatching {
                requireNotNull(ServiceModule.apiService.getUser(userId).data)
            }.onSuccess { profile ->
                // 내가 만든 코디는 서버 응답에 없어 비워 둠
                _uiState.update {
                    it.copy(
                        name = profile.name,
                        rank = profile.position,
                        department = profile.department,
                        profileImageUrl = profile.profileImageUrl.orEmpty(),
                        myOutfits = profile.receivedOutfits.map { outfit -> outfit.toUiModel() },
                        isLoading = false,
                    )
                }
            }.onFailure { throwable ->
                Log.e("MyProfileViewModel", "내 프로필 조회 실패", throwable)
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(MyProfileUiEffect.ShowToast("프로필을 불러오지 못했어요"))
            }
        }
    }
}
