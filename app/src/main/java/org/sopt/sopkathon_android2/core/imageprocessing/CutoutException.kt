package org.sopt.sopkathon_android2.core.imageprocessing

class CutoutException(
    val reason: Reason,
    cause: Throwable? = null,
) : Exception(reason.name, cause) {
    enum class Reason {
        PLAY_SERVICES_UNAVAILABLE,
        MODEL_NOT_READY,
        IMAGE_UNREADABLE,
        IMAGE_TOO_LARGE,
        NO_FOREGROUND,
        PROCESSING_FAILED,
        SAVE_FAILED,
    }
}

enum class CutoutModelState { NOT_CHECKED, CHECKING, DOWNLOADING, READY, FAILED }
