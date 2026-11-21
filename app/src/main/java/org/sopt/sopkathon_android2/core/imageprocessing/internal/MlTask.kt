package org.sopt.sopkathon_android2.core.imageprocessing.internal

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.concurrent.Executor
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// ML Kit Task는 취소할 수 없어 완료까지 입력 비트맵과 클라이언트 수명 유지
internal suspend fun <T> Task<T>.awaitSettled(): T = withContext(NonCancellable) {
    suspendCancellableCoroutine { continuation ->
        addOnCompleteListener(Executor { it.run() }) { task ->
            when {
                task.isSuccessful -> continuation.resume(task.result)
                else -> continuation.resumeWithException(task.exception ?: IllegalStateException("ML task cancelled"))
            }
        }
    }
}
