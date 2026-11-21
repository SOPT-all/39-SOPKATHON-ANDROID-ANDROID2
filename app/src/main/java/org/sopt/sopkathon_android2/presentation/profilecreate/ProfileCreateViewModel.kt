package org.sopt.sopkathon_android2.presentation.profilecreate

import android.net.Uri
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileCreateViewModel : ViewModel() {
    val nameState = TextFieldState()

    private val _uiState = MutableStateFlow(ProfileCreateUiState())
    val uiState: StateFlow<ProfileCreateUiState> = _uiState.asStateFlow()

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
}
