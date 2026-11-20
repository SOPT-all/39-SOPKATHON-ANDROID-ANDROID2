package org.sopt.sopkathon_android2.presentation.main.navigation

import kotlinx.serialization.Serializable
import org.sopt.sopkathon_android2.core.navigation.Route

@Serializable
data object Home : Route

// 인자 있는 화면 예시
// @Serializable
// data class Detail(val id: Long) : Route
