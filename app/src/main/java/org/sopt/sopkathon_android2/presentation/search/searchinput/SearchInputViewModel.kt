package org.sopt.sopkathon_android2.presentation.search.searchinput

import android.app.Application
import android.database.sqlite.SQLiteException
import android.util.Log
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.sopt.sopkathon_android2.core.common.util.suspendRunCatching
import org.sopt.sopkathon_android2.data.di.ServiceModule
import org.sopt.sopkathon_android2.data.local.RecentSearchDatabase
import org.sopt.sopkathon_android2.presentation.search.searchinput.model.SearchQuery

class SearchInputViewModel(
    application: Application,
    private val savedStateHandle: SavedStateHandle,
) : AndroidViewModel(application) {
    private val recentSearchMutex = Mutex()
    private var searchJob: Job? = null
    private var isDebouncing = false
    private val _uiState = MutableStateFlow(
        SearchInputUiState(
            query = savedStateHandle.get<String>(QUERY_KEY).orEmpty(),
        ),
    )
    val uiState = _uiState.asStateFlow()
    val queryState = TextFieldState(initialText = _uiState.value.query)

    init {
        updateRecentQueries()
        startSearch(_uiState.value.query, debounced = false)
        viewModelScope.launch {
            snapshotFlow { queryState.text.toString().trim() }.collect(::changeQuery)
        }
    }

    private fun changeQuery(query: String) {
        if (query == _uiState.value.query) return
        savedStateHandle[QUERY_KEY] = query
        startSearch(query, debounced = true)
    }

    fun submitQuery() {
        val query = queryState.text.toString().trim()
        if (query.isEmpty()) return
        updateRecentQueries { record(query) }
        if (query != _uiState.value.query || isDebouncing) {
            savedStateHandle[QUERY_KEY] = query
            startSearch(query, debounced = false)
        }
    }

    fun selectRecentQuery(query: String) {
        queryState.setTextAndPlaceCursorAtEnd(query)
        submitQuery()
    }

    fun removeRecentQuery(query: String) {
        updateRecentQueries { remove(query) }
    }

    fun clearRecentQueries() {
        updateRecentQueries { clear() }
    }

    private fun updateRecentQueries(change: RecentSearchDatabase.() -> Unit = {}) {
        viewModelScope.launch {
            recentSearchMutex.withLock {
                try {
                    val queries = withContext(Dispatchers.IO) {
                        RecentSearchDatabase(getApplication<Application>()).use { database ->
                            database.change()
                            database.queries()
                        }
                    }
                    _uiState.update { it.copy(recentQueries = queries) }
                } catch (exception: SQLiteException) {
                    Log.e("SearchInputViewModel", "최근 검색어 처리 실패", exception)
                }
            }
        }
    }

    fun loadMore() {
        val state = _uiState.value
        val cursor = state.nextCursor ?: return
        if (state.isLoading || state.hasError) return
        requestPage(state.query, cursor)
    }

    private fun startSearch(query: String, debounced: Boolean) {
        searchJob?.cancel()
        val hasQuery = query.isNotBlank()
        isDebouncing = hasQuery && debounced
        _uiState.update {
            it.copy(
                query = query,
                results = emptyList(),
                nextCursor = null,
                isLoading = hasQuery,
                hasError = false,
            )
        }
        if (hasQuery) requestPage(query, cursor = null, debounced = debounced)
    }

    private fun requestPage(query: String, cursor: String?, debounced: Boolean = false) {
        _uiState.update { it.copy(isLoading = true, hasError = false) }
        searchJob = viewModelScope.launch {
            if (debounced) delay(SEARCH_DEBOUNCE_MS)
            isDebouncing = false
            val filters = SearchQuery.parse(query)
            suspendRunCatching {
                val response = ServiceModule.apiService.searchUsers(
                    keyword = filters.keyword,
                    position = filters.position,
                    cursor = cursor,
                )
                check(response.status in 200..299)
                requireNotNull(response.data)
            }.onSuccess { page ->
                _uiState.update { state ->
                    state.copy(
                        results = (state.results + page.items.map { it.toUiModel() }).distinctBy { it.userId },
                        nextCursor = page.nextCursor?.takeIf {
                            page.hasMore && it.isNotBlank() && it != cursor
                        },
                        isLoading = false,
                    )
                }
            }.onFailure { error ->
                Log.w("SearchInputViewModel", "동료 검색 실패", error)
                _uiState.update { it.copy(isLoading = false, hasError = true) }
            }
        }
    }

    private companion object {
        const val QUERY_KEY = "query"
        const val SEARCH_DEBOUNCE_MS = 300L
    }
}
