package org.sopt.sopkathon_android2.core.common.util

import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.cancellation.CancellationException

/**
 * 코루틴 취소는 그대로 넘기는 runCatching 함수
 *
 * [runCatching]은 취소([CancellationException])까지 실패로 삼켜서 화면을 나가도 작업이 멈추지 않음
 * API 호출을 감쌀 때 사용
 *
 * @param block 실행할 suspend 블록
 */
suspend fun <R> suspendRunCatching(block: suspend () -> R): Result<R> =
    try {
        Result.success(block())
    } catch (e: TimeoutCancellationException) {
        // withTimeout 시간 초과는 취소가 아닌 실패로 처리
        Result.failure(e)
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // 취소된 뒤 다른 예외로 끝난 경우에도 취소 전달
        currentCoroutineContext().ensureActive()
        Result.failure(e)
    }
