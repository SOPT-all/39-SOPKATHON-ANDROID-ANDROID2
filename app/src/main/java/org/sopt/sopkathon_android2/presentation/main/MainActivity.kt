package org.sopt.sopkathon_android2.presentation.main

import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.designsystem.theme.SopkathonTheme
import org.sopt.sopkathon_android2.presentation.main.navigation.rememberMainNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 화면 회전 등으로 다시 만들어질 때는 바로 보여 줌
        if (savedInstanceState == null) keepSplash() else hideSplashBackground()
        enableEdgeToEdge()
        setContent {
            val navigator = rememberMainNavigator()

            SopkathonTheme {
                MainScreen(
                    navigator = navigator,
                )
            }
        }
    }

    // 첫 화면 그리기를 미뤄서 그동안 스플래시 유지
    private fun keepSplash() {
        val startTime = SystemClock.uptimeMillis()
        val content = findViewById<View>(android.R.id.content)
        content.viewTreeObserver.addOnPreDrawListener(
            object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    if (SystemClock.uptimeMillis() - startTime < SPLASH_DURATION_MS) return false
                    content.viewTreeObserver.removeOnPreDrawListener(this)
                    hideSplashBackground()
                    return true
                }
            },
        )
    }

    // 안드로이드 11 이하는 창 배경이 스플래시라 화면이 뜬 뒤 흰색으로 교체
    private fun hideSplashBackground() {
        window.setBackgroundDrawableResource(R.color.white)
    }

    private companion object {
        const val SPLASH_DURATION_MS = 1000L
    }
}
