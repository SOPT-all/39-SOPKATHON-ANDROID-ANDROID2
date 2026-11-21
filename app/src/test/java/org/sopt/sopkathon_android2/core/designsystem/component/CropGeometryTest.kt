package org.sopt.sopkathon_android2.core.designsystem.component

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Test

private const val DELTA = 0.01f

class CropGeometryTest {
    private val geometry = CropGeometry.initial(
        bounds = Rect(0f, 0f, 900f, 1600f),
        aspectRatio = 9f / 16f,
        imageSize = Size(4000f, 3000f),
    )

    @Test
    fun `오므리면 사진이 작아지되 박스를 채우는 배율 아래로 내려가지 않는다`() {
        val zoomed = geometry.transformed(geometry.box.center, Offset.Zero, zoom = 4f)

        val zoomedOut = zoomed.transformed(zoomed.box.center, Offset.Zero, zoom = 0.5f)
        val minimum = zoomedOut.transformed(zoomed.box.center, Offset.Zero, zoom = 0.01f)

        assertEquals(geometry.scale * 2f, zoomedOut.scale, DELTA)
        assertEquals(geometry.scale, minimum.scale, DELTA)
    }

    @Test
    fun `사진을 옮겨도 박스 밖으로 사진이 빠지지 않는다`() {
        val panned = geometry.transformed(geometry.box.center, Offset(100000f, 100000f), zoom = 1f)

        assertEquals(geometry.box.left, panned.photoRect.left, DELTA)
        assertEquals(geometry.box.top, panned.photoRect.top, DELTA)
    }
}
