package org.sopt.sopkathon_android2.core.common.extensions

import java.text.BreakIterator

/**
 * 화면에 보이는 글자 수를 세는 함수
 *
 * [length]는 이모지를 여러 글자로 계산 (`"👍".length` = 2). 글자 수 제한·카운터에 사용
 */
fun CharSequence.graphemeLength(): Int {
    if (isEmpty()) return 0
    val iterator = BreakIterator.getCharacterInstance()
    iterator.setText(toString())
    var count = 0
    while (iterator.next() != BreakIterator.DONE) count++
    return count
}
