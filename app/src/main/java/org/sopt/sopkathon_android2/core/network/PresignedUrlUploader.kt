package org.sopt.sopkathon_android2.core.network

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.max
import kotlin.math.min

/**
 * presigned URL로 이미지를 S3에 바로 올리는 객체
 *
 * 업로드 순서와 ViewModel 예시는 docs/network.md의 Image Upload
 */
object PresignedUrlUploader {
    /** presigned URL을 요청할 때 보낼 Content-Type (이미지는 항상 JPEG로 바꿔 올림) */
    const val CONTENT_TYPE = "image/jpeg"

    private const val MAX_SIZE = 1920
    private const val JPEG_QUALITY = 80

    // API 클라이언트는 이미지 바이트를 로그에 전부 찍고 쓰기 제한 10초에 큰 사진이 끊겨서 분리
    private val client = OkHttpClient.Builder()
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 이미지를 줄여 JPEG로 바꾼 뒤 presigned URL에 PUT 하는 함수
     *
     * @param context ContentResolver로 이미지를 읽을 때 사용
     * @param presignedUrl 서버에서 받은 업로드 URL
     * @param uri 갤러리에서 고른 이미지
     */
    suspend fun upload(context: Context, presignedUrl: String, uri: Uri) = withContext(Dispatchers.IO) {
        val bytes = compress(context.contentResolver, uri)
        val request = Request.Builder()
            .url(presignedUrl)
            .put(bytes.toRequestBody(CONTENT_TYPE.toMediaType()))
            .build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("이미지 업로드 실패: ${response.code}")
        }
    }

    // 폰 사진(3~10MB)은 그대로 올리면 느려서 긴 변을 MAX_SIZE로 줄이고 JPEG로 재저장
    private fun compress(resolver: ContentResolver, uri: Uri): ByteArray {
        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // ImageDecoder는 사진 회전(EXIF)까지 반영
            ImageDecoder.decodeBitmap(ImageDecoder.createSource(resolver, uri)) { decoder, info, _ ->
                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                val scale = min(1f, MAX_SIZE.toFloat() / max(info.size.width, info.size.height))
                decoder.setTargetSize(
                    (info.size.width * scale).toInt().coerceAtLeast(1),
                    (info.size.height * scale).toInt().coerceAtLeast(1),
                )
            }
        } else {
            decodeLegacy(resolver, uri)
        }
        return try {
            ByteArrayOutputStream().use { output ->
                if (!bitmap.compress(Bitmap.CompressFormat.JPEG, JPEG_QUALITY, output)) throw IOException("이미지 압축 실패")
                output.toByteArray()
            }
        } finally {
            bitmap.recycle()
        }
    }

    // API 27 이하는 회전 정보 미반영
    private fun decodeLegacy(resolver: ContentResolver, uri: Uri): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, bounds) }
        var sampleSize = 1
        while (max(bounds.outWidth, bounds.outHeight) / (sampleSize * 2) >= MAX_SIZE) sampleSize *= 2
        val options = BitmapFactory.Options().apply { inSampleSize = sampleSize }
        return resolver.openInputStream(uri).use { BitmapFactory.decodeStream(it, null, options) }
            ?: throw IOException("이미지를 읽을 수 없음")
    }
}
