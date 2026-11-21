package org.sopt.sopkathon_android2.core.imageprocessing.internal

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException.Reason
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutModelState
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutFiles

internal class CutoutEngine(context: Context) {
    private val context = context.applicationContext
    private val state = MutableStateFlow(CutoutModelState.NOT_CHECKED)
    val modelState = state.asStateFlow()

    suspend fun prepareModel() = withContext(Dispatchers.IO) {
        gate.withLock {
            newSegmenter().use { prepare(it) }
        }
    }

    suspend fun process(uri: Uri, mask: suspend (Bitmap, Bitmap) -> Bitmap): Uri {
        var saved: Uri? = null
        try {
            return withContext(Dispatchers.IO) {
                processLocked(uri, mask).also { saved = it }
            }
        } catch (e: CancellationException) {
            withContext(NonCancellable + Dispatchers.IO) { saved?.let { CutoutFiles(context).delete(it) } }
            throw e
        }
    }

    private suspend fun processLocked(uri: Uri, mask: suspend (Bitmap, Bitmap) -> Bitmap): Uri =
        gate.withLock {
            try {
                newSegmenter().use { segmenter ->
                    prepare(segmenter)
                    val input = CutoutImageIO.decode(context, uri)
                    var foreground: Bitmap? = null
                    var output: Bitmap? = null
                    try {
                        currentCoroutineContext().ensureActive()
                        foreground = segmenter.process(InputImage.fromBitmap(input, 0)).awaitSettled().foregroundBitmap
                            ?: throw CutoutException(Reason.NO_FOREGROUND)
                        currentCoroutineContext().ensureActive()
                        output = mask(input, foreground)
                        currentCoroutineContext().ensureActive()
                        CutoutImageIO.save(context, output)
                    } finally {
                        if (output !== foreground && output !== input) output?.recycle()
                        if (foreground !== input) foreground?.recycle()
                        input.recycle()
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: OutOfMemoryError) {
                throw CutoutException(Reason.IMAGE_TOO_LARGE, e)
            } catch (e: CutoutException) {
                currentCoroutineContext().ensureActive()
                throw e
            } catch (e: Exception) {
                currentCoroutineContext().ensureActive()
                throw CutoutException(Reason.PROCESSING_FAILED, e)
            }
        }

    private suspend fun prepare(segmenter: SubjectSegmenter) {
        state.value = CutoutModelState.CHECKING
        if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context) != ConnectionResult.SUCCESS) {
            state.value = CutoutModelState.FAILED
            throw CutoutException(Reason.PLAY_SERVICES_UNAVAILABLE)
        }
        try {
            val installer = ModuleInstall.getClient(context)
            if (!installer.areModulesAvailable(segmenter).awaitSettled().areModulesAvailable()) {
                state.value = CutoutModelState.DOWNLOADING
                installer.installModules(ModuleInstallRequest.newBuilder().addApi(segmenter).build()).awaitSettled()
                // 설치 요청의 성공은 다운로드 완료가 아니므로 준비 상태를 다시 확인
                withTimeout(60_000) {
                    while (!installer.areModulesAvailable(segmenter).awaitSettled().areModulesAvailable()) delay(1_000)
                }
            }
            currentCoroutineContext().ensureActive()
            state.value = CutoutModelState.READY
        } catch (e: TimeoutCancellationException) {
            state.value = CutoutModelState.FAILED
            currentCoroutineContext().ensureActive()
            throw CutoutException(Reason.MODEL_NOT_READY, e)
        } catch (e: CancellationException) {
            state.value = CutoutModelState.NOT_CHECKED
            throw e
        } catch (e: Exception) {
            state.value = CutoutModelState.FAILED
            currentCoroutineContext().ensureActive()
            throw CutoutException(Reason.MODEL_NOT_READY, e)
        }
    }

    private fun newSegmenter() = SubjectSegmentation.getClient(
        SubjectSegmenterOptions.Builder().enableForegroundBitmap().build(),
    )

    companion object {
        // 화면과 처리기 인스턴스가 달라도 대형 비트맵 추론은 한 번에 하나만 실행
        private val gate = Mutex()
    }
}
