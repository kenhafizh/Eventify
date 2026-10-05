package com.example.technical_test_indomarco.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.model.EventSections
import com.example.technical_test_indomarco.domain.usecase.GetAllEventsUseCase
import com.example.technical_test_indomarco.utils.Helper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class HomeViewModel(
    getAllEventsUseCase: GetAllEventsUseCase
) : ViewModel() {

    val allEvents: StateFlow<List<Event>> = getAllEventsUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    val sections: StateFlow<EventSections> = allEvents
        .map { events ->
            val (upcoming, others) = Helper.separateEvents(events)
            EventSections(upcoming = upcoming, others = others)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = EventSections(upcoming = null, others = emptyList())
        )

    class Factory(private val useCase: GetAllEventsUseCase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(useCase) as T
        }
    }
}
