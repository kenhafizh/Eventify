package com.example.technical_test_indomarco.domain.usecase

import com.example.technical_test_indomarco.domain.model.Event
import com.example.technical_test_indomarco.domain.repository.EventRepository

class SaveEventUseCase(private val repository: EventRepository) {
    suspend operator fun invoke(event: Event) = repository.saveEvent(event)
}
