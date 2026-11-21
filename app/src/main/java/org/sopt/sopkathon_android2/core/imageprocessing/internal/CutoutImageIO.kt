package org.sopt.sopkathon_android2.core.imageprocessing.internal

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException
import org.sopt.sopkathon_android2.core.imageprocessing.CutoutException.Reason
import java.io.File
import java.util.UUID
import kotlin.math.max
import kotlin.math.roundToInt

internal object CutoutImageIO {
    private const val MAX_EDGE = 1600
    private const val MAX_FILE_BYTES = 64L * 1024 * 1024

    suspend fun decode(context: Context, uri: Uri): Bitmap {
        val staleBefore = System.currentTimeMillis() - 24 * 60 * 60 * 1000L
        context.cacheDir.listFiles().orEmpty()
            .filter { it.name.startsWith("cutout-input-") && it.lastModified() < staleBefore }
            .forEach { it.delete() }
        File(context.filesDir, "cutouts").listFiles().orEmpty()
            .filter { it.name.endsWith(".partial") && it.lastModified() < staleBefore }
            .forEach { it.delete() }
        val snapshot = File.createTempFile("cutout-input-", ".tmp", context.cacheDir)
        try {
            context.contentResolver.openInputStream(uri).use { input ->
                requireNotNull(input)
                snapshot.outputStream().use { output ->
                    val buffer = ByteArray(32 * 1024)
                    var total = 0L
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val count = input.read(buffer)
                        if (count < 0) break
                        total += count
                        if (total > MAX_FILE_BYTES) throw CutoutException(Reason.IMAGE_TOO_LARGE)
                        output.write(buffer, 0, count)
                    }
                }
            }
            currentCoroutineContext().ensureActive()
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(snapshot)) { decoder, info, _ ->
                    decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                    decoder.isMutableRequired = true
                    val scale = (MAX_EDGE.toFloat() / max(info.size.width, info.size.height)).coerceAtMost(1f)
                    decoder.setTargetSize(
                        (info.size.width * scale).roundToInt().coerceAtLeast(1),
                        (info.size.height * scale).roundToInt().coerceAtLeast(1),
                    )
                }
            } else {
                decodeLegacy(snapshot)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: CutoutException) {
            throw e
        } catch (e: OutOfMemoryError) {
            throw CutoutException(Reason.IMAGE_TOO_LARGE, e)
        } catch (e: Exception) {
            throw CutoutException(Reason.IMAGE_UNREADABLE, e)
        } finally {
            snapshot.delete()
        }
    }

    internal fun decodeLegacy(file: File): Bitmap {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(file.path, bounds)
        require(bounds.outWidth > 0 && bounds.outHeight > 0)
        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / sample > MAX_EDGE) sample *= 2
        val bitmap = requireNotNull(BitmapFactory.decodeFile(file.path, BitmapFactory.Options().apply {
            inSampleSize = sample
            inPreferredConfig = Bitmap.Config.ARGB_8888
        }))
        try {
            val exif = ExifInterface(file)
            val matrix = Matrix().apply {
                if (exif.isFlipped) postScale(-1f, 1f)
                postRotate(exif.rotationDegrees.toFloat())
            }
            val oriented = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            if (oriented !== bitmap) bitmap.recycle()
            return oriented
        } catch (e: Throwable) {
            bitmap.recycle()
            throw e
        }
    }

    suspend fun save(context: Context, bitmap: Bitmap): Uri {
        requireForeground(bitmap)
        val directory = File(context.filesDir, "cutouts")
        val file = File(directory, "${UUID.randomUUID()}.png")
        val partial = File(directory, "${file.name}.partial")
        try {
            check(directory.isDirectory || directory.mkdirs())
            partial.outputStream().use { output ->
                check(bitmap.compress(Bitmap.CompressFormat.PNG, 100, output))
            }
            currentCoroutineContext().ensureActive()
            check(partial.renameTo(file))
            return FileProvider.getUriForFile(context, "${context.packageName}.cutout.files", file)
        } catch (e: CancellationException) {
            file.delete()
            throw e
        } catch (e: Exception) {
            file.delete()
            throw CutoutException(Reason.SAVE_FAILED, e)
        } finally {
            partial.delete()
        }
    }

    private suspend fun requireForeground(bitmap: Bitmap) {
        val row = IntArray(bitmap.width)
        for (y in 0 until bitmap.height) {
            currentCoroutineContext().ensureActive()
            bitmap.getPixels(row, 0, bitmap.width, 0, y, bitmap.width, 1)
            if (row.any { it ushr 24 > 16 }) return
        }
        throw CutoutException(Reason.NO_FOREGROUND)
    }
}
