package org.sopt.sopkathon_android2.core.imageprocessing

import android.content.Context
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CutoutFiles(context: Context) {
    private val context = context.applicationContext

    suspend fun delete(uri: Uri): Boolean = withContext(Dispatchers.IO) {
        require(uri.scheme == "content" && uri.authority == "${context.packageName}.cutout.files")
        require(uri.pathSegments.size == 2 && uri.pathSegments.first() == "cutouts")
        val name = requireNotNull(uri.lastPathSegment)
        require(name.matches(Regex("[0-9a-f-]{36}\\.png")))
        File(context.filesDir, "cutouts/$name").delete()
    }
}
