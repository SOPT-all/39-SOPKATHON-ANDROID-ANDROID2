package org.sopt.sopkathon_android2.core.imageprocessing.internal

import kotlin.math.hypot

internal data class MaskPoint(val x: Float, val y: Float)

internal class HeadGeometry(
    contour: List<MaskPoint>,
    origin: MaskPoint,
    rightEyeDirection: MaskPoint,
) {
    private val origin = origin
    private val magnitude = hypot(rightEyeDirection.x, rightEyeDirection.y)
    private val ux = rightEyeDirection.x / magnitude
    private val uy = rightEyeDirection.y / magnitude
    private val local = contour.map {
        val dx = it.x - origin.x
        val dy = it.y - origin.y
        MaskPoint(dx * ux + dy * uy, -dx * uy + dy * ux)
    }
    val minX = local.minOf { it.x }
    val maxX = local.maxOf { it.x }
    val minY = local.minOf { it.y }
    val maxY = local.maxOf { it.y }
    val width = maxX - minX
    val height = maxY - minY
    val cutoff = minY + height * 0.70f
    val jaw = local.filter { it.y >= cutoff }.sortedByDescending { it.x }

    init {
        require(magnitude > 0f && width > 0f && height > 0f && jaw.size >= 3)
    }

    fun world(x: Float, y: Float) = MaskPoint(origin.x + x * ux - y * uy, origin.y + x * uy + y * ux)
}
