package org.sopt.sopkathon_android2.presentation.colleagueprofile

import android.util.Log
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
import org.sopt.sopkathon_android2.presentation.colleagueprofile.model.toUiModel

class ColleagueProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ColleagueProfileUiState())
    val uiState: StateFlow<ColleagueProfileUiState> = _uiState.asStateFlow()

    private val _uiEffect = Channel<ColleagueProfileUiEffect>(Channel.BUFFERED)
    val uiEffect: Flow<ColleagueProfileUiEffect> = _uiEffect.receiveAsFlow()

    private var loadedUserId: Long? = null

    fun getProfile(userId: Long) {
        // 화면 회전, 재진입 시 중복 요청 방지
        if (loadedUserId == userId || _uiState.value.isLoading) return
        loadedUserId = userId

        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            suspendRunCatching {
                requireNotNull(ServiceModule.apiService.getUser(userId).data)
            }.onSuccess { profile ->
                // 동료가 만든 코디는 서버 응답에 없어 비워 둠
                _uiState.update {
                    it.copy(
                        name = profile.name,
                        rank = profile.position,
                        department = profile.department,
                        profileImageUrl = profile.profileImageUrl.orEmpty(),
                        receivedOutfits = profile.receivedOutfits.map { outfit -> outfit.toUiModel() },
                        isLoading = false,
                    )
                }
            }.onFailure { throwable ->
                Log.e("ColleagueProfileViewModel", "동료 프로필 조회 실패", throwable)
                loadedUserId = null
                _uiState.update { it.copy(isLoading = false) }
                _uiEffect.send(ColleagueProfileUiEffect.ShowToast("프로필을 불러오지 못했어요"))
            }
        }
    }
}
