package org.sopt.sopkathon_android2.presentation.myprofile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MyProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MyProfileUiState(title = "MyProfile"))
    val uiState: StateFlow<MyProfileUiState> = _uiState.asStateFlow()
}
