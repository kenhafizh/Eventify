package com.example.technical_test_indomarco.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.usecase.GetEventsPagedUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
class EventListViewModel(
    private val getEventsPagedUseCase: GetEventsPagedUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val pagingEvents: Flow<PagingData<Event>> = _searchQuery
        .debounce(SEARCH_DEBOUNCE_MS)
        .distinctUntilChanged()
        .flatMapLatest { query ->
            getEventsPagedUseCase(query).cachedIn(viewModelScope)
        }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    class Factory(private val useCase: GetEventsPagedUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return EventListViewModel(useCase) as T
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 400L
    }
}
