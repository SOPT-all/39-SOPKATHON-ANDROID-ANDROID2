package org.sopt.sopkathon_android2.core.designsystem.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Test

class StickerGeometryTest {
    @Test
    fun `캔버스 밖으로 옮기면 캔버스 끝에 멈춘다`() {
        val toTopLeft = StickerGeometry.move(STICKER, Offset(-1000f, -1000f), CANVAS)
        val toBottomRight = StickerGeometry.move(STICKER, Offset(1000f, 1000f), CANVAS)

        assertEquals(Offset.Zero, toTopLeft.topLeft)
        assertEquals(Offset(CANVAS.width - 100f, CANVAS.height - 50f), toBottomRight.topLeft)
    }

    @Test
    fun `짧은 변이 최소 길이보다 작아지지 않는다`() {
        val resized = StickerGeometry.resize(STICKER, Offset(-1000f, -1000f), CANVAS, MIN_LENGTH)

        assertEquals(MIN_LENGTH, resized.height, DELTA)
        assertEquals(MIN_LENGTH * 2, resized.width, DELTA)
    }

    @Test
    fun `캔버스 끝에서 멈추고 왼쪽 위는 그대로다`() {
        val resized = StickerGeometry.resize(STICKER, Offset(10_000f, 5_000f), CANVAS, MIN_LENGTH)

        assertEquals(CANVAS.width, resized.right, DELTA)
        assertEquals(STICKER.topLeft, resized.topLeft)
    }

    private companion object {
        val CANVAS = Size(328f, 583f)
        val STICKER = Rect(Offset(100f, 100f), Size(100f, 50f))
        const val MIN_LENGTH = 40f
        const val DELTA = 0.001f
    }
}
