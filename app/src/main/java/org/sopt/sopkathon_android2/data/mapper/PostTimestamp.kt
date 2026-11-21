package org.sopt.sopkathon_android2.data.mapper

// TODO 서버가 UTC 저장 시각에 붙인 +09:00 오프셋을 수정하면 제거
internal fun String.normalizePostTimestamp(): String = when {
    endsWith("+09:00") -> removeSuffix("+09:00") + "Z"
    Regex("^\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(?:\\.\\d{1,9})?$").matches(this) -> this + "Z"
    else -> this
}
