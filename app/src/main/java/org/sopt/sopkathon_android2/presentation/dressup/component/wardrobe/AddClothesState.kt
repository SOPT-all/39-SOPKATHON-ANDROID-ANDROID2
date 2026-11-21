package org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import androidx.core.net.toFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.core.imageprocessing.ClothingCutoutProcessor
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutFiles
import org.sopt.sopkathon_android2.core.network.PresignedUrlUploader
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.dto.request.ClothesCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.request.PresignedUrlRequestDto
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeCategory
import org.sopt.sopkathon_android2.presentation.dressup.component.wardrobe.model.WardrobeClothes
import java.io.File

private const val TAG = "AddClothesState"
private const val CAMERA_DIRECTORY = "camera"

@Stable
internal class AddClothesState(
    context: Context,
    private val scope: CoroutineScope,
    cameraFilePath: String? = null,
) {
    private val context = context.applicationContext
    private val processor = ClothingCutoutProcessor(this.context)
    private var cameraFile: File? = cameraFilePath?.let(::File)

    val cameraFilePath: String?
        get() = cameraFile?.path

    val nameState = TextFieldState()

    val linkState = TextFieldState()

    var category: WardrobeCategory? by mutableStateOf(null)

    var cutoutUri: Uri? by mutableStateOf(null)
        private set

    var isProcessing: Boolean by mutableStateOf(false)
        private set

    var isUploading: Boolean by mutableStateOf(false)
        private set

    // 서버가 purchaseLink를 빈 값으로 받지 않아 링크까지 있어야 추가 가능
    val isAddEnabled: Boolean
        get() = cutoutUri != null && category != null && linkState.text.isNotBlank() && !isProcessing && !isUploading

    // 첫 누끼 때 모델 다운로드를 기다리지 않도록 시트가 열릴 때 미리 준비
    suspend fun prepareModel() {
        suspendRunCatching { processor.prepareModel() }
            .onFailure { Log.e(TAG, "누끼 모델 준비 실패", it) }
    }

    fun newCameraUri(): Uri {
        val directory = File(context.cacheDir, CAMERA_DIRECTORY).apply { mkdirs() }
        val file = File.createTempFile("clothes-", ".jpg", directory).also { cameraFile = it }
        return FileProvider.getUriForFile(context, "${context.packageName}.cutout.files", file)
    }

    fun onCameraResult(isSaved: Boolean) {
        val file = cameraFile ?: return
        if (isSaved) {
            removeBackground(Uri.fromFile(file))
        } else {
            deleteCameraFile()
        }
    }

    fun removeBackground(photoUri: Uri) {
        if (isProcessing) return
        scope.launch {
            isProcessing = true
            suspendRunCatching {
                val source = processor.process(photoUri)
                try {
                    ClothesCutoutTrimmer.trim(context, source)
                } finally {
                    deleteCutoutSource(source)
                }
            }.onSuccess { trimmed ->
                deleteCutout()
                cutoutUri = trimmed
            }.onFailure {
                Log.e(TAG, "옷 배경 제거 실패", it)
                showToast(it.toCutoutFailMessage())
            }
            isProcessing = false
            deleteCameraFile()
        }
    }

    // 다시 고르면 이전에 자른 파일은 쓰지 않으니 삭제
    fun reselect() {
        scope.launch { deleteCutout() }
    }

    fun add(onAdded: (WardrobeClothes) -> Unit) {
        val uri = cutoutUri ?: return
        val category = category ?: return
        if (!isAddEnabled) return
        val name = nameState.text.toString().trim().ifBlank { category.label }
        val link = linkState.text.toString().trim()
        scope.launch {
            isUploading = true
            suspendRunCatching { postClothes(uri, name, link, category) }
                .onSuccess { clothesId ->
                    // 다시 불러오기 전까지 누끼 파일로 보여 주므로 지우지 않음
                    cutoutUri = null
                    onAdded(
                        WardrobeClothes(
                            clothesId = clothesId,
                            name = name,
                            imageUrl = uri.toString(),
                            purchaseLink = link,
                            category = category,
                        ),
                    )
                }.onFailure {
                    Log.e(TAG, "옷 추가 실패", it)
                    showToast("옷을 추가하지 못했어요. 다시 시도해 주세요.")
                }
            isUploading = false
        }
    }

    fun discard() {
        deleteCameraFile()
        cutoutUri?.let { uri ->
            cutoutUri = null
            scope.launch { deleteFile(uri) }
        }
    }

    private suspend fun postClothes(
        cutoutUri: Uri,
        name: String,
        link: String,
        category: WardrobeCategory,
    ): Long {
        val presignedUrl = requireNotNull(
            ServiceModule.apiService.postPresignedUrl(
                PresignedUrlRequestDto(contentType = PresignedUrlUploader.PNG_CONTENT_TYPE),
            ).data,
        )
        PresignedUrlUploader.uploadPng(context, presignedUrl.presignedUrl, cutoutUri)
        return requireNotNull(
            ServiceModule.apiService.postClothes(
                ClothesCreateRequestDto(
                    name = name,
                    imageKey = presignedUrl.imageKey,
                    purchaseLink = link,
                    category = category.name,
                ),
            ).data,
        ).clothesId
    }

    private suspend fun deleteCutout() {
        val uri = cutoutUri ?: return
        cutoutUri = null
        deleteFile(uri)
    }

    private suspend fun deleteCutoutSource(source: Uri) = withContext(NonCancellable) {
        suspendRunCatching { CutoutFiles(context).delete(source) }
            .onFailure { Log.e(TAG, "누끼 파일 삭제 실패", it) }
    }

    private suspend fun deleteFile(uri: Uri) {
        withContext(Dispatchers.IO) { uri.toFile().delete() }
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun deleteCameraFile() {
        cameraFile?.delete()
        cameraFile = null
    }
}

private fun Throwable.toCutoutFailMessage(): String = when ((this as? CutoutException)?.reason) {
    CutoutException.Reason.NO_FOREGROUND -> "옷을 찾지 못했어요. 옷이 잘 보이는 사진으로 다시 올려 주세요."
    CutoutException.Reason.MODEL_NOT_READY -> "배경 제거를 준비하고 있어요. 잠시 뒤 다시 시도해 주세요."
    CutoutException.Reason.PLAY_SERVICES_UNAVAILABLE -> "이 기기에서는 배경 제거를 쓸 수 없어요."
    CutoutException.Reason.IMAGE_TOO_LARGE -> "사진이 너무 커요. 다른 사진을 골라 주세요."
    CutoutException.Reason.IMAGE_UNREADABLE -> "사진을 읽지 못했어요. 다른 사진을 골라 주세요."
    else -> "배경을 지우지 못했어요. 다시 시도해 주세요."
}

@Composable
internal fun rememberAddClothesState(): AddClothesState {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    // 카메라 앱에 다녀오는 사이 Activity가 다시 만들어져도 찍은 사진 경로를 잃지 않게 저장
    return rememberSaveable(
        saver = Saver(
            save = { it.cameraFilePath },
            restore = { path -> AddClothesState(context, scope, path) },
        ),
    ) { AddClothesState(context, scope) }
}
