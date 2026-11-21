package org.sopt.sopkathon_android2.presentation.search.searchinput.model

import org.sopt.sopkathon_android2.presentation.profilecreate.ProfileCreateUiState

internal data class SearchQuery(val keyword: String?, val position: String?) {
    companion object {
        private val whitespace = Regex("\\s+")

        fun parse(text: String): SearchQuery {
            val words = text.trim().split(whitespace).filter(String::isNotEmpty)
            val position = words.lastOrNull()?.takeIf(ProfileCreateUiState.ranks::contains)
            val nameWords = if (position == null) words else words.dropLast(1)
            return SearchQuery(
                keyword = nameWords.joinToString(" ").ifEmpty { null },
                position = position,
            )
        }
    }
}
