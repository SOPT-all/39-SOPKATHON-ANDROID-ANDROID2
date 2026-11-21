package org.sopt.sopkathon_android2.presentation.colleagueprofile

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
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.FeedServiceModule
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.local.UserPreferences
import org.sopt.sopkathon_android2.data.service.getPostsByAuthor
import org.sopt.sopkathon_android2.presentation.colleagueprofile.model.toOutfitUiModel
import org.sopt.sopkathon_android2.presentation.colleagueprofile.model.toUiModel

// 저장된 내 userId를 읽느라 AndroidViewModel
class ColleagueProfileViewModel(application: Application) : AndroidViewModel(application) {
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
                val profile = requireNotNull(ServiceModule.apiService.getUser(userId).data)
                val createdPosts = FeedServiceModule.apiService.getPostsByAuthor(
                    // 피드 조회는 보는 사람 기준이라 내 userId, 없으면 동료 기준
                    viewerId = UserPreferences(getApplication()).userId ?: userId,
                    authorId = userId,
                    postCount = profile.postCount,
                )
                profile to createdPosts
            }.onSuccess { (profile, createdPosts) ->
                _uiState.update {
                    it.copy(
                        name = profile.name,
                        rank = profile.position,
                        department = profile.department,
                        profileImageUrl = profile.profileImageUrl.orEmpty(),
                        receivedOutfits = profile.receivedOutfits.map { outfit -> outfit.toUiModel() },
                        createdOutfits = createdPosts.map { post -> post.toOutfitUiModel() },
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
