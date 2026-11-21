package org.sopt.sopkathon_android2.core.imageprocessing.internal

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.Face
import com.google.mlkit.vision.face.FaceContour
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException.Reason
import kotlin.math.abs

internal object PortraitMask {
    suspend fun apply(input: Bitmap, foreground: Bitmap): Bitmap {
        val image = InputImage.fromBitmap(input, 0)
        // 윤곽 모드의 대표 얼굴만 선택되는 동작을 피하기 위한 별도 다중 얼굴 검사
        FaceDetection.getClient(FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setMinFaceSize(0.05f)
            .build()).use { detector ->
            val faces = detector.process(image).awaitSettled()
            currentCoroutineContext().ensureActive()
            if (faces.isEmpty()) throw CutoutException(Reason.NO_FACE)
            if (faces.size > 1) throw CutoutException(Reason.MULTIPLE_FACES)
        }
        return FaceDetection.getClient(FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setContourMode(FaceDetectorOptions.CONTOUR_MODE_ALL)
            .build()).use { detector ->
            val faces = detector.process(image).awaitSettled()
            currentCoroutineContext().ensureActive()
            if (faces.size != 1) throw CutoutException(if (faces.isEmpty()) Reason.NO_FACE else Reason.MULTIPLE_FACES)
            val face = faces.single()
            if (abs(face.headEulerAngleY) > 35f || abs(face.headEulerAngleX) > 30f || abs(face.headEulerAngleZ) > 40f) {
                throw CutoutException(Reason.UNSUPPORTED_POSE)
            }
            val points = face.getContour(FaceContour.FACE)?.points
                ?.takeIf { it.size == 36 } ?: throw CutoutException(Reason.FACE_CONTOUR_UNAVAILABLE)
            val eyeA = face.eyeCenter(FaceContour.LEFT_EYE)
            val eyeB = face.eyeCenter(FaceContour.RIGHT_EYE)
            val left = if (eyeA.x < eyeB.x) eyeA else eyeB
            val right = if (eyeA.x < eyeB.x) eyeB else eyeA
            val geometry = HeadGeometry(
                points.map { MaskPoint(it.x, it.y) },
                MaskPoint(face.boundingBox.exactCenterX(), face.boundingBox.exactCenterY()),
                MaskPoint(right.x - left.x, right.y - left.y),
            )
            compose(foreground, geometry)
        }
    }

    private fun Face.eyeCenter(type: Int): PointF {
        val points = getContour(type)?.points?.takeIf { it.isNotEmpty() }
            ?: throw CutoutException(Reason.FACE_CONTOUR_UNAVAILABLE)
        return PointF(points.map { it.x }.average().toFloat(), points.map { it.y }.average().toFloat())
    }

    private fun compose(foreground: Bitmap, g: HeadGeometry): Bitmap {
        val head = Path().apply {
            move(g, g.minX - g.width * 0.6f, g.minY - g.height * 0.8f)
            line(g, g.maxX + g.width * 0.6f, g.minY - g.height * 0.8f)
            line(g, g.maxX + g.width * 0.6f, g.cutoff)
            g.jaw.forEach { line(g, it.x, it.y) }
            line(g, g.minX - g.width * 0.6f, g.cutoff)
            close()
        }
        val output = Bitmap.createBitmap(foreground.width, foreground.height, Bitmap.Config.ARGB_8888)
        try {
            val canvas = Canvas(output)
            val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = android.graphics.Color.WHITE }
            canvas.drawPath(head, paint)
            paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(foreground, 0f, 0f, paint)
            return output
        } catch (e: Throwable) {
            output.recycle()
            throw e
        }
    }

    private fun Path.move(g: HeadGeometry, x: Float, y: Float) = g.world(x, y).let { moveTo(it.x, it.y) }
    private fun Path.line(g: HeadGeometry, x: Float, y: Float) = g.world(x, y).let { lineTo(it.x, it.y) }
}
