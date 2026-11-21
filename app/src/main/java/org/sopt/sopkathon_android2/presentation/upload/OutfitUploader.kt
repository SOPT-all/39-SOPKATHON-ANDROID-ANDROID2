package org.sopt.sopkathon_android2.presentation.upload

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import androidx.core.net.toFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.R
import org.sopt.sopkathon_android2.core.network.PresignedUrlUploader
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.dto.request.ClothesCreateRequestDto
import org.sopt.sopkathon_android2.data.dto.request.PresignedUrlRequestDto
import org.sopt.sopkathon_android2.data.dto.response.PresignedUrlResponseDto
import org.sopt.sopkathon_android2.presentation.upload.model.OutfitUploadItem
import org.sopt.sopkathon_android2.presentation.upload.model.OutfitUploadResult
import java.io.File
import java.io.IOException

/**
 * 2.2 코디 완성 때 옷과 코디 이미지를 서버에 올리는 객체
 *
 * 새 옷은 누끼 PNG를 올리고 옷 추가 API로 clothesId를 받음, 옷장에 있던 옷은 clothesId 그대로
 * 코디 이미지는 캐릭터 캡처를 무작위 방 배경에 합쳐 JPEG로 업로드
 * 피드 생성은 2.3 코디 게시하기에서 함
 */
object OutfitUploader {
    private val roomBackgrounds = listOf(
        R.drawable.img_upload_room_1,
        R.drawable.img_upload_room_2,
        R.drawable.img_upload_room_3,
        R.drawable.img_upload_room_4,
        R.drawable.img_upload_room_5,
        R.drawable.img_upload_room_6,
    )

    // 피그마 5 게시물 상세의 방(328x340)에서 캐릭터는 위 30, 높이 305
    private const val CHARACTER_TOP_RATIO = 30f / 340f
    private const val CHARACTER_HEIGHT_RATIO = 305f / 340f

    /**
     * ViewModel에서 `suspendRunCatching`으로 감싸 호출. 하나라도 실패하면 예외
     *
     * @param context 이미지를 읽고 합칠 때 사용
     * @param items 입힌 옷 목록
     * @param captureUri `StickerCanvasState.capture()`로 저장한 PNG. 2.3 썸네일로 써서 지우지 않음
     */
    suspend fun upload(
        context: Context,
        items: List<OutfitUploadItem>,
        captureUri: Uri,
    ): OutfitUploadResult = coroutineScope {
        val clothesIds = items.map { item ->
            async { item.clothesId ?: postClothes(context, item) }
        }
        val imageKey = async { uploadOutfitImage(context, captureUri) }
        OutfitUploadResult(
            imageKey = imageKey.await(),
            clothesIds = clothesIds.awaitAll(),
        )
    }

    private suspend fun postClothes(context: Context, item: OutfitUploadItem): Long {
        val cutoutUri = requireNotNull(item.cutoutUri) { "새 옷은 cutoutUri 필요" }
        val presignedUrl = postPresignedUrl(PresignedUrlUploader.PNG_CONTENT_TYPE)
        PresignedUrlUploader.uploadPng(context, presignedUrl.presignedUrl, cutoutUri)
        val clothesId = requireNotNull(
            ServiceModule.apiService.postClothes(
                ClothesCreateRequestDto(
                    name = item.name,
                    imageKey = presignedUrl.imageKey,
                    purchaseLink = item.link,
                    category = item.category,
                ),
            ).data,
        ).clothesId
        // toFile은 file:// Uri만 가능
        if (cutoutUri.scheme == ContentResolver.SCHEME_FILE) cutoutUri.toFile().delete()
        return clothesId
    }

    private suspend fun uploadOutfitImage(context: Context, captureUri: Uri): String {
        val outfitUri = drawOnRandomRoom(context, captureUri)
        try {
            val presignedUrl = postPresignedUrl(PresignedUrlUploader.CONTENT_TYPE)
            PresignedUrlUploader.upload(context, presignedUrl.presignedUrl, outfitUri)
            return presignedUrl.imageKey
        } finally {
            outfitUri.toFile().delete()
        }
    }

    private suspend fun postPresignedUrl(contentType: String): PresignedUrlResponseDto = requireNotNull(
        ServiceModule.apiService.postPresignedUrl(PresignedUrlRequestDto(contentType = contentType)).data,
    )

    // 얼굴이 흐려지지 않게 캐릭터는 원본 크기로 그리고 배경을 키움
    private suspend fun drawOnRandomRoom(context: Context, captureUri: Uri): Uri = withContext(Dispatchers.IO) {
        val room = BitmapFactory.decodeResource(context.resources, roomBackgrounds.random())
            ?: throw IOException("배경을 읽을 수 없음")
        val character = context.contentResolver.openInputStream(captureUri)?.use { BitmapFactory.decodeStream(it) }
            ?: throw IOException("캡처 이미지를 읽을 수 없음")
        val scale = character.height / (room.height * CHARACTER_HEIGHT_RATIO)
        val output = Bitmap.createBitmap(
            (room.width * scale).toInt(),
            (room.height * scale).toInt(),
            Bitmap.Config.ARGB_8888,
        )
        try {
            val paint = Paint(Paint.FILTER_BITMAP_FLAG)
            val characterTop = output.height * CHARACTER_TOP_RATIO
            val characterLeft = (output.width - character.width) / 2f
            Canvas(output).apply {
                drawBitmap(room, null, Rect(0, 0, output.width, output.height), paint)
                drawBitmap(
                    character,
                    null,
                    RectF(characterLeft, characterTop, characterLeft + character.width, characterTop + character.height),
                    paint,
                )
            }
            val file = File(context.cacheDir, "outfit${System.currentTimeMillis()}.png")
            file.outputStream().use { stream ->
                if (!output.compress(Bitmap.CompressFormat.PNG, 100, stream)) throw IOException("코디 이미지 저장 실패")
            }
            Uri.fromFile(file)
        } finally {
            room.recycle()
            character.recycle()
            output.recycle()
        }
    }
}
