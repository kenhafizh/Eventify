package com.example.technical_test_indomarco.domain.usecase

import androidx.paging.PagingData
import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.repository.EventRepository
import kotlinx.coroutines.flow.Flow

class GetEventsPagedUseCase(private val repository: EventRepository) {
    operator fun invoke(query: String = ""): Flow<PagingData<Event>> =
        repository.getEventsPaged(query)
}
