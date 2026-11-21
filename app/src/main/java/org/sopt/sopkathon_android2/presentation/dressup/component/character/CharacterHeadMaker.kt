package org.sopt.sopkathon_android2.presentation.dressup.component.character

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import androidx.core.net.toUri
import coil3.imageLoader
import coil3.request.CachePolicy
import coil3.request.ErrorResult
import coil3.request.ImageRequest
import coil3.request.SuccessResult
import coil3.request.allowHardware
import coil3.size.Precision
import coil3.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutFiles
import org.sopt.sopkathon_android2.core.imageprocessing.PortraitCutoutProcessor
import java.io.File
import java.security.MessageDigest

// 누끼 처리기가 긴 변을 1600으로 줄여서 그보다 크게 받을 필요 없음
private const val SOURCE_MAX_SIZE = 1600

// 머리 자르는 방식이 바뀌면 올려서 예전에 저장한 머리 무시
private const val CACHE_VERSION = 1

// 누끼 가장자리의 거의 투명한 픽셀은 머리 영역에서 제외
private const val ALPHA_THRESHOLD = 16

/**
 * 동료 프로필 사진으로 옷 입히기 캐릭터 머리 PNG를 만드는 객체
 *
 * 만든 머리는 사진 주소마다 캐시에 저장해 두고 다음부터 바로 돌려줌
 */
class CharacterHeadMaker(context: Context) {
    private val context = context.applicationContext
    private val portraitProcessor = PortraitCutoutProcessor(this.context)
    private val cutoutFiles = CutoutFiles(this.context)
    private val directory = File(this.context.cacheDir, "character_heads")

    /**
     * 사진에서 턱선 위 머리만 누끼로 따서 투명 여백을 잘라낸 PNG를 돌려주는 함수
     *
     * 처음에는 사진을 받고 누끼를 따느라 몇 초 걸림. 얼굴을 못 찾는 등 실패하면 예외 (`CutoutException`)
     *
     * @param photoUrl 2.1 동료 선택에서 넘겨받은 `profileImageUrl`. 갤러리 `Uri.toString()`도 가능
     * @return 캐시에 저장한 투명 PNG `Uri`. 다음에 쓰니 지우지 않음
     */
    suspend fun make(photoUrl: String): Uri = withContext(Dispatchers.IO) {
        val head = File(directory, "${cacheName(photoUrl)}.png")
        if (head.exists()) return@withContext Uri.fromFile(head)

        val source = File.createTempFile("character-head-source-", ".png", context.cacheDir)
        try {
            saveSource(photoUrl, source)
            val cutout = portraitProcessor.process(Uri.fromFile(source))
            try {
                saveTrimmed(cutout, head)
            } finally {
                withContext(NonCancellable) { cutoutFiles.delete(cutout) }
            }
        } finally {
            source.delete()
        }
        Uri.fromFile(head)
    }

    // presigned URL은 요청마다 쿼리가 달라져서 쿼리를 뺀 주소로 구분
    private fun cacheName(photoUrl: String): String {
        val address = photoUrl.toUri().buildUpon().clearQuery().fragment(null).build().toString()
        val hash = MessageDigest.getInstance("SHA-256")
            .digest(address.toByteArray())
            .joinToString("") { "%02x".format(it) }
        return "v$CACHE_VERSION-$hash"
    }

    // 누끼 처리기는 Uri만 받아서 URL 사진을 파일로 받아 둠
    private suspend fun saveSource(photoUrl: String, target: File) {
        val request = ImageRequest.Builder(context)
            .data(photoUrl)
            .size(SOURCE_MAX_SIZE)
            .precision(Precision.INEXACT)
            .allowHardware(false)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .build()
        val bitmap = when (val result = context.imageLoader.execute(request)) {
            is SuccessResult -> result.image.toBitmap()
            is ErrorResult -> throw result.throwable
        }
        target.outputStream().use { output ->
            check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)) { "프로필 사진 저장 실패" }
        }
    }

    // 누끼 결과는 원본 사진 크기라 머리가 작게 그려져서 투명 여백을 잘라냄
    private fun saveTrimmed(cutout: Uri, target: File) {
        val bitmap = context.contentResolver.openInputStream(cutout).use { BitmapFactory.decodeStream(it) }
            ?: throw IllegalStateException("누끼 이미지를 읽을 수 없음")
        val bounds = bitmap.opaqueBounds()
        val trimmed = Bitmap.createBitmap(bitmap, bounds.left, bounds.top, bounds.width(), bounds.height())
        check(directory.isDirectory || directory.mkdirs()) { "머리 저장 폴더 생성 실패" }
        // 저장 도중 끊겨도 반쯤 쓴 파일을 머리로 쓰지 않도록 다 쓴 뒤 이름 변경
        val partial = File.createTempFile(target.name, ".partial", directory)
        try {
            partial.outputStream().use { output ->
                check(trimmed.compress(Bitmap.CompressFormat.PNG, 100, output)) { "머리 저장 실패" }
            }
            check(partial.renameTo(target)) { "머리 저장 실패" }
        } finally {
            partial.delete()
            if (trimmed !== bitmap) trimmed.recycle()
            bitmap.recycle()
        }
    }

    private fun Bitmap.opaqueBounds(): Rect {
        val row = IntArray(width)
        var left = width
        var top = height
        var right = -1
        var bottom = -1
        for (y in 0 until height) {
            getPixels(row, 0, width, 0, y, width, 1)
            for (x in 0 until width) {
                if (row[x] ushr 24 > ALPHA_THRESHOLD) {
                    if (x < left) left = x
                    if (x > right) right = x
                    if (y < top) top = y
                    bottom = y
                }
            }
        }
        check(right >= 0) { "누끼 결과가 비어 있음" }
        return Rect(left, top, right + 1, bottom + 1)
    }
}
